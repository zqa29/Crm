package per.zqa.crm.workbench.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;
import per.zqa.crm.utils.DateTimeUtil;
import per.zqa.crm.utils.HandleFlag;
import per.zqa.crm.utils.UUIDUtil;
import per.zqa.crm.vo.ActivityVo;
import per.zqa.crm.vo.PageVo;
import per.zqa.crm.workbench.domain.Activity;
import per.zqa.crm.workbench.domain.ActivityRemark;
import per.zqa.crm.workbench.service.ActivityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public PageVo<Activity> pageList(ActivityVo activityVo) {
        // 取得页码和页面显示记录数并转换成Integer
        String pageNoStr = activityVo.getPageNo();
        Integer pageSize = activityVo.getPageSize();
        Integer pageNO = Integer.valueOf(pageNoStr);

        // 计算得出需要过滤的条数并进行赋值
        Integer skipCount = (pageNO - 1) * pageSize;
        activityVo.setSkipCount(skipCount);

        // 调用activityService的selectActivity方法
        PageVo<Activity> pageVo = activityService.selectActivity(activityVo);

        return pageVo;
    }

    @RequestMapping("/deleteActivity.do")
    public Map<String, Object> deleteActivity(HttpServletRequest request) {
        // 获取id数组
        String[] ids = request.getParameterValues("id");

        // 调用activityService.deleteActivity方法
        boolean b = activityService.deleteActivity(ids);

        if (b == true) {
            // 删除成功
            return HandleFlag.successTrue();
        }
        return null;
    }

    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    public Map<String, Object> getUserListAndActivity(String id) {
        // 分别调用相应方法获取
        Activity activity = activityService.getActivityById(id);
        id = activity.getOwner();
        User user = userService.getUserById(id);

        // 因为该数据（功能）不常用，采用临时方法放入Map中
        Map<String,Object> map = new HashMap<>();
        map.put("user",user);
        map.put("activity",activity);

        return map;
    }

    @RequestMapping("/updateActivity.do")
    public Map<String,Object> updateActivity(Activity activity, HttpSession session) throws AjaxRequestException {
        // 获取当前系统时间
        activity.setEditTime(DateTimeUtil.getSysTime());
        // 获取当前登录的用户名
        String userName = ((User) session.getAttribute("user")).getName();
        activity.setEditBy(userName);
        // 调用方法
        boolean b = activityService.updateActivity(activity);
        if (b == false) {
            // 更新失败抛出异常
            throw new AjaxRequestException("更新失败");
        }
        return HandleFlag.successTrue();
    }

    @RequestMapping("/activityDetail.do")
    public ModelAndView getActivityDetail(String id) {
        // 调用方法
        Activity activity = activityService.getActivityDetailById(id);

        // 使用传统请求创建ModelAndView对象
        ModelAndView mav = new ModelAndView();
        // 添加到request域
        mav.addObject("activity", activity);
        // 请求转发
        mav.setViewName("workbench/activity/detail");

        return mav;
    }

    @RequestMapping("/getRemarkListByAId.do")
    @ResponseBody
    public List<ActivityRemark> getRemarkListByAId(String aId) {
        // 调用方法获取
        List<ActivityRemark> remarkList = activityService.getRemarkListByAId(aId);
        return remarkList;
    }

    @RequestMapping("/deleteRemark.do")
    public Map<String,Object> deleteRemark(String id) throws AjaxRequestException {
        // 调用方法
        boolean b = activityService.deleteRemark(id);
        if (b == false) {
            // 删除失败抛出异常
            throw new AjaxRequestException("删除失败");
        }
        return HandleFlag.successTrue();
    }

    @RequestMapping("/saveRemark.do")
    public Map<String,Object> saveRemark(ActivityRemark activityRemark) throws AjaxRequestException {
        activityRemark.setId(UUIDUtil.getUUID());
        activityRemark.setCreateTime(DateTimeUtil.getSysTime());
        activityRemark.setEditFlag("0");
        boolean b = activityService.saveRemark(activityRemark);
        if (b == false) {
            // 保存失败，抛出异常
            throw new AjaxRequestException("保存失败");
        }
        // 保存成功调用工具类方法，返回对象
        return HandleFlag.successObj("activityRemark", activityRemark);
    }

    @RequestMapping("/updateRemark.do")
    public Map<String,Object> updateRemark(ActivityRemark activityRemark) throws AjaxRequestException {
        activityRemark.setEditFlag("1");
        activityRemark.setEditTime(DateTimeUtil.getSysTime());
        boolean b = activityService.updateRemark(activityRemark);
        if (!b) {
            // 更新失败，抛出异常
            throw new AjaxRequestException("更新失败");
        }
        return HandleFlag.successObj("activityRemark", activityRemark);
    }
}
