package per.zqa.crm.workbench.dao;

import per.zqa.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int saveHistory(TranHistory tranHistory);


    List<TranHistory> getHistoryListByTranId(String tranId);
}
