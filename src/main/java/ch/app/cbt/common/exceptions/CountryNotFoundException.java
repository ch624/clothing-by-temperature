package ch.app.cbt.common.exceptions;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException() {
        super("country not found");
    }

    public CountryNotFoundException(String msg) {
        super(msg);
    }
}
