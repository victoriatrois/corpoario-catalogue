package br.com.corpoario.catalogue.exception;

public class InvalidCollageIdException extends RuntimeException {

    public InvalidCollageIdException() {
        super("The collage id value must be a valid UUID");
    }
}
