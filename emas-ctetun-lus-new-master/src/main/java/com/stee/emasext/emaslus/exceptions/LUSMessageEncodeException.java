package com.stee.emasext.emaslus.exceptions;

/**
 * @author Wang Yu
 * Created at 2023/2/20
 */
public class LUSMessageEncodeException extends RuntimeException {
    public LUSMessageEncodeException() {
        super();
    }

    public LUSMessageEncodeException(String message) {
        super(message);
    }
}
