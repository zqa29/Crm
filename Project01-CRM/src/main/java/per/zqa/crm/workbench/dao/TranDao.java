package per.zqa.crm.workbench.dao;

import org.apache.ibatis.annotations.Param;
import per.zqa.crm.workbench.domain.Tran;
import per.zqa.crm.workbench.vo.TranVo;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int saveTranFromConvert(Tran tran);

    int saveTran(Tran tran);

    Tran tranDetail(String id);

    int updateStage(@Param("id") String id, @Param("stage") String stage, @Param("money") String money, @Param("expectedDate") String expectedDate, @Param("editBy") String editBy, @Param("editTime") String editTime);

    int getTotal();

    List<Map<String,Object>> getCharts();
}
