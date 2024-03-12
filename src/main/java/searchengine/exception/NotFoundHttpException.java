package searchengine.exception;

public class NotFoundHttpException extends RuntimeException {

    public NotFoundHttpException() {
    }

    public NotFoundHttpException(String message) {
        super(message);
    }
}
