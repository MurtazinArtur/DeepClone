package newjob.deepclone;

public class DeepCopyException extends RuntimeException{
    public DeepCopyException(String message) {
        super(message);
    }

    public DeepCopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeepCopyException(Throwable cause) {
        super(cause);
    }
}
