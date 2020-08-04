package per.zqa.crm.workbench.web.controller;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;
import per.zqa.crm.utils.DateTimeUtil;
import per.zqa.crm.utils.UUIDUtil;
import per.zqa.crm.workbench.domain.Activity;
import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.workbench.service.ActivityService;

import javax.servlet.http.HttpSession;
import java.util.List;

/*
    workbench的controller:
        1.进行免验证登录操作
 */
@Controller
@RequestMapping("/workbench/activity")
public class ActivityController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;

    @RequestMapping("/toIndex.do")
    public String toIndex() {
        return "redirect:/workbench/index.jsp";
    }

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList() {
        // 调用UserService方法执行查询操作
        List<User> userList = userService.getUserList();
        return userList;
    }

    @RequestMapping("/saveActivity.do")
    public void saveActivity(Activity activity, HttpSession session) throws AjaxRequestException { // 使用对象接受参数
        // 继续向对象里添加数据
        activity.setId(UUIDUtil.getUUID());
        activity.setCreateTime(DateTimeUtil.getSysTime());
        String user = ((User)session.getAttribute("user")).getName();
        activity.setCreateBy(user);

        // 调用ActivityService方法执行添加操作
        activityService.saveActivity(activity);

    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    public List<T> pageList(ActivityVo activityVo) {
        List<T> list = activityService.selectActivity(activityVo);
        return list;
    }

}
