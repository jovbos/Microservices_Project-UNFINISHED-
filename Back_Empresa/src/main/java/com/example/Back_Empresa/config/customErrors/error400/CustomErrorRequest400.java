package com.example.Back_Empresa.config.customErrors.error400;

public class CustomErrorRequest400 extends RuntimeException {

    public CustomErrorRequest400(String message) {
        super (message);
    }

}