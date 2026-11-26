package com.api.governo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String,Object>> status(ResponseStatusException e) { return response(HttpStatus.valueOf(e.getStatusCode().value()),e.getReason()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e) {
        List<String> errors=e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).distinct().toList();
        return response(HttpStatus.BAD_REQUEST,"Dados inválidos.",errors);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String,Object>> unreadable(HttpMessageNotReadableException e) { return response(HttpStatus.BAD_REQUEST,"JSON inválido ou data em formato incorreto."); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String,Object>> unexpected(Exception e) { return response(HttpStatus.INTERNAL_SERVER_ERROR,"Erro interno ao processar a solicitação."); }
    private ResponseEntity<Map<String,Object>> response(HttpStatus status,String message) { return response(status,message,List.of()); }
    private ResponseEntity<Map<String,Object>> response(HttpStatus status,String message,List<String> details) { Map<String,Object> body=new LinkedHashMap<>();body.put("status",status.value());body.put("erro",message);if(!details.isEmpty())body.put("detalhes",details);return ResponseEntity.status(status).body(body); }
}
