package per.zqa.crm.workbench.dao;

import per.zqa.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> getClueRemarkListByClueId(String clueId);

    int deleteRemark(ClueRemark clueRemark);
}
