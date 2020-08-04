package per.zqa.crm.workbench.service.impl;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.workbench.dao.ActivityDao;
import per.zqa.crm.workbench.domain.Activity;
import per.zqa.crm.workbench.service.ActivityService;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;

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
    public List<T> selectActivity(ActivityVo activityVo) {
        List<T> list = activityDao.selectActivities(activityVo);
        return list;
    }
}
