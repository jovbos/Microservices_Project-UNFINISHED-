package com.example.Back_Empresa.config.customErrors.error400;

import com.example.Back_Empresa.config.customErrors.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomErrorHandler400 {

    @ExceptionHandler(value={CustomErrorRequest400.class})
    public ResponseEntity<Object> handlerError(CustomErrorRequest400 error) {
        CustomError customError = new CustomError(
                error.getMessage(),
                400,
                "Info",
                ZonedDateTime.now(ZoneId.of("UTC+1"))
        );
        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);
    }

}