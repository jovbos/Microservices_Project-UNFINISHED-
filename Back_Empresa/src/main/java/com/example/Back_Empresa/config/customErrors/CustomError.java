package com.example.Back_Empresa.config.customErrors;

import java.time.ZonedDateTime;

public record CustomError (String message, Integer httpStatus, String type, ZonedDateTime timestamp){};

