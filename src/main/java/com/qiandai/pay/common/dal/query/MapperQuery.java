package com.qiandai.pay.common.dal.query;

import java.io.Serializable;

/**
 * 用于查询
 * Created by mashengli on 2016/7/6.
 */
public class MapperQuery implements Serializable {

    private static final long serialVersionUID = -2196269466666530147L;

    private static int        DEFAULT_PAGE_SIZE   = 15;
    private static int        DEFAULT_PAGE_NUMBER = 1;

    public static String      ORDER_TYPE_ASC      = "ASC";
    public static String      ORDER_TYPE_DESC     = "DESC";
    private static String     ORDER               = "ID";

    private Integer           pageSize            = DEFAULT_PAGE_SIZE;
    private Integer           pageNumber          = DEFAULT_PAGE_NUMBER;

    private String            orderOperator       = ORDER_TYPE_DESC;
    private String            orderParam          = ORDER;

    private Integer           offset;
    private Integer           size;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize != null && pageSize <= 0) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        if (pageNumber != null && pageNumber < 1) {
            this.pageNumber = DEFAULT_PAGE_NUMBER;
        } else {
            this.pageNumber = pageNumber;
        }
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getOffset() {
        offset = this.getPageSize() * (this.getPageNumber() - 1);
        return offset;
    }

    public String getOrderParam() {
        return orderParam;
    }

    public void setOrderParam(String orderParam) {
        this.orderParam = orderParam;
    }

    public String getOrderOperator() {
        return orderOperator;
    }

    public void setOrderOperator(String orderOperator) {
        this.orderOperator = orderOperator;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {
        this.size = pageSize;
        return size;
    }

}
