package per.zqa.crm.workbench.dao;

import per.zqa.crm.workbench.domain.Tran;
import per.zqa.crm.workbench.vo.ConvertVo;

public interface TranDao {

    int saveTranFromConvert(Tran tran);
}
