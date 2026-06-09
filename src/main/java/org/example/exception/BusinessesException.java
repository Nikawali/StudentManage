package org.example.exception;

public class BusinessesException
        extends RuntimeException {

    private int code;

    public BusinessesException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessesException(int code,
                        String message) {

        super(message);
        this.code = code;
    }



    public int getCode() {
        return code;
    }
}