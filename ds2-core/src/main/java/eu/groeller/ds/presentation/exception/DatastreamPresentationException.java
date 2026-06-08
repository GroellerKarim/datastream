package eu.groeller.ds.presentation.exception;

import eu.groeller.ds.DatastreamException;

public abstract class DatastreamPresentationException extends DatastreamException {

    protected DatastreamPresentationException(String message, String userMessage) {
        super(message, userMessage);
    }
}
