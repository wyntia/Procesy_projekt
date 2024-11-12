package pl.pollub.backend.exception;

public class UserSaveException extends RuntimeException {
    public UserSaveException(String message) {
        super(message);
    }
}
