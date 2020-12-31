package moe.ofs.backend.services;

public class ExportObjectNotFoundException extends RuntimeException {
    public ExportObjectNotFoundException(String message) {
        super(message);
    }
}
