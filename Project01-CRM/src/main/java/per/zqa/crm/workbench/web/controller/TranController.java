package per.zqa.crm.workbench.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.exception.TraditionRequestException;
import per.zqa.crm.settings.domain.User;
import per.zqa.crm.settings.service.UserService;
import per.zqa.crm.utils.HandleFlag;
import per.zqa.crm.workbench.domain.Tran;
import per.zqa.crm.workbench.domain.TranHistory;
import per.zqa.crm.workbench.service.CustomerService;
import per.zqa.crm.workbench.service.TranService;
import per.zqa.crm.workbench.vo.TranVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/transaction")
public class TranController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TranService tranService;

    @RequestMapping("/add.do")
    public ModelAndView add() {
        // 创建ModelAndView对象
        ModelAndView mav = new ModelAndView();

        // 调用service获取集合
        List<User> userList = userService.getUserList();

        // 因是传统请求，将集合放入到request域中
        mav.addObject(userList);

        // 设置视图
        mav.setViewName("workbench/transaction/save");

        return mav;
    }

    @RequestMapping("/getCustomerName.do")
    @ResponseBody
    public List<String> getCustomerName(String name) throws AjaxRequestException {
        // 调用service方法
        List<String> nameList = customerService.getCustomerName(name);
        return nameList;
    }

    @RequestMapping("/saveTran.do")
    public ModelAndView saveTran(String customerName, Tran tran) throws TraditionRequestException {
        ModelAndView mav = new ModelAndView();

        // 调用tranService
        tranService.saveTran(customerName, tran);

        // 设置视图
        mav.setViewName("redirect:/workbench/transaction/index.jsp");

        return mav;
    }

    @RequestMapping("/tranDetail.do")
    public ModelAndView tranDetail(String id, HttpServletRequest request) throws TraditionRequestException {
        ModelAndView mav = new ModelAndView();

        // 调用service
        Tran tran = tranService.tranDetail(id);

        // Tran新建一个possibility属性，将可能性赋值到属性中
        String stage = tran.getStage();
        Map<String,String> proMap = (Map<String, String>) request.getServletContext().getAttribute("proMap");
        tran.setPossibility(proMap.get(stage));

        // 放入域
        mav.addObject(tran);

        // 设置视图
        mav.setViewName("workbench/transaction/detail");

        return mav;
    }

    @RequestMapping("/getHistoryListByTranId.do")
    @ResponseBody
    public List<TranHistory> getHistoryListByTranId(String tranId, HttpServletRequest request) {
        // 调用service
        List<TranHistory> thList = tranService.getHistoryListByTranId(tranId);

        // 阶段与可能性map（否则每一次遍历都需要取出）
        Map<String,String> proMap = (Map<String, String>) request.getServletContext().getAttribute("proMap");
        // 遍历集合，TranHistory新建一个possibility属性，将可能性赋值到属性中
        for (TranHistory tranHistory: thList) {
            String stage = tranHistory.getStage();
            tranHistory.setPossibility(proMap.get(stage));
        }

        return thList;
    }

    @RequestMapping("/updateStage.do")
    public Map<String,Object> updateStage(String id, String stage, String editBy, String money, String expectedDate, HttpServletRequest request) throws AjaxRequestException {
        // 调用service
        TranVo tranVo = tranService.updateStage(id, stage, editBy, money, expectedDate);

        // 补全tranVo
        String possibility = ((Map<String, String>) request.getServletContext().getAttribute("proMap")).get(stage);
        tranVo.setPossibility(possibility);

        // 传入vo
        return HandleFlag.successObj("tranVo", tranVo);
    }
}
