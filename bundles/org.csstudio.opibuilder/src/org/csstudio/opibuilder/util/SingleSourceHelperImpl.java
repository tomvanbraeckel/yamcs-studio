package org.csstudio.opibuilder.util;

import java.util.Objects;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.PrintDisplayAction;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIShell;
import org.csstudio.opibuilder.widgetActions.OpenFileAction;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

public class SingleSourceHelperImpl extends SingleSourceHelper {

    @Override
    protected GC iGetImageGC(Image image) {
        return new GC(image);
    }

    /**
     * Open the file in its associated editor, supporting workspace files, local file system files or URLs
     * 
     * @param openFileAction
     *            Action for opening a file
     */
    @Override
    protected void iOpenFileActionRun(final OpenFileAction openFileAction) {
        UIJob job = new UIJob(openFileAction.getDescription()) {
            @Override
            public IStatus runInUIThread(final IProgressMonitor monitor) {
                // Open editor on new file.
                IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (dw == null)
                    return Status.OK_STATUS; // Not really OK..
                try {
                    IWorkbenchPage page = Objects.requireNonNull(dw.getActivePage());

                    IPath absolutePath = openFileAction.getPath();
                    if (!absolutePath.isAbsolute())
                        absolutePath = ResourceUtil.buildAbsolutePath(
                                openFileAction.getWidgetModel(), absolutePath);

                    // Workspace file?
                    IFile file = ResourceUtilSSHelperImpl.getIFileFromIPath(absolutePath);
                    if (file != null) { // Clear the last-used-editor info to always get the default editor,
                                        // the one configurable via Preferences, General, Editors, File Associations,
                                        // and not whatever one user may have used last via Navigator's "Open With..".
                                        // Other cases below use a new, local file that won't have last-used-editor
                                        // info, yet
                        file.setPersistentProperty(IDE.EDITOR_KEY, null);
                        IDE.openEditor(page, file, true);
                    } else if (ResourceUtil.isExistingLocalFile(absolutePath)) { // Local file system
                        try {
                            IFileStore localFile = EFS.getLocalFileSystem().getStore(absolutePath);
                            IDE.openEditorOnFileStore(page, localFile);
                        } catch (Exception e) {
                            throw new Exception("Cannot open local file system location " + openFileAction.getPath(),
                                    e);
                        }
                    }
                } catch (Exception e) {
                    final String message = "Failed to open file " + openFileAction.getPath();
                    ExceptionDetailsErrorDialog.openError(dw.getShell(), "Failed to open file", message, e);
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
                    ConsoleService.getInstance().writeError(message);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    protected void iAddPaintListener(Control control, PaintListener paintListener) {
        control.addPaintListener(paintListener);

    }

    @Override
    protected void iRemovePaintListener(Control control, PaintListener paintListener) {
        control.removePaintListener(paintListener);

    }

    @Override
    protected void iRegisterRCPRuntimeActions(ActionRegistry actionRegistry, IOPIRuntime opiRuntime) {
        actionRegistry.registerAction(new PrintDisplayAction(opiRuntime));
    }

    @Override
    protected void iappendRCPRuntimeActionsToMenu(
            ActionRegistry actionRegistry, IMenuManager menu) {
        IAction action = actionRegistry.getAction(ActionFactory.PRINT.getId());
        if (action != null) {
            menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        }

    }

    @Override
    protected IPath iRcpGetPathFromWorkspaceFileDialog(IPath startPath, String[] extensions) {
        ResourceSelectionDialog rsDialog = new ResourceSelectionDialog(
                Display.getCurrent().getActiveShell(), "Choose File", extensions);
        if (startPath != null)
            rsDialog.setSelectedResource(startPath);

        if (rsDialog.open() == Window.OK) {
            return rsDialog.getSelectedResource();
        }
        return null;
    }

    @Override
    protected void iOpenEditor(IWorkbenchPage page, IPath path) throws Exception {
        SingleSourcePlugin.getUIHelper().openEditor(page, path);
    }

    @Override
    protected void iOpenOPIShell(IPath path, MacrosInput input) {
        OPIShell.openOPIShell(path, input);
    }

    @Override
    protected IOPIRuntime iGetOPIShellForShell(Shell shell) {
        return OPIShell.getOPIShellForShell(shell);
    }
}
