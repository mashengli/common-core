package pers.mashengli.common.validate;

import java.security.InvalidParameterException;

/**
 * Created by mashengli on 2016/8/1.
 */
public abstract interface ValidationService {

    public abstract ValidationResult validate(Object paramObject, boolean paramBoolean);

    public abstract void validate(Object paramObject)
            throws InvalidParameterException;
}
