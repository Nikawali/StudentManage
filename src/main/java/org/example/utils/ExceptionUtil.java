package org.example.utils;


import org.example.exception.BusinessesException;

public class ExceptionUtil {

    public static Result<?> handle(Exception e) {

        if(e instanceof BusinessesException){

            BusinessesException ae =
                    (BusinessesException) e;

            return Result.fail(
                    ae.getCode(),
                    ae.getMessage()
            );
        }

        e.printStackTrace();

        return Result.fail(
                "服务器内部异常"
        );
    }
}