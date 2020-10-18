package moe.ofs.backend.atlas.exceptions;

public class AtlasBorderOutOfBoundException extends RuntimeException {


    private AtlasBorderOutOfBoundException() {
        super();
    }

    public AtlasBorderOutOfBoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
