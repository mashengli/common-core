package pers.mashengli.common.validate;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mashengli on 2016/8/1.
 */
public class ValidationResult implements Serializable {
    private static final long serialVersionUID = 9034912753064967249L;
    private boolean passed;
    private List<String> failedReasonList;

    public boolean isPassed() {
        return this.passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public List<String> getFailedReasonList() {
        return this.failedReasonList;
    }

    public void setFailedReasonList(List<String> failedReasonList) {
        this.failedReasonList = failedReasonList;
    }

    public String getFirstFailedReason() {
        return (this.failedReasonList != null) && (!this.failedReasonList.isEmpty()) ? (String) this.failedReasonList.get(0) : null;
    }
}