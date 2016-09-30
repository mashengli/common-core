package com.qiandai.pay.common.client.model;

import java.io.Serializable;

/**
 * Created by mashengli on 2016/8/2.
 */
public class BaseModel implements Serializable {
    private static final long serialVersionUID = 7324525047116205878L;
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
