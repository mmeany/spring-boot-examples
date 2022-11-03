package net.mmeany.example.common.config.controller;

import net.mmeany.example.common.config.controller.model.ValidationFailedResponse;
import net.mmeany.example.common.config.controller.model.ValidationMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

//@ControllerAdvice
public class BaseControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationFailedResponse> handleConstraintViolations(ConstraintViolationException exception) {
        List<ValidationMessage> validationErrors = exception.getConstraintViolations()
                .stream()
                .map(e -> new ValidationMessage(e.getPropertyPath().toString(), e.getMessage()))
                .toList();
        return new ResponseEntity<>(new ValidationFailedResponse("VALIDATION FAILURE", validationErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationFailedResponse> handleInvalidArguments(MethodArgumentNotValidException exception) {
        return handleBindExceptions(exception);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationFailedResponse> handleBindExceptions(BindException exception) {
        List<ValidationMessage> validationErrors = exception.getBindingResult().getAllErrors()
                .stream()
                .map(e -> new ValidationMessage(parameterName(e), e.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(new ValidationFailedResponse("VALIDATION FAILURE", validationErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationFailedResponse> handleBindExceptions(MethodArgumentTypeMismatchException exception) {
        List<ValidationMessage> validationErrors = List.of(new ValidationMessage(exception.getPropertyName(), exception.getMessage()));

        return new ResponseEntity<>(new ValidationFailedResponse("TYPE CONVERSION FAILURE", validationErrors), HttpStatus.BAD_REQUEST);
    }

    protected String parameterName(final ObjectError error) {
        return (error instanceof FieldError fieldError)
                ? fieldError.getField()
                : error.getObjectName();
    }
}
