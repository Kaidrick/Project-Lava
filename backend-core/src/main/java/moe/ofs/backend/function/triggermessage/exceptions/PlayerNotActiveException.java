package moe.ofs.backend.function.triggermessage.exceptions;

public class PlayerNotActiveException extends RuntimeException {
    public PlayerNotActiveException() {
        super("Player is currently not active in mission and thus cannot receive a trigger message.");
    }
}
