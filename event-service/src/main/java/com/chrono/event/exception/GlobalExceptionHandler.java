package com.chrono.event.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleEventNotFoundException(EventNotFoundException ex) {
        log.error("Événement non trouvé: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CivilizationNotFoundException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleCivilizationNotFoundException(CivilizationNotFoundException ex) {
        log.error("Civilisation non trouvée: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex) {
        log.error("Commentaire non trouvé: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Argument invalide: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Erreur de conversion de type: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST, 
                "Erreur de conversion de paramètre: " + ex.getName() + " - " + ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Erreur de validation: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        String errorMessage = "Erreur de validation";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleResourceAccessException(ResourceAccessException ex) {
        log.error("Erreur d'accès au service externe: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE, 
                "Le service externe n'est pas disponible. Veuillez réessayer plus tard."
        );
    }
    
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleRestClientException(RestClientException ex) {
        log.error("Erreur de communication avec un service externe: {}", ex.getMessage());
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erreur de communication avec un service externe. Veuillez réessayer plus tard."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<com.chrono.event.exception.ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erreur inattendue: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Une erreur inattendue s'est produite. Veuillez réessayer plus tard."
        );
    }

    private ResponseEntity<com.chrono.event.exception.ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        com.chrono.event.exception.ErrorResponse errorResponse = new com.chrono.event.exception.ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now().toString()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
} 