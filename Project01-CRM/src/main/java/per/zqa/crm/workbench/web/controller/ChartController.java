package per.zqa.crm.workbench.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.vo.PageVo;
import per.zqa.crm.workbench.service.TranService;

import java.util.Map;

@Controller
@RequestMapping("/workbench/chart")
public class ChartController {

    @Autowired
    private TranService tranService;

    @RequestMapping("/transaction/getCharts.do")
    @ResponseBody
    public PageVo<Map<String, Object>> getCharts() throws AjaxRequestException {
        // 调用tranService
        PageVo<Map<String, Object>> pageVo = tranService.getCharts();
        return pageVo;
    }

}
