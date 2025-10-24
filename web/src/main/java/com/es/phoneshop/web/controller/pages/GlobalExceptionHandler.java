package com.es.phoneshop.web.controller.pages;

import com.es.core.exception.NotFoundException;
import com.es.core.exception.StockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String EXCEPTION_MESSAGE = "Internal server error";
    private static final String INVALID_REQUEST = "Invalid request";

    private static final String NOT_FOUND_ERROR_PAGE = "notFoundError";

    @ExceptionHandler(StockException.class)
    public ResponseEntity<String> handleOutOfStockException(StockException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : INVALID_REQUEST;

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException() {
        return new ModelAndView(NOT_FOUND_ERROR_PAGE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException() {
        return new ResponseEntity<>(EXCEPTION_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
