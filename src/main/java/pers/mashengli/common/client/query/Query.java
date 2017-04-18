package pers.mashengli.common.client.query;

import java.io.Serializable;

/**
 * Created by mashengli on 2016/8/2.
 */
public class Query implements Serializable {
    private static final long serialVersionUID = 1699457668211951418L;
    private static int DEFAULT_PAGE_SIZE = 15;
    private static int DEFAULT_PAGE_NUMBER = 1;
    public static String ORDER_TYPE_ASC = "ASC";
    public static String ORDER_TYPE_DESC = "DESC";
    private static String ORDER = "ID";
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    private Integer pageNumber = DEFAULT_PAGE_NUMBER;
    private String orderOperator = ORDER_TYPE_DESC;
    private String orderParam = ORDER;

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        if (pageNumber != null && pageNumber < 1) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        this.pageNumber = pageNumber;
    }

    public String getOrderParam() {
        return this.orderParam;
    }

    public void setOrderParam(String orderParam) {
        this.orderParam = orderParam;
    }

    public String getOrderOperator() {
        return this.orderOperator;
    }

    public void setOrderOperator(String orderOperator) {
        this.orderOperator = orderOperator;
    }
}
