package org.yamcs.studio.core.client;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.yamcs.YamcsException;
import org.yamcs.api.YamcsConnectionProperties;
import org.yamcs.api.rest.BulkRestDataReceiver;
import org.yamcs.api.rest.RestClient;
import org.yamcs.api.ws.ConnectionListener;
import org.yamcs.api.ws.WebSocketClient;
import org.yamcs.api.ws.WebSocketClientCallback;
import org.yamcs.api.ws.WebSocketRequest;
import org.yamcs.protobuf.Rest.GetApiOverviewResponse;
import org.yamcs.protobuf.Web.ConnectionInfo;
import org.yamcs.protobuf.Web.WebSocketServerMessage.WebSocketReplyData;
import org.yamcs.protobuf.Web.WebSocketServerMessage.WebSocketSubscriptionData;
import org.yamcs.protobuf.YamcsManagement.YamcsInstance;
import org.yamcs.studio.core.YamcsPlugin;

import com.google.protobuf.MessageLite;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.HttpMethod;

/**
 * Provides passage to Yamcs. This covers both the REST and WebSocket API.
 * 
 * TODO currently reconnection can only be cancelled on initial connect. We should also make it cancellable (via Job UI)
 * on auto reconnect.
 */
public class YamcsStudioClient implements WebSocketClientCallback {

    private static final Logger log = Logger.getLogger(YamcsStudioClient.class.getName());

    // WebSocketClient max frame payload length, otherwise we get "frame length 65535 exceeded" error for displays with
    // many parameters
    private static final int MAX_FRAME_PAYLOAD_LENGTH = 10 * 1024 * 1024;

    private YamcsConnectionProperties yprops;
    private String caCertFile;
    private String application;

    private volatile boolean connecting;
    private volatile boolean connected;

    private RestClient restClient;
    private WebSocketClient wsclient;

    private boolean retry = true;
    private boolean reconnecting = false;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private List<YamcsInstance> instances;

    private List<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<>();
    private List<WebSocketClientCallback> subscribers = new CopyOnWriteArrayList<>();

    private ParameterSubscriptionBundler parameterSubscriptionBundler;

    private ConnectionInfo connectionInfo;

    // Keep track of ongoing jobs, to respond to user cancellation requests.
    private ScheduledExecutorService canceller = Executors.newSingleThreadScheduledExecutor();
    private Map<IProgressMonitor, Future<?>> cancellableJobs = new ConcurrentHashMap<>();

    public YamcsStudioClient(String application, boolean retry) {
        this.application = application;
        this.retry = retry;

        canceller.scheduleWithFixedDelay(() -> {
            cancellableJobs.forEach((monitor, future) -> {
                if (monitor.isCanceled()) {
                    future.cancel(true);
                    cancellableJobs.remove(monitor);
                } else if (future.isDone()) {
                    cancellableJobs.remove(monitor);
                }
            });
        }, 2000, 1000, TimeUnit.MILLISECONDS);

        parameterSubscriptionBundler = new ParameterSubscriptionBundler(this);
        executor.scheduleWithFixedDelay(parameterSubscriptionBundler, 200, 400, TimeUnit.MILLISECONDS);
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        this.connectionListeners.add(connectionListener);
    }

    private FutureTask<YamcsConnectionProperties> doConnect() {
        if (connected) {
            disconnect();
        }

        restClient = new RestClient(yprops);
        restClient.setInsecureTls(true);
        restClient.setAutoclose(false);
        if (caCertFile != null) {
            try {
                restClient.setCaCertFile(caCertFile);
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        wsclient = new WebSocketClient(yprops, this);
        wsclient.setUserAgent(application);
        wsclient.setInsecureTls(true);
        wsclient.enableReconnection(true);
        wsclient.setMaxFramePayloadLength(MAX_FRAME_PAYLOAD_LENGTH);
        if (caCertFile != null) {
            try {
                wsclient.setCaCertFile(caCertFile);
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        FutureTask<YamcsConnectionProperties> future = new FutureTask<>(() -> {
            log.info("Connecting to " + yprops);
            int maxAttempts = 10;
            try {
                if (reconnecting && !retry) {
                    log.warning("Retries are disabled, cancelling reconnection");
                    reconnecting = false;
                    return;
                }

                connecting = true;
                connecting();
                for (int i = 0; i < maxAttempts; i++) {
                    try {
                        log.fine(String.format("Connecting to %s attempt %d", yprops, i));
                        GetApiOverviewResponse serverInfo = restClient.doRequest("", HttpMethod.GET)
                                .thenApply(b -> {
                                    try {
                                        return GetApiOverviewResponse.parseFrom(b);
                                    } catch (Exception e) {
                                        throw new CompletionException(e);
                                    }
                                }).get(5, TimeUnit.SECONDS);
                        String defaultInstanceName = null;
                        if (serverInfo.hasDefaultYamcsInstance()) {
                            defaultInstanceName = serverInfo.getDefaultYamcsInstance();
                        }

                        instances = restClient.blockingGetYamcsInstances();
                        if (instances == null || instances.isEmpty()) {
                            log.warning("No configured yamcs instance");
                            return;
                        }

                        if (defaultInstanceName == null) {
                            defaultInstanceName = instances.get(0).getName();
                        }
                        String instanceName = defaultInstanceName;
                        if (yprops.getInstance() != null) { // check if the instance saved in properties exists,
                                                            // otherwise use the default one
                            instanceName = instances.stream().map(yi -> yi.getName())
                                    .filter(s -> s.equals(yprops.getInstance()))
                                    .findFirst()
                                    .orElse(defaultInstanceName);
                        }
                        yprops.setInstance(instanceName);

                        ChannelFuture future1 = wsclient.connect();
                        future1.get(5000, TimeUnit.MILLISECONDS);
                        // now the TCP connection is established but we have to wait for the websocket to be setup
                        // the connected callback will handle that

                        return;
                    } catch (Exception e1) {
                        // For anything other than a security exception, re-try
                        if (log.isLoggable(Level.FINE)) {
                            log.log(Level.FINE, String.format("Connection to %s failed (attempt %d of %d)",
                                    yprops, i + 1, maxAttempts), e1);
                        } else {
                            log.warning(String.format("Connection to %s failed: %s (attempt %d of %d)",
                                    yprops, e1.getMessage(), i + 1, maxAttempts));
                        }
                        Thread.sleep(5000);
                    }
                }
                connecting = false;
                for (ConnectionListener cl1 : connectionListeners) {
                    cl1.connectionFailed(null,
                            new YamcsException(maxAttempts + " connection attempts failed, giving up."));
                }
                log.warning(maxAttempts + " connection attempts failed, giving up.");
            } catch (InterruptedException e2) {
                log.info("Connection cancelled by user");
                connecting = false;
                for (ConnectionListener cl2 : connectionListeners) {
                    cl2.connectionFailed(null, new YamcsException("Thread interrupted", e2));
                }
            }
        }, yprops);
        executor.submit(future);

        // Add Progress indicator in status bar
        String jobName = "Connecting to " + yprops;
        scheduleAsJob(jobName, future, Job.SHORT);

        return future;
    }

    @Override
    public void connecting() {
        for (ConnectionListener cl : connectionListeners) {
            cl.connecting(null);
        }
    }

    @Override
    public void connected() {
        log.info("Connected to " + yprops);
        parameterSubscriptionBundler.clearQueue();

        connected = true;
        for (ConnectionListener listener : connectionListeners) {
            listener.connected(null);
        }
    }

    @Override
    public void disconnected() {
        if (connected) {
            log.warning("Connection to " + yprops + " lost");
        }
        connected = false;
        connectionInfo = null;
        for (ConnectionListener listener : connectionListeners) {
            listener.disconnected();
        }
    }

    public void disconnect() {
        log.info("Disconnecting from " + yprops);
        if (!connected) {
            return;
        }
        wsclient.disconnect();
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public Future<YamcsConnectionProperties> connect(YamcsConnectionProperties yprops) {
        this.yprops = yprops;
        return doConnect();
    }

    public YamcsConnectionProperties getYamcsConnectionProperties() {
        return yprops;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public List<String> getYamcsInstances() {
        if (instances == null) {
            return null;
        }
        return instances.stream().map(r -> r.getName()).collect(Collectors.toList());
    }

    public CompletableFuture<WebSocketReplyData> subscribe(WebSocketRequest req,
            WebSocketClientCallback messageHandler) {
        if (!subscribers.contains(messageHandler)) {
            subscribers.add(messageHandler);
        }
        if (req instanceof ParameterWebSocketRequest) {
            parameterSubscriptionBundler.queue((ParameterWebSocketRequest) req);
            return null; // TODO ?
        } else {
            return wsclient.sendRequest(req);
        }
    }

    public CompletableFuture<WebSocketReplyData> sendWebSocketMessage(WebSocketRequest req) {
        if (req instanceof ParameterWebSocketRequest) {
            parameterSubscriptionBundler.queue((ParameterWebSocketRequest) req);
            return null; // TODO ?
        } else {
            return wsclient.sendRequest(req);
        }
    }

    @Override
    public void onMessage(WebSocketSubscriptionData data) {

        // Stop processing messages on shutdown
        YamcsPlugin plugin = YamcsPlugin.getDefault();
        if (plugin == null) {
            return;
        }

        if (data.hasConnectionInfo()) {
            connectionInfo = data.getConnectionInfo();
        }

        subscribers.forEach(s -> s.onMessage(data));
    }

    public CompletableFuture<byte[]> get(String uri, MessageLite msg) {
        return requestAsync(HttpMethod.GET, uri, msg);
    }

    public CompletableFuture<Void> streamGet(String uri, MessageLite msg, BulkRestDataReceiver receiver) {
        return doRequestWithDelimitedResponse(HttpMethod.GET, uri, msg, receiver);
    }

    public CompletableFuture<byte[]> post(String uri, MessageLite msg) {
        return requestAsync(HttpMethod.POST, uri, msg);
    }

    public CompletableFuture<byte[]> patch(String uri, MessageLite msg) {
        return requestAsync(HttpMethod.PATCH, uri, msg);
    }

    public CompletableFuture<byte[]> put(String uri, MessageLite msg) {
        return requestAsync(HttpMethod.PUT, uri, msg);
    }

    public CompletableFuture<byte[]> delete(String uri, MessageLite msg) {
        return requestAsync(HttpMethod.DELETE, uri, msg);
    }

    private <S extends MessageLite> CompletableFuture<byte[]> requestAsync(HttpMethod method, String uri,
            MessageLite requestBody) {
        CompletableFuture<byte[]> cf;
        if (requestBody != null) {
            cf = restClient.doRequest(uri, method, requestBody.toByteArray());
        } else {
            cf = restClient.doRequest(uri, method);
        }

        String jobName = method + " /api" + uri;
        scheduleAsJob(jobName, cf, Job.SHORT);
        return cf;
    }

    private <S extends MessageLite> CompletableFuture<Void> doRequestWithDelimitedResponse(HttpMethod method,
            String uri, MessageLite requestBody, BulkRestDataReceiver receiver) {
        CompletableFuture<Void> cf;
        if (requestBody != null) {
            cf = restClient.doBulkGetRequest(uri, requestBody.toByteArray(), receiver);
        } else {
            cf = restClient.doBulkGetRequest(uri, receiver);
        }

        String jobName = method + " /api" + uri;
        scheduleAsJob(jobName, cf, Job.LONG);
        return cf;
    }

    private void scheduleAsJob(String jobName, Future<?> cf, int priority) {
        Job job = Job.create(jobName, monitor -> {
            cancellableJobs.put(monitor, cf);

            try {
                cf.get();
                return Status.OK_STATUS;
            } catch (CancellationException | InterruptedException e) {
                return Status.CANCEL_STATUS;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                log.log(Level.SEVERE, "Exception while executing job '" + jobName + "': " + cause.getMessage(),
                        cause);
                return Status.OK_STATUS;
            }
        });
        job.setPriority(Job.LONG);
        job.schedule();
    }

    public WebSocketClient getWebSocketClient() {
        return wsclient;
    }

    /**
     * Performs an orderly shutdown of this service
     */
    public void shutdown() {
        if (restClient != null) {
            restClient.close(); // Shuts down the thread pool
        }
        if (wsclient != null) {
            wsclient.shutdown();
        }
        executor.shutdown();
        canceller.shutdown();
    }
}
