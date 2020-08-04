package per.zqa.crm.workbench.dao;

import org.apache.poi.ss.formula.functions.T;
import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.workbench.domain.Activity;

import java.util.List;

public interface ActivityDao {

    int saveActivity(Activity activity);

    List<T> selectActivities(ActivityVo activityVo);
}
