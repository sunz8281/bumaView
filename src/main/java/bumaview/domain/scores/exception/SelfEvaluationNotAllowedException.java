package bumaview.domain.scores.exception;

import bumaview.common.exception.BusinessException;

/**
 * 본인 답변 평가 시도 시 발생하는 예외
 */
public class SelfEvaluationNotAllowedException extends BusinessException {
    
    public SelfEvaluationNotAllowedException() {
        super("본인의 답변은 평가할 수 없습니다.");
    }
    
    public SelfEvaluationNotAllowedException(String message) {
        super(message);
    }
}