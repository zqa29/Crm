package per.zqa.crm.workbench.service;

import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.exception.TraditionRequestException;
import per.zqa.crm.vo.PageVo;
import per.zqa.crm.workbench.domain.Tran;
import per.zqa.crm.workbench.domain.TranHistory;
import per.zqa.crm.workbench.vo.TranVo;

import java.util.List;
import java.util.Map;

public interface TranService {
    void saveTran(String customerName, Tran tran) throws TraditionRequestException;

    Tran tranDetail(String id) throws TraditionRequestException;

    List<TranHistory> getHistoryListByTranId(String tranId);

    TranVo updateStage(String id, String stage, String money, String expectedDate, String editBy) throws AjaxRequestException;

    PageVo<Map<String,Object>> getCharts() throws AjaxRequestException;
}
