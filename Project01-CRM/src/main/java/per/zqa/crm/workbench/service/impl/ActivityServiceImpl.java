package per.zqa.crm.workbench.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.exception.TraditionRequestException;
import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.vo.PageVo;
import per.zqa.crm.workbench.dao.ActivityDao;
import per.zqa.crm.workbench.dao.ActivityRemarkDao;
import per.zqa.crm.workbench.domain.Activity;
import per.zqa.crm.workbench.domain.ActivityRemark;
import per.zqa.crm.workbench.service.ActivityService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivityRemarkDao activityRemarkDao;

    @Override
    public boolean saveActivity(Activity activity) throws AjaxRequestException {
        // 调用ActivityDao方法进行插入数据
        int i = activityDao.saveActivity(activity);
        // 判断是否插入成功
        if (i != 1) {
            // 插入不成功，抛出异常
            throw new AjaxRequestException("创建市场活动不成功");
        }
        return true;
    }

    @Override
    public PageVo<Activity> selectActivity(ActivityVo activityVo) {
        // 取得total
        Integer total = activityDao.getTotalByCondition(activityVo);

        // 取得datalist
        List<Activity> activities = activityDao.getActivityByCondition(activityVo);

        // 创建Vo对象，将total和datalist封装到Vo中
        PageVo<Activity> pageVo = new PageVo<>();
        pageVo.setTotal(total);
        pageVo.setDataList(activities);

        return pageVo;
    }

    @Override
    public boolean deleteActivity(String[] ids) {
        // 遍历获取每个id查询出是否关联备注
        for (String id: ids) {
            // 首先调用方法获取备注表是否有关联记录(删除哪个表就调用相应的表的dao层的方法)
            int remarkNums = activityRemarkDao.selectActivityRemarkByActivityId(id);

            // 判断记录的条数
            if (remarkNums > 0) {
                // 有记录先删除记录
                // 调用activityDao.deleteActivityRemarkByActivityId进行删除
                int i = activityRemarkDao.deleteActivityRemarkByActivityId(id);
                if (i == remarkNums) {
                    // 删除成功继续进行下一步操作，
                    int activityResult = activityDao.deleteActivityById(id);
                    if (activityResult != 1) {
                        // 删除主表记录失败
                        return false;
                    }
                } else {
                    // 删除备注记录失败
                    return false;
                }
            } else {
                // 无记录直接删除主表数据
                int activityResult = activityDao.deleteActivityById(id);
                if (activityResult != 1) {
                    // 删除主表记录失败
                    return false;
                }
            }
        }
        // 程序运行到这里说明删除成功，返回true
        return true;
    }

    @Override
    public Activity getActivityById(String id) {
        Activity activity = activityDao.getActivityById(id);
        return activity;
    }

    @Override
    public boolean updateActivity(Activity activity) {
        boolean flag = false;
        // 调用方法进行更新
        int i = activityDao.updateActivity(activity);
        if (i == 1) {
            // 更新成功
            flag = true;
        }
        return flag;
    }

    @Override
    public Activity getActivityDetailById(String id) {
        Activity activity = activityDao.getActivityDetailById(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAId(String aId) {
        List<ActivityRemark> remarkList = activityRemarkDao.getRemarkListByAId(aId);
        return remarkList;
    }

    @Override
    public boolean deleteRemark(String id) {
        int i = activityRemarkDao.deleteRemark(id);

        if (i != 1) {
            // 删除失败
            return false;
        }
        return true;
    }

    @Override
    public boolean saveRemark(ActivityRemark activityRemark) {
        // 先调用方法进行插入
        int i = activityRemarkDao.saveRemark(activityRemark);
        if (i != 1) {
            // 添加失败
            return false;
        }
        return true;
    }

    @Override
    public boolean updateRemark(ActivityRemark activityRemark) {
        int i = activityRemarkDao.updateRemark(activityRemark);
        if (i != 1) {
            return false;
        }
        return true;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> aList =  activityDao.getActivityListByClueId(clueId);
        return aList;
    }

    @Override
    public List<Activity> getActivityListNotByClueId(String condition, String clueId) {
        // 传入参数调用dao层
        List<Activity> aList = activityDao.getActivityListNotByClueId(condition, clueId);
        return aList;

    }

    @Override
    public List<Activity> getActivityListByName(String condition) {
        List<Activity> aList = activityDao.getActivityListByName(condition);
        return aList;
    }

    @Override
    public List<Activity> getActivityList() throws  TraditionRequestException {
        List<Activity> activityList = activityDao.getActivityList();
        if (activityList == null) {
            throw new TraditionRequestException("获取市场活动列表失败");
        }
        return activityList;
    }

    @Override
    public List<Activity> getActivityListById(String[] ids) {
        List<Activity> aList = new ArrayList<>();
        // 遍历数组
        for (String id: ids) {
            // 调用dao层
            Activity activity = activityDao.getActivityById(id);
            // 放入list中
            aList.add(activity);
        }
        return aList;
    }

    @Override
    public void saveActivityByList(List<Activity> aList) throws AjaxRequestException {
        // 遍历list，传入Activity对象
        for (Activity activity: aList) {
            int i = activityDao.saveActivity(activity);
            if (i != 1) {
                throw new AjaxRequestException("保存市场活动失败");
            }
        }
    }
}
