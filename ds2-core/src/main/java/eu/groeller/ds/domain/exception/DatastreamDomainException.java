package eu.groeller.ds.domain.exception;

import eu.groeller.ds.DatastreamException;

public abstract class DatastreamDomainException extends DatastreamException {

    protected DatastreamDomainException(String message, String userMessage) {
        super(message, userMessage);
    }
}
