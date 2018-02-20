package org.superbiz.fetch;

public class ParsingException extends Exception {
    public ParsingException(Throwable exception) {
        super(exception);
    }

    public ParsingException(String message) {
        super(message);
    }
}
