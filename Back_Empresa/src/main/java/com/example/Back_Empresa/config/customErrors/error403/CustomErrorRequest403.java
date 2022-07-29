package com.example.Back_Empresa.config.customErrors.error403;

public class CustomErrorRequest403 extends RuntimeException {

    public CustomErrorRequest403(String message) {
        super (message);
    }

}