package com.fujixerox.aus.lombard;

/**
 * Thrown by application code when an exception occurs that can not be handled immediately.
 * If is expected that if the request is attempted a second time then the request will succeed.
 * Compare with UnexpectedException
 */
public class SystemException extends RuntimeException {
    public SystemException(String message)
    {
        super(message);
    }
    public SystemException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
