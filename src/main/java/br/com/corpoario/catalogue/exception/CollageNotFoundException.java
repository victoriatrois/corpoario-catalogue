package br.com.corpoario.catalogue.exception;

import java.util.UUID;

public class CollageNotFoundException extends RuntimeException {
    private final UUID collageId;

    public CollageNotFoundException(UUID collageId) {
        super("No collage was found with the id " + collageId);
        this.collageId = collageId;
    }

    public UUID getCollageId() {
        return collageId;
    }

}
