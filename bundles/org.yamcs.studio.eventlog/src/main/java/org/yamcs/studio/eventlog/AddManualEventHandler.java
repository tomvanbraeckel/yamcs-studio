package org.yamcs.studio.ui.eventlog;

import java.util.Date;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.yamcs.protobuf.Yamcs.Event;

public class AddManualEventHandler extends AbstractHandler {

	public static final String EVENT_ADD_ACTION = "org.yamcs.studio.ui.eventlog.addEvent.action";
    private static final String EVENT_ADD = "ADD";
    private static final String EVENT_INSERT = "INSERT";
	
	
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
        EventLogView view = (EventLogView) part;
        
        String action = event.getParameter(EVENT_ADD_ACTION);
        
        if(action.equals(EVENT_ADD))
        {
        	AddManualEventDialog addManualEventDialog = new AddManualEventDialog(part.getSite().getShell());
        	int result = addManualEventDialog.open();
        }
        else{ // action.equals(EVENT_INSERT)
        	ISelection sel = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
        	
        	// get selected event and open a manual event dialog using the selected generation time.
        	if (sel != null && sel instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) sel;
                Iterator<?> it = selection.iterator();
                Event rec = (Event) it.next();
                long generationTime=rec.getGenerationTime();                
                
                AddManualEventDialog addManualEventDialog = new AddManualEventDialog(part.getSite().getShell(), generationTime);
            	int result = addManualEventDialog.open();                
        	}
        }
    	
        return null;
    }
    
    
}
