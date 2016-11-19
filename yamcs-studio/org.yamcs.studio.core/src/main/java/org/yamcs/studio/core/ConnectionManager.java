package org.yamcs.studio.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yamcs.ConfigurationException;
import org.yamcs.api.YamcsConnectionProperties;
import org.yamcs.protobuf.Rest.GetApiOverviewResponse;
import org.yamcs.protobuf.YamcsManagement.UserInfo;
import org.yamcs.security.UsernamePasswordToken;
import org.yamcs.studio.core.security.YamcsAuthorizations;
import org.yamcs.studio.core.security.YamcsCredentials;
import org.yamcs.studio.core.web.ResponseHandler;
import org.yamcs.studio.core.web.RestClient;
import org.yamcs.studio.core.web.WebSocketRegistrar;

import com.google.protobuf.MessageLite;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Handles external connections and its related state.
 *
 * @todo don't really like the use of synchronized here, we may be blocking the gui thread
 */
public class ConnectionManager {

    private static final Logger log = Logger.getLogger(ConnectionManager.class.getName());

    private Set<StudioConnectionListener> studioConnectionListeners = new HashSet<>();

    private YamcsCredentials creds;
    private ConnectionInfo connectionInfo;
    private ConnectionMode mode;
    private ConnectionStatus connectionStatus;

    // Below are not-null after connect, null again after disconnect
    private RestClient restClient;
    private WebSocketRegistrar webSocketClient;
    private String serverId;
    private String serverVersion;

    public static ConnectionManager getInstance() {
        YamcsPlugin plugin = YamcsPlugin.getDefault(); // null when workbench is closing
        return (plugin != null) ? plugin.getConnectionManager() : null;
    }

    public boolean isConnected() {
        return connectionStatus == ConnectionStatus.Connected;
    }

    public void addStudioConnectionListener(StudioConnectionListener listener) {
        synchronized (studioConnectionListeners) {
            studioConnectionListeners.add(listener);
            if (isConnected())
                listener.onStudioConnect();
        }
    }

    public void removeStudioConnectionListener(StudioConnectionListener listener) {
        synchronized (studioConnectionListeners) {
            studioConnectionListeners.remove(listener);
        }
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
        if (mode == null)
            mode = ConnectionMode.PRIMARY;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public ConnectionMode getConnectionMode() {
        return mode;
    }

    public void requestAuthenticatedUser(ResponseHandler responseHandler) {
        if (restClient != null) {
            restClient.get("/user", null, UserInfo.newBuilder(), responseHandler);
        } else {
            responseHandler.onException(new NotConnectedException());
        }
    }

    public String getYamcsInstance() {
        return connectionInfo.getConnection(mode).getInstance();
    }

    public void setYamcsInstance(String yamcsInstance) {
        // TODO should not reuse conn info field
        connectionInfo.getConnection(mode).setInstance(yamcsInstance);
    }

    public String getUsername() {
        return creds == null ? null : creds.getUsername();
    }

    public String getServerId() {
        return serverId;
    }

    public CompletableFuture<Void> connect(YamcsCredentials creds) {
        return connect(creds, mode);
    }

    /**
     * (re)establish the connection to yamcs
     */
    public CompletableFuture<Void> connect(YamcsCredentials creds, ConnectionMode mode) {
        disconnectIfConnected();
        this.creds = creds;
        this.mode = mode;

        setConnectionStatus(ConnectionStatus.Connecting);

        YamcsConnectionProperties yprops = getConnectionProperties();
        String user = creds != null ? creds.getUsername() : null;
        String pass = creds != null ? creds.getPasswordS() : null;
        if (user != null) {
            yprops.setAuthenticationToken(new UsernamePasswordToken(user, pass));
        } else {
            yprops.setAuthenticationToken(null);
        }

        restClient = new RestClient(yprops, creds);

        // Future covering both the initial rest call, and the ws conn attempt
        CompletableFuture<Void> cf = new CompletableFuture<>();

        // This 'defaultInstance' should really be moved to Server,
        // but for that we require more work on the websocket api,
        // which currently requires an instance to work with
        log.info("Retrieving server information for " + yprops.getUrl("http"));
        restClient.get("", null, GetApiOverviewResponse.newBuilder(), new ResponseHandler() {

            @Override
            public void onMessage(MessageLite responseMsg) {
                GetApiOverviewResponse response = (GetApiOverviewResponse) responseMsg;
                serverId = response.getServerId();
                serverVersion = response.getYamcsVersion();

                log.info(String.format("Detected Yamcs Server v%s (id: '%s')", serverVersion, serverId));
                if (yprops.getInstance() == null || "".equals(yprops.getInstance())) {
                    if (response.hasDefaultYamcsInstance()) {
                        yprops.setInstance(response.getDefaultYamcsInstance());
                    } else {
                        Exception ex = new ConfigurationException(
                                "No 'instance' was specified, and no "
                                        + "default instance was configured on Yamcs Server.");

                        // TODO get rid of this, use only future
                        onWebSocketConnectionFailed(ex);
                        cf.completeExceptionally(ex);
                        return;
                    }
                }

                log.info("Connecting WebSocket to instance " + yprops.getInstance());
                webSocketClient = new WebSocketRegistrar(yprops);
                webSocketClient.connect().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            cf.complete(null);
                        } else {
                            cf.completeExceptionally(future.cause());
                        }
                    }
                });
            }

            @Override
            public void onException(Exception e) {
                onWebSocketConnectionFailed(e);
                cf.completeExceptionally(e);
            }
        });

        return cf;
    }

    public void disconnectIfConnected() {
        boolean doDisconnect = false;
        synchronized (this) {
            doDisconnect = (connectionStatus != null)
                    && (connectionStatus != ConnectionStatus.Disconnected);
        }
        if (doDisconnect)
            disconnect();
    }

    public void disconnect() {
        log.info("Start disconnect procedure (current state: " + connectionStatus + ")");
        synchronized (this) {
            if (connectionStatus == ConnectionStatus.Disconnected
                    || connectionStatus == ConnectionStatus.Disconnecting
                    || connectionStatus == ConnectionStatus.ConnectionFailure)
                return;

            setConnectionStatus(ConnectionStatus.Disconnecting);
        }

        log.info("Shutting down WebSocket client");
        if (webSocketClient != null)
            webSocketClient.shutdown();

        webSocketClient = null;

        log.info("Shutting down REST client");
        if (restClient != null)
            restClient.shutdown();

        restClient = null;

        log.info("Notify downstream components of Studio disconnect");
        synchronized (studioConnectionListeners) {
            for (StudioConnectionListener scl : studioConnectionListeners) {
                log.info(String.format(" -> Inform %s", scl.getClass().getSimpleName()));
                try {
                    scl.onStudioDisconnect();
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Unable to disconnect listener " + scl + ".", e);
                }
            }
        }

        serverId = null;
        serverVersion = null;
        setConnectionStatus(ConnectionStatus.Disconnected);
    }

    public void onWebSocketConnected() {
        log.fine("WebSocket connected");
        YamcsAuthorizations.getInstance().loadAuthorizations();

        setConnectionStatus(ConnectionStatus.Connected);
        synchronized (studioConnectionListeners) {
            studioConnectionListeners.forEach(l -> l.onStudioConnect());
        }
    }

    public void onWebSocketConnectionFailed(Throwable t) {
        log.severe("Could not connect: " + t.getMessage());
        synchronized (this) {
            setConnectionStatus(ConnectionStatus.ConnectionFailure);
        }
        synchronized (studioConnectionListeners) {
            studioConnectionListeners.forEach(l -> l.onStudioConnectionFailure(t));
        }
    }

    public void switchNode() {
        if (mode == ConnectionMode.PRIMARY) {
            log.info("Switching to failover server");
            mode = ConnectionMode.FAILOVER;
        } else {
            log.info("Switching back to primary server");
            mode = ConnectionMode.PRIMARY;
        }

        disconnect();
        connect(creds, mode);
    }

    public void onWebSocketDisconnected() {
        disconnect();
    }

    public boolean isPrivilegesEnabled() {
        // TODO we should probably control this from the server, rather than here. Just because
        // the creds are null, does not really mean anything. We could also send creds to an
        // unsecured yamcs server. It would just ignore it, and then our client state would
        // be wrong
        return creds != null;
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public WebSocketRegistrar getWebSocketClient() {
        return webSocketClient;
    }

    public YamcsConnectionProperties getConnectionProperties() {
        return connectionInfo.getConnection(mode);
    }

    private void setConnectionStatus(ConnectionStatus connectionStatus) {
        log.info(String.format("[%s] %s", mode, connectionStatus));
        this.connectionStatus = connectionStatus;
    }

    public void shutdown() {
        if (restClient != null)
            restClient.shutdown();
        if (webSocketClient != null)
            webSocketClient.shutdown();
    }
}
