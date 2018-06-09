package com.ahajizadeh.rts;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author amir
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class InvalidTransactionException extends RuntimeException{
    private static final long serialVersionUID = -1552927998991380762L;

    public InvalidTransactionException(String s) {
        super(s);
    }
}
