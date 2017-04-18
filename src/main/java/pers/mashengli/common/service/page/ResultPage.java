package pers.mashengli.common.service.page;

import java.util.ArrayList;
import java.util.List;

import pers.mashengli.common.dal.query.MapperQuery;

/**
 * @param <T>
 */
public final class ResultPage<T>{

    private List<T>     t;
    private MapperQuery query;
    /**
     * total=0,尾页;total=1,后面有1页;total=2,后面有2页
     */
    private Integer     total = 0;

    //总行数
    private Long count = 0L;

    public ResultPage(List<T> list, MapperQuery query){
        this.t = new ArrayList<T>();

        if (query != null) {
            this.query = query;
        } else {
            this.query = new MapperQuery();
        }
        init(t, list);
    }

    private void init(List<T> t, List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        int pageSize = 0;
        if (list.size() < query.getPageSize()) {
            pageSize = list.size();
        } else {
            pageSize = query.getPageSize();
        }

        for (int i = 0; i < pageSize; i++) {
            t.add(list.get(i));
        }

        int count = list.size();

        if (count > query.getPageSize() * 2) {
            total = 2;
        } else if (count > query.getPageSize()) {
            total = 1;
        } else {
            total = 0;
        }
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total){
        this.total = total;
    }

    public int getPageNumber() {
        return query.getPageNumber();
    }

    public int getPageSize() {
        return query.getPageSize();
    }

    public int getTotalPages() {
        if (total < 0) {
            return -1;
        }
        int totalPages = total / getPageSize();
        if (total % getPageSize() > 0) {
            totalPages++;
        }
        return totalPages;
    }

    public int getIndex() {
        return (getPageNumber() - 1) * getPageSize() + 1;
    }

    public List<T> getResult() {
        return t;
    }

}
