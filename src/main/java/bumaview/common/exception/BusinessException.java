package bumaview.common.exception;

/**
 * 비즈니스 로직에서 발생하는 예외의 기본 클래스
 */
public abstract class BusinessException extends RuntimeException {
    
    protected BusinessException(String message) {
        super(message);
    }
    
    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}