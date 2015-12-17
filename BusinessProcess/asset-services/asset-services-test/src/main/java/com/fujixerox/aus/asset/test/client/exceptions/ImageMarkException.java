package com.fujixerox.aus.asset.test.client.exceptions;

public class ImageMarkException extends Exception {

    private static final long serialVersionUID = -1L;

    ImageMarkException() {
        super();
    }

    public ImageMarkException(String message) {
        super(message);
    }

    public ImageMarkException(String message, Throwable cause) {
        super(message, cause);
    }

    ImageMarkException(Throwable cause) {
        super(cause);
    }
}
