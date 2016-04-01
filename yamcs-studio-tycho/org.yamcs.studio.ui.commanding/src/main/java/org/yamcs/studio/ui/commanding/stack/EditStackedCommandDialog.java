package org.yamcs.studio.ui.commanding.stack;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.yamcs.protobuf.Mdb.ArgumentInfo;

public class EditStackedCommandDialog extends TitleAreaDialog {

    private StackedCommand command;
    private List<Text> textFields = new ArrayList<>();

    public EditStackedCommandDialog(Shell parentShell, StackedCommand command) {
        super(parentShell);
        this.command = command;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    public void create() {
        super.create();
        setTitle("Edit Stacked Command");
        setMessage(command.getMetaCommand().getQualifiedName());
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setLayout(new GridLayout());

        ExpandableComposite ec = new ExpandableComposite(composite, ExpandableComposite.TREE_NODE |
                ExpandableComposite.CLIENT_INDENT);
        ec.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ec.setLayout(new GridLayout(1, false));
        ec.setText("Command Options");
        Composite optionsComposite = new Composite(ec, SWT.NONE);
        optionsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        optionsComposite.setLayout(new GridLayout(2, false));
        Label l1 = new Label(optionsComposite, SWT.NONE);
        l1.setText("Comment");
        GridData gridData = new GridData(SWT.NONE, SWT.TOP, false, false);
        l1.setLayoutData(gridData);
        Text comment = new Text(optionsComposite, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        comment.setText(command.getComment() != null ? command.getComment() : "");
        ec.setExpanded(!comment.getText().isEmpty());
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = 2 * comment.getLineHeight();
        comment.setLayoutData(gridData);
        ec.setClient(optionsComposite);
        ec.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                parent.layout(true);
                // parent.getShell().pack();
                composite.layout();
                optionsComposite.layout();
                composite.layout();
            }
        });
        comment.addModifyListener(evt -> {
            if (comment.getText().trim().isEmpty()) {
                command.setComment(null);
            } else {
                command.setComment(comment.getText());
            }
        });

        Label desc = new Label(composite, SWT.NONE);
        desc.setText("Specify the command parameters:");
        desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite argumentsComposite = new Composite(composite, SWT.NONE);
        argumentsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        argumentsComposite.setLayout(new GridLayout(2, false));
        for (ArgumentInfo arg : command.getMetaCommand().getArgumentList()) {
            Label lbl = new Label(argumentsComposite, SWT.NONE);
            lbl.setText(arg.getName());

            Text text = new Text(argumentsComposite, SWT.BORDER);
            text.setData(arg);
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            String value = command.getAssignedStringValue(arg);
            text.setText(value == null ? "" : value);
            text.addModifyListener(evt -> command.addAssignment(arg, text.getText()));
            textFields.add(text);
        }

        return composite;
    }

    @Override
    protected void okPressed() {
        for (Text textField : textFields) {
            ArgumentInfo arg = (ArgumentInfo) textField.getData();
            if (textField.getText().trim().isEmpty()) {
                command.addAssignment(arg, null);
            } else {
                command.addAssignment(arg, textField.getText());
            }
        }
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 375);
    }
}
