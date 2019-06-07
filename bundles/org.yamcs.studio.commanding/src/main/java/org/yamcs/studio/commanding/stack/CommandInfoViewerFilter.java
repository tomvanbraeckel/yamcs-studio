package org.yamcs.studio.commanding.stack;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.yamcs.protobuf.Mdb.CommandInfo;
import org.yamcs.protobuf.Yamcs.NamedObjectId;

/**
 * JFace ViewerFilter for use with GPB CommandInfo
 */
public class CommandInfoViewerFilter extends ViewerFilter {

    private String regex = ".*";

    public void setSearchTerm(String searchTerm) {
        regex = "(?i:.*" + searchTerm + ".*)";
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        CommandInfo cmd = (CommandInfo) element;

        // check match in all namespace
        boolean matching = false;
        for (NamedObjectId alias : cmd.getAliasList()) {
            matching |= alias.getName().matches(regex);
        }
        matching |= cmd.getQualifiedName().matches(regex);
        return matching;
    }
}
