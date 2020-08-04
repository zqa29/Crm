package per.zqa.crm.vo;

import java.util.List;

public class PageVo<T> {

    private Integer total; // 总纪录条数
    private List<T> dataList; // 数据对象list

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
