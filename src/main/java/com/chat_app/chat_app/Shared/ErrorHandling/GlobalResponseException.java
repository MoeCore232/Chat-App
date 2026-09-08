package com.chat_app.chat_app.Shared.ErrorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@ControllerAdvice
public class GlobalResponseException {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<GlobalResponse<?>> handleNoResourceFoundException (NoResourceFoundException ex) {
        List<GlobalResponse.ErrorItem> errorItems = List.of (
                new GlobalResponse.ErrorItem("Error: No Resource Found Exception!")
        );
        return new ResponseEntity<>(new GlobalResponse(errorItems), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<GlobalResponse<?>> customResponseException (CustomResponseException ex) {
        List<GlobalResponse.ErrorItem> errorItems = List.of (
                new GlobalResponse.ErrorItem(ex.getMessage())
        );
        return new ResponseEntity<>(new GlobalResponse<>(errorItems), HttpStatus.resolve(ex.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<?>> formISValid(MethodArgumentNotValidException ex){
        List<GlobalResponse.ErrorItem> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new GlobalResponse.ErrorItem(err.getField() + " " + err.getDefaultMessage()))
                .toList();
        return new ResponseEntity<>(new GlobalResponse<>(errors),HttpStatus.BAD_REQUEST);
    }
}
