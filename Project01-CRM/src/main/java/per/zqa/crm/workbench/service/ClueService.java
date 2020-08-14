package per.zqa.crm.workbench.service;

import per.zqa.crm.workbench.domain.Clue;
import per.zqa.crm.workbench.domain.Tran;

public interface ClueService {
    boolean saveClue(Clue clue);

    Clue getDetail(String id);

    boolean deleteRelationById(String id);

    boolean boundRelation(String uuid, String cId, String aId);

    boolean saveCustomerByConvert(String clueId, String createBy, Tran tran);
}
