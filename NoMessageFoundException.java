public class NoMessageFoundException extends Exception {
    public NoMessageFoundException(String message) {
        super(message);
    }
    public String getMessage() {
        return "No Message Found!";
    }
}
