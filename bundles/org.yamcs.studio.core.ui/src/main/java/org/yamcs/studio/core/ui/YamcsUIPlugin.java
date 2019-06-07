package org.yamcs.studio.core.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.framework.BundleContext;
import org.yamcs.protobuf.Web.ConnectionInfo;
import org.yamcs.protobuf.YamcsManagement.ClientInfo;
import org.yamcs.protobuf.YamcsManagement.ProcessorInfo;
import org.yamcs.protobuf.YamcsManagement.Statistics;
import org.yamcs.studio.core.model.ManagementCatalogue;
import org.yamcs.studio.core.model.ManagementListener;
import org.yamcs.studio.core.model.TimeCatalogue;
import org.yamcs.studio.core.ui.connections.ConnectionPreferences;
import org.yamcs.studio.core.ui.prefs.DateFormatPreferencePage;
import org.yamcs.studio.core.ui.processor.ProcessorStateProvider;
import org.yamcs.studio.core.ui.utils.RCPUtils;
import org.yamcs.utils.TimeEncoding;

public class YamcsUIPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.yamcs.studio.core.ui";

    private static YamcsUIPlugin plugin;

    public static final String CMD_CONNECT = "org.yamcs.studio.core.ui.connect";

    private SimpleDateFormat format;
    private SimpleDateFormat tzFormat;

    // private EventLogViewActivator eventLogActivator; // TODO remove? very annoying actually

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);

        plugin = this;
        TimeEncoding.setUp();
        ConnectionUIHelper.getInstance();

        // TODO should maybe move this to eventlog-plugin, but verify lazy behaviour
        // eventLogActivator = new EventLogViewActivator(); // TODO remove? very annoying actually

        // Warning to future self: don't access preference store here. It triggers before workspace selection, causing
        // chaos.
    }

    public void setDateFormat(String pattern) {
        format = new SimpleDateFormat(pattern, Locale.US);
        tzFormat = new SimpleDateFormat(pattern + " Z", Locale.US);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static YamcsUIPlugin getDefault() {
        return plugin;
    }

    /**
     * Formats a Yamcs instant. Timezone information is not added. Must be called on SWT thread due to reuse of
     * dateformatter.
     */
    public String formatInstant(long instant) {
        return formatInstant(instant, false);
    }

    /**
     * Formats a Yamcs instant. Must be called on SWT thread due to reuse of dateformatter.
     * 
     * @param tzOffset
     *            whether timezone offset is added to the output string.
     */
    public String formatInstant(long instant, boolean tzOffset) {
        if (format == null) {
            IPreferenceStore store = getPreferenceStore();
            String pattern = store.getString(DateFormatPreferencePage.PREF_DATEFORMAT);
            setDateFormat(pattern);
        }
        // TODO Improve this. Don't use Date
        Calendar cal = TimeEncoding.toCalendar(instant);
        cal.setTimeZone(TimeCatalogue.getInstance().getTimeZone());
        if (tzOffset) {
            tzFormat.setTimeZone(cal.getTimeZone());
            return tzFormat.format(cal.getTime());
        } else {
            format.setTimeZone(cal.getTimeZone());
            return format.format(cal.getTime());
        }
    }

    /**
     * Hook called by application layer to bootstrap connection state
     */
    public void postWorkbenchStartup(IWorkbench workbench) {
        // Listen to processing-info updates
        doUpdateGlobalProcessingState(workbench, null); // Trigger initial state
        ManagementCatalogue.getInstance().addManagementListener(new ManagementListener() {
            @Override
            public void processorUpdated(ProcessorInfo processorInfo) {
                updateGlobalProcessingState(processorInfo);
            }

            @Override
            public void statisticsUpdated(Statistics stats) {
            }

            @Override
            public void clientUpdated(ClientInfo clientInfo) {
            }

            @Override
            public void clientDisconnected(ClientInfo clientInfo) {
            }

            @Override
            public void instanceUpdated(ConnectionInfo connectionInfo) {
                IWorkbench workbench = PlatformUI.getWorkbench();
                workbench.getDisplay().asyncExec(() -> {
                    if (connectionInfo.hasProcessor()) {
                        doUpdateGlobalProcessingState(workbench, connectionInfo.getProcessor());
                    } else {
                        doUpdateGlobalProcessingState(workbench, null);
                    }
                });
            }

            @Override
            public void clearAllManagementData() {
            }
        });

        // Request connection to Yamcs server
        boolean singleConnectionMode = getPreferenceStore().getBoolean("singleConnectionMode");
        if (!singleConnectionMode && ConnectionPreferences.isAutoConnect()) {
            RCPUtils.runCommand("org.yamcs.studio.core.ui.autoconnect");
        }
    }

    private void updateGlobalProcessingState(ProcessorInfo processor) {
        // First update state of various buttons (at the level of the workbench)
        IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(() -> {
            ClientInfo clientInfo = ManagementCatalogue.getInstance().getCurrentClientInfo();
            if (clientInfo != null && clientInfo.getProcessorName().equals(processor.getName())) {
                doUpdateGlobalProcessingState(workbench, processor);
            }
        });
    }

    private void doUpdateGlobalProcessingState(IWorkbench workbench, ProcessorInfo processorInfo) {
        ISourceProviderService service = (ISourceProviderService) workbench.getService(ISourceProviderService.class);
        if (service != null) {
            ProcessorStateProvider state = (ProcessorStateProvider) service
                    .getSourceProvider(ProcessorStateProvider.STATE_KEY_PROCESSING);
            state.updateState(processorInfo);
        }
    }

    public ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
