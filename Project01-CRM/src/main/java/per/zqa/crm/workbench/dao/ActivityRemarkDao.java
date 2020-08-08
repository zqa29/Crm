package per.zqa.crm.workbench.dao;

import per.zqa.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {


    int selectActivityRemarkByActivityId(String id);

    int deleteActivityRemarkByActivityId(String id);

    List<ActivityRemark> getRemarkListByAId(String aId);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark activityRemark);

    int updateRemark(ActivityRemark activityRemark);
}
