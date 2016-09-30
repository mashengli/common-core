package com.qiandai.pay.common.validate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Created by mashengli on 2016/8/1.
 */
public class ValidationServiceImpl implements ValidationService {
    @Resource
    private Validator validator;

    public ValidationResult validate(Object param, boolean fastMode) {
        ValidationResult result = new ValidationResult();

        Set<ConstraintViolation<Object>> violations = this.validator.validate(param);
        boolean isEmpty = violations.isEmpty();

        result.setPassed(isEmpty);
        if (!isEmpty) {
            List<String> failedReasonList = new ArrayList<>(fastMode ? 1 : violations.size());
            for (ConstraintViolation<Object> violation : violations) {
                String reason = violation.getPropertyPath().toString() + violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
                failedReasonList.add(reason);
                if (fastMode) {
                    break;
                }
            }
            result.setFailedReasonList(failedReasonList);
        }
        return result;
    }

    public void validate(Object param) throws InvalidParameterException {
        if (param == null) {
            throw new InvalidParameterException("参数为空");
        }
        ValidationResult vr = validate(param, true);
        if (!vr.isPassed()) {
            throw new InvalidParameterException("输入参数不正确");
        }
    }
}
