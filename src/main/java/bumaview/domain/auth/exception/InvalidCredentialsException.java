package bumaview.domain.auth.exception;

import bumaview.common.exception.BusinessException;

/**
 * 잘못된 로그인 정보로 로그인을 시도할 때 발생하는 예외
 */
public class InvalidCredentialsException extends BusinessException {
    
    public InvalidCredentialsException() {
        super("아이디 또는 비밀번호가 올바르지 않습니다");
    }
    
    public InvalidCredentialsException(Throwable cause) {
        super("아이디 또는 비밀번호가 올바르지 않습니다", cause);
    }
}