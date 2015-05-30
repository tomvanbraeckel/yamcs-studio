package org.yamcs.studio.ui.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.yamcs.protobuf.YamcsManagement.ProcessorInfo;
import org.yamcs.protobuf.YamcsManagement.ProcessorRequest;
import org.yamcs.protobuf.YamcsManagement.ProcessorRequest.Operation;
import org.yamcs.studio.core.YamcsPlugin;
import org.yamcs.studio.core.web.ResponseHandler;

import com.google.protobuf.MessageLite;

public class GoToBeginningHandler extends AbstractRestHandler {

    private static final Logger log = Logger.getLogger(GoToBeginningHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        if (!checkRestClient(event, "go to beginning of replay range"))
            return null;

        String processorName = YamcsPlugin.getDefault().getClientInfo().getProcessorName();
        ProcessorInfo processorInfo = YamcsPlugin.getDefault().getProcessorInfo(processorName);
        long seekTime = processorInfo.getReplayRequest().getStart();

        ProcessorRequest req = ProcessorRequest.newBuilder().setOperation(Operation.SEEK).setSeekTime(seekTime).build();
        restClient.createProcessorRequest(processorName, req, new ResponseHandler() {
            @Override
            public void onMessage(MessageLite responseMsg) {
            }

            @Override
            public void onException(Exception e) {
                log.log(Level.SEVERE, "Could not go to beginning of replay range", e);
                Display.getDefault().asyncExec(() -> {
                    MessageDialog.openError(HandlerUtil.getActiveShell(event), "Seek Error", e.getMessage());
                });
            }
        });
        return null;
    }
}
