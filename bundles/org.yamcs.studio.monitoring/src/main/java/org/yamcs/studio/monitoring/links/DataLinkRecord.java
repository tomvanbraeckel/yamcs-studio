package org.yamcs.studio.monitoring.links;

import org.yamcs.protobuf.YamcsManagement.LinkInfo;

/**
 * Wrapper around LinkInfo with some extra metadata for use in the table
 */
public class DataLinkRecord {

    private LinkInfo linkInfo;
    private long lastDataInCountIncrease;
    private long lastDataOutCountIncrease;

    public DataLinkRecord(LinkInfo linkInfo) {
        this.linkInfo = linkInfo;
    }

    public LinkInfo getLinkInfo() {
        return linkInfo;
    }

    public boolean isDataInCountIncreasing() {
        return (System.currentTimeMillis() - lastDataInCountIncrease) < 1500;
    }

    public boolean isDataOutCountIncreasing() {
        return (System.currentTimeMillis() - lastDataOutCountIncrease) < 1500;
    }

    public void processIncomingLinkInfo(LinkInfo incoming) {
        if (incoming.getDataInCount() > linkInfo.getDataInCount()) {
            lastDataInCountIncrease = System.currentTimeMillis();
        }
        if (incoming.getDataOutCount() > linkInfo.getDataOutCount()) {
            lastDataOutCountIncrease = System.currentTimeMillis();
        }
        linkInfo = incoming;
    }
}
