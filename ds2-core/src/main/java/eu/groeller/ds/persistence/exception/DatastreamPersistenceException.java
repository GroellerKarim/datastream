package eu.groeller.ds.persistence.exception;

import eu.groeller.ds.DatastreamException;

public abstract class DatastreamPersistenceException extends DatastreamException {

    protected DatastreamPersistenceException(String message, String userMessage) {
        super(message, userMessage);
    }
}
