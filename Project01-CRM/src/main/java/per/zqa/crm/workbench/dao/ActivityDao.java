package per.zqa.crm.workbench.dao;

import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.vo.PageVo;
import per.zqa.crm.workbench.domain.Activity;

import java.util.List;

public interface ActivityDao {

    int saveActivity(Activity activity);

    Integer getTotalByCondition(ActivityVo activityVo);

    List<Activity> getActivityByCondition(ActivityVo activityVo);

    int selectActivityRemarkByActivityId(String id);

    int deleteActivityRemarkByActivityId(String id);

    int deleteActivityById(String id);

    Activity getActivityById(String id);

    int updateActivity(Activity activity);

    Activity getActivityDetailById(String id);
}
