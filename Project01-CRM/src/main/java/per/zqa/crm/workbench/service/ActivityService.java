package per.zqa.crm.workbench.service;

import org.apache.poi.ss.formula.functions.T;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.workbench.domain.Activity;

import java.util.List;

public interface ActivityService {

    boolean saveActivity(Activity activity) throws AjaxRequestException;

    List<T> selectActivity(ActivityVo activityVo);
}
