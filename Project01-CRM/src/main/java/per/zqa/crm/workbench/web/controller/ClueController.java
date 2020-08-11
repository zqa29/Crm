package per.zqa.crm.workbench.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.exception.TraditionRequestException;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;
import per.zqa.crm.utils.DateTimeUtil;
import per.zqa.crm.utils.HandleFlag;
import per.zqa.crm.utils.UUIDUtil;
import per.zqa.crm.workbench.domain.Activity;
import per.zqa.crm.workbench.domain.Clue;
import per.zqa.crm.workbench.domain.Customer;
import per.zqa.crm.workbench.domain.Tran;
import per.zqa.crm.workbench.service.ActivityService;
import per.zqa.crm.workbench.service.ClueService;
import per.zqa.crm.workbench.service.CustomerService;
import per.zqa.crm.workbench.service.TranService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/clue")
public class ClueController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClueService clueService;
    @Autowired
    private TranService tranService;
    @Autowired
    private CustomerService customerService;

    @RequestMapping("/getUserList.do")
    @ResponseBody
    public List<User> getUserList(){
        List<User> userList = userService.getUserList();
        return userList;
    }

    @RequestMapping("/saveClue.do")
    public Map<String,Object> saveClue(Clue clue) throws AjaxRequestException {
        // 生成id
        clue.setId(UUIDUtil.getUUID());
        // 生成时间
        clue.setCreateTime(DateTimeUtil.getSysTime());
        // 调用方法
        boolean b = clueService.saveClue(clue);
        if (b == false) {
            // 保存失败抛出异常
            throw new AjaxRequestException("线索保存失败");
        }
        return HandleFlag.successTrue();
    }

    @RequestMapping("/getDetail.do")
    @ResponseBody
    public ModelAndView getDetail(String id) throws TraditionRequestException {
        ModelAndView mav = new ModelAndView();

        // 通过id获取指定的详细线索
        Clue clue = clueService.getDetail(id);
        if (clue == null) {
            throw new TraditionRequestException("获取详细线索失败！");
        }

        // 将获取到的对象放入request域
        mav.addObject("clue", clue);

        // 转发到detail页面
        mav.setViewName("workbench/clue/detail");

        return mav;
    }

    @RequestMapping("/getActivityListByClueId.do")
    @ResponseBody
    public List<Activity> getActivityListByClueId(String clueId) throws AjaxRequestException {
        // 根据clue的id获取到相关联的市场活动list
        List<Activity> aList = activityService.getActivityListByClueId(clueId);

        if (aList == null) {
            throw new AjaxRequestException("获取市场活动列表失败");
        }

        return aList;
    }

    @RequestMapping("/deleteRelationById.do")
    public Map<String,Object> deleteRelationById(String id) throws AjaxRequestException {
        boolean b = clueService.deleteRelationById(id);
        if (b == false) {
            throw new AjaxRequestException("删除关联列表失败");
        }
        return HandleFlag.successTrue();

    }

    @RequestMapping("/getActivityListNotByClueId.do")
    @ResponseBody
    public List<Activity> getActivityListNotByClueId(String condition, String clueId) throws AjaxRequestException {
        // 调用方法
        List<Activity> aList = activityService.getActivityListNotByClueId(condition, clueId);
        if (aList == null) {
            throw new AjaxRequestException("查询失败");
        }
        return  aList;
    }

    @RequestMapping("/boundRelation.do")
    public Map<String, Object> boundRelation(String cId, @RequestParam("aId") String[] aIds) throws AjaxRequestException {
        boolean b = false;
        // 遍历数组分别传入参数
        for (String aId: aIds) {
            // 生成id
            String uuid = UUIDUtil.getUUID();
            // 将字符串拆开
            b = clueService.boundRelation(uuid, cId, aId);
            if (b == false) {
                throw new AjaxRequestException("关联失败");
            }
        }

        return HandleFlag.successTrue();
    }

    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    public List<Activity> getActivityListByName(String condition) throws AjaxRequestException {
        List<Activity> aList = activityService.getActivityListByName(condition);
        if (aList == null) {
            // 查询失败抛出异常
            throw new AjaxRequestException("查询转换区域市场活动失败");
        }
        return aList;
    }

    @RequestMapping("/changeConvert.do")
    public ModelAndView changeConvert(String flag, String clueId, String createBy, Tran tran) throws TraditionRequestException {
        boolean b = true;
        // 判断是否需要创建交易
        if ("a".equals(flag)) {
            // 有添加交易,传入tran调用saveClueFromConvert方法
            b = clueService.saveCustomerByConvert(clueId, createBy, tran);
        } else {
            // 没有添加交易,把tran赋值为null
            b = clueService.saveCustomerByConvert(clueId, createBy, null);
        }
        // 判断是否成功
        if (!b) {
            // 转换失败抛出异常
            throw new TraditionRequestException("保存联系人失败");
        }

        // 创建ModelAndView对象
        ModelAndView mav = new ModelAndView();

        // 设置视图
        mav.setViewName("workbench/clue/index");

        return mav;
    }

}
