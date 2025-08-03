package com.arman.parkingservice.config;

import com.arman.parkingservice.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFound(ResourceNotFoundException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleAlreadyUsed(ResourceAlreadyUsedException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ExceptionHandler(BookingNotReservedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleNotReserved(BookingNotReservedException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ExceptionHandler(BookingNotStartedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleNotStarted(BookingNotStartedException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ExceptionHandler(BookingExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ErrorDto handleExpired(BookingExpiredException ex) {
        return new ErrorDto(ex.getMessage());
    }

    // fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleAny(Exception ex) {
        return new ErrorDto(ex.getMessage());
    }

    public record ErrorDto(String error) { }
}

