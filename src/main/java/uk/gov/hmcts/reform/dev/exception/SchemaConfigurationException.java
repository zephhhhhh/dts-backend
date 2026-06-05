package uk.gov.hmcts.reform.dev.exception;

public class SchemaConfigurationException extends RuntimeException {
    public SchemaConfigurationException(String msg) {
        super(msg);
    }

    public SchemaConfigurationException(Throwable t) {
        super(t);
    }

    public SchemaConfigurationException(String message, Throwable t) {
        super(message, t);
    }
}
