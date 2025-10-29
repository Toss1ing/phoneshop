package com.es.phoneshop.web.controller.pages;

import com.es.core.exception.StockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleOutOfStockException() {
        StockException exception = new StockException("Out of stock");
        ResponseEntity<String> response = handler.handleOutOfStockException(exception);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Out of stock", response.getBody());
    }

    @Test
    void testHandleValidationExceptionsWithFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "Invalid value");
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<String> response = handler.handleValidationExceptions(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid value", response.getBody());
    }

    @Test
    void testHandleValidationExceptionsWithoutFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(null);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<String> response = handler.handleValidationExceptions(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid request", response.getBody());
    }

    @Test
    void testHandleNotFoundException() {
        ModelAndView modelAndView = handler.handleNotFoundException();

        assertEquals("notFoundError", modelAndView.getViewName());
    }

    /*@Test
    void testHandleGenericException() {
        ResponseEntity<String> response = handler.handleGenericException();

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal server error", response.getBody());
    }*/
}
