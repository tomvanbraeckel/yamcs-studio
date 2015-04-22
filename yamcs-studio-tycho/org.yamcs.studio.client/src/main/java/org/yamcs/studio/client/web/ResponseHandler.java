package org.yamcs.studio.client.web;

import com.google.protobuf.MessageLite;

public interface ResponseHandler {

    /**
     * The response of the server. Could be a RestExceptionMessage.
     */
    void onMessage(MessageLite responseMsg);

    /**
     * When some uncaught exception occurred
     */
    void onException(Exception e);
}