package bumaview.domain.auth.exception;

import bumaview.common.exception.BusinessException;

/**
 * 중복된 사용자 ID로 회원가입을 시도할 때 발생하는 예외
 */
public class DuplicateUserException extends BusinessException {
    
    public DuplicateUserException(String userId) {
        super("이미 존재하는 사용자 ID입니다: " + userId);
    }
    
    public DuplicateUserException(String userId, Throwable cause) {
        super("이미 존재하는 사용자 ID입니다: " + userId, cause);
    }
}