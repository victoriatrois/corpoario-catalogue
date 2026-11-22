package br.com.corpoario.catalogue.exception;

public record ErrorResponse(
    int status,
    String error,
    String message) {}
