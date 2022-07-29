package com.example.Back_Empresa.config.customErrors.error404;

import com.example.Back_Empresa.config.customErrors.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomErrorHandler404 {

    @ExceptionHandler(value={CustomErrorRequest404.class})
    public ResponseEntity<Object> handlerError(CustomErrorRequest404 error) {
        CustomError customError = new CustomError(
                error.getMessage(),
                404,
                "Not found",
                ZonedDateTime.now(ZoneId.of("UTC+1"))
        );
        return new ResponseEntity<>(customError, HttpStatus.NOT_FOUND);
    }

}