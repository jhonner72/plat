package com.fujixerox.aus.lombard;

/**
 * Thrown by application code when an exception occurs that can not occur.
 * If is expected that the request will not be attempted a second time as it will most likely continue to fail.
 * Compare with SystemException
 */
public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message)
    {
        super(message);
    }
    public UnexpectedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
