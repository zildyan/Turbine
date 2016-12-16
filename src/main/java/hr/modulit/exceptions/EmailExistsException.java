package hr.modulit.exceptions;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String email) {
        super("There is an account with email address: " + email);
    }
}
