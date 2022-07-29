package com.example.Back_Web.config.customErrors.error403;

import com.example.Back_Web.config.customErrors.CustomError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomErrorHandler403 {

    @ExceptionHandler(value={CustomErrorRequest403.class})
    public ResponseEntity<Object> handlerError(CustomErrorRequest403 error) {
        CustomError customError = new CustomError(
                error.getMessage(),
                403,
                "Access Denied",
                ZonedDateTime.now(ZoneId.of("UTC+1"))
        );
        return new ResponseEntity<>(customError, HttpStatus.FORBIDDEN);
    }

}