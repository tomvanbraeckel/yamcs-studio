package org.yamcs.studio.core.ui.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.yamcs.protobuf.Rest.ListProcessorsResponse;
import org.yamcs.protobuf.YamcsManagement.ProcessorInfo;
import org.yamcs.studio.core.model.ManagementCatalogue;

import com.google.protobuf.InvalidProtocolBufferException;

public class SwitchProcessorDialog extends TitleAreaDialog {

    private static final Logger log = Logger.getLogger(SwitchProcessorDialog.class.getName());

    private TableViewer processorsTable;
    private ProcessorInfo processorInfo;

    public SwitchProcessorDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Choose a different processor");
        // setMessage("informative message");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite composite = new Composite(area, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setLayout(new GridLayout());

        Composite tableWrapper = new Composite(composite, SWT.NONE);
        TableColumnLayout tcl = new TableColumnLayout();
        tableWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));
        tableWrapper.setLayout(tcl);

        processorsTable = new TableViewer(tableWrapper, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        processorsTable.getTable().setHeaderVisible(true);
        processorsTable.getTable().setLinesVisible(true);

        TableViewerColumn instanceColumn = new TableViewerColumn(processorsTable, SWT.NONE);
        instanceColumn.getColumn().setText("Instance");
        instanceColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ProcessorInfo info = (ProcessorInfo) element;
                return info.getInstance();
            }
        });
        tcl.setColumnData(instanceColumn.getColumn(), new ColumnPixelData(100));

        TableViewerColumn nameColumn = new TableViewerColumn(processorsTable, SWT.NONE);
        nameColumn.getColumn().setText("Processor");
        nameColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ProcessorInfo info = (ProcessorInfo) element;
                return info.getName();
            }
        });
        tcl.setColumnData(nameColumn.getColumn(), new ColumnWeightData(100));

        TableViewerColumn typeColumn = new TableViewerColumn(processorsTable, SWT.NONE);
        typeColumn.getColumn().setText("Type");
        typeColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ProcessorInfo info = (ProcessorInfo) element;
                return info.getType();
            }
        });
        tcl.setColumnData(typeColumn.getColumn(), new ColumnPixelData(100));

        processorsTable.setContentProvider(ArrayContentProvider.getInstance());
        processorsTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        processorsTable.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 == null || e2 == null) {
                    return 0;
                }
                ProcessorInfo p1 = (ProcessorInfo) e1;
                ProcessorInfo p2 = (ProcessorInfo) e2;
                int c = p1.getInstance().compareTo(p2.getInstance());
                return (c != 0) ? c : p1.getName().compareTo(p2.getName());
            }
        });

        ManagementCatalogue catalogue = ManagementCatalogue.getInstance();
        catalogue.fetchProcessors().whenComplete((data, exc) -> {
            parent.getDisplay().asyncExec(() -> {
                if (exc == null) {
                    try {
                        ListProcessorsResponse response = ListProcessorsResponse.parseFrom(data);
                        processorsTable.setInput(response.getProcessorList());
                    } catch (InvalidProtocolBufferException e) {
                        log.log(Level.SEVERE, "Failed to decode server message", e);
                    }
                }
            });
        });

        return composite;
    }

    @Override
    protected void okPressed() {
        IStructuredSelection sel = (IStructuredSelection) processorsTable.getSelection();
        if (!sel.isEmpty()) {
            processorInfo = (ProcessorInfo) sel.getFirstElement();
        }
        super.okPressed();
    }

    public ProcessorInfo getProcessorInfo() {
        return processorInfo;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 375);
    }
}
