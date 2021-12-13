package com.pro02.springbootshop.exception;

public class OutStockException extends RuntimeException{

    public OutStockException(String message){
        super(message);
    }
}
