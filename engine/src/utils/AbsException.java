package utils;

public class AbsException extends Exception {
    private final String errorMsg;

    public AbsException(String error) {
        super(error);
        errorMsg = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
