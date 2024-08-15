package com.shoperal.core.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import com.shoperal.core.controller.dto.ShoperalErrorType;
import com.shoperal.core.controller.dto.ValidationErrorResponse;
import com.shoperal.core.controller.dto.ValidationErrorResponse.Violation;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Julius Krah
 */
@RestControllerAdvice
public class ErrorHandlingRestControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.getViolations().add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
        }
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ValidationErrorResponse error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ShoperalErrorType onIllegalArgumentException(IllegalArgumentException e) {
        return new ShoperalErrorType(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ShoperalErrorType onDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ShoperalErrorType(HttpStatus.CONFLICT.value(), e.getMessage());
    }

}
