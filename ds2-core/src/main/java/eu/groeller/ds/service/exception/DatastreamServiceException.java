package eu.groeller.ds.service.exception;

import eu.groeller.ds.DatastreamException;

public abstract class DatastreamServiceException extends DatastreamException {

    protected DatastreamServiceException(String message, String userMessage) {
        super(message, userMessage);
    }
}
