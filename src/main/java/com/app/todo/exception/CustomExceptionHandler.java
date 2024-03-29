package com.app.todo.exception;

import com.app.todo.dto.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<String>> handleCustomException(Exception ex) {
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<String>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(er -> er.getDefaultMessage())
                .collect(Collectors.joining(", "));

        APIResponse<String> response = APIResponse.badRequest(null, errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex
    ) {
        String errorMsg = "Request body is missing or malformed. Please provide a valid request body.";
        APIResponse<String> response = APIResponse.badRequest(null, errorMsg);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse<String>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex
    ) {
        String name = ex.getParameterName();
        String type = ex.getParameterType();
        String message = "Parameter " + name + " of type " + type + " is missing!";
        APIResponse<String> response = APIResponse.badRequest(null, message);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType().toString();
        String message = "Parameter " + paramName + " must be of type " + requiredType;
        APIResponse<String> response = APIResponse.badRequest(null, message);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
