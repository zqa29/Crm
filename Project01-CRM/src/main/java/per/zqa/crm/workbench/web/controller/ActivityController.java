package per.zqa.crm.workbench.web.controller;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import per.zqa.crm.exception.AjaxRequestException;
import per.zqa.crm.exception.TraditionRequestException;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
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

    @RequestMapping("/exportActivityAll.do")
    public void exportActivityAll(HttpServletResponse response) throws IOException, TraditionRequestException{
        // 调用service获取市场活动列表
        List<Activity> activityList = activityService.getActivityList();

        // 数据量不大采用03版速度快
        Workbook workbook = new HSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet();
        // 创建第一行
        Row row = sheet.createRow(0);

        // 创建第一格并赋值
        Cell cell = row.createCell(0);
        cell.setCellValue("id");
        // 第二格
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        // 第三格
        cell = row.createCell(2);
        cell.setCellValue("名称");
        // 第四格
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        // 第五格
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        // 第六格
        cell = row.createCell(5);
        cell.setCellValue("成本");
        // 第七格
        cell = row.createCell(6);
        cell.setCellValue("描述");
        // 第八格
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        // 第九格
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        // 第十格
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        // 第11格
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        // for循环采用值进行数据写入
        for(int i=0;i<activityList.size();i++){
            Activity a = activityList.get(i);
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(a.getId());
            cell = row.createCell(1);
            cell.setCellValue(a.getOwner());
            cell = row.createCell(2);
            cell.setCellValue(a.getName());
            cell = row.createCell(3);
            cell.setCellValue(a.getStartDate());
            cell = row.createCell(4);
            cell.setCellValue(a.getEndDate());
            cell = row.createCell(5);
            cell.setCellValue(a.getCost());
            cell = row.createCell(6);
            cell.setCellValue(a.getDescription());
            cell = row.createCell(7);
            cell.setCellValue(a.getCreateBy());
            cell = row.createCell(8);
            cell.setCellValue(a.getCreateTime());
        }

        //为客户浏览器提供下载框
        response.setContentType("octets/stream");
        response.setHeader("Content-Disposition","attachment;filename=Activity-"+DateTimeUtil.getSysTime()+".xls");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        // 关闭流
        workbook.close();
    }

    @RequestMapping("/exportActivitySelected.do")
    public void exportActivitySelected(@RequestParam("id")String[] ids, HttpServletResponse response) throws IOException, TraditionRequestException{
        // 调用service获取市场活动列表
        List<Activity> activityList = activityService.getActivityListById(ids);

        // 数据量不大采用03版速度快
        Workbook workbook = new HSSFWorkbook();
        // 创建工作表
        Sheet sheet = workbook.createSheet();
        // 创建第一行
        Row row = sheet.createRow(0);

        // 创建第一格并赋值
        Cell cell = row.createCell(0);
        cell.setCellValue("id");
        // 第二格
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        // 第三格
        cell = row.createCell(2);
        cell.setCellValue("名称");
        // 第四格
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        // 第五格
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        // 第六格
        cell = row.createCell(5);
        cell.setCellValue("成本");
        // 第七格
        cell = row.createCell(6);
        cell.setCellValue("描述");
        // 第八格
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        // 第九格
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        // 第十格
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        // 第11格
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        // for循环采用值进行数据写入
        for(int i=0;i<activityList.size();i++){
            Activity a = activityList.get(i);
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(a.getId());
            cell = row.createCell(1);
            cell.setCellValue(a.getOwner());
            cell = row.createCell(2);
            cell.setCellValue(a.getName());
            cell = row.createCell(3);
            cell.setCellValue(a.getStartDate());
            cell = row.createCell(4);
            cell.setCellValue(a.getEndDate());
            cell = row.createCell(5);
            cell.setCellValue(a.getCost());
            cell = row.createCell(6);
            cell.setCellValue(a.getDescription());
            cell = row.createCell(7);
            cell.setCellValue(a.getCreateBy());
            cell = row.createCell(8);
            cell.setCellValue(a.getCreateTime());
        }

        //为客户浏览器提供下载框
        response.setContentType("octets/stream");
        response.setHeader("Content-Disposition","attachment;filename=Activity-"+DateTimeUtil.getSysTime()+".xls");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        // 关闭流
        workbook.close();
    }

    @RequestMapping("/importActivity.do")
    @ResponseBody
    public Map<String, Object> importActivity(MultipartFile myFile, HttpServletRequest request) throws IOException, AjaxRequestException {
        List<Activity> aList = new ArrayList<>();
        // 定义文件名称，由于是导入数据，可能大数据，采用SXSSF读取.xlsx结尾
        String fileName = DateTimeUtil.getSysTimeForUpload()+".xls";

        // 获取真实路径
        String path = request.getServletContext().getRealPath("/tmp");
        System.out.println("------------------------------"+ path +"=======================");

        // 将临时file编译到指定目录指定文件名
        myFile.transferTo(new File(path+"/"+fileName));

        // 从指定目录指定文件读取创建输入流
        InputStream fis = new FileInputStream(path+"/"+fileName);

        // 创建SXSSF对象(缓存200条)
        Workbook workbook = new HSSFWorkbook(fis);

        // 读取工作表
        Sheet sheet = workbook.getSheetAt(0);

        // 读取行
        Row row = null;

        // 获取工作表最后一行，
        // 遍历取出每一行
        for(int i = 1;i < sheet.getLastRowNum() + 1; i++) {
            Activity a = new Activity();
            row = sheet.getRow(i);
            a.setId(UUIDUtil.getUUID());
            Cell cell = null;
            // 遍历取出每行中的单元格
            for (int j = 1; j < row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                // 采用switch给单元格赋值
                switch (j) {
                    case 1:
                        a.setOwner(cell.getStringCellValue());
                        break;
                    case 2:
                        a.setName(cell.getStringCellValue());
                        break;
                    case 3:
                        a.setStartDate(cell.getStringCellValue());
                        break;
                    case 4:
                        a.setEndDate(cell.getStringCellValue());
                        break;
                    case 5:
                        a.setCost(cell.getStringCellValue());
                        break;
                    case 6:
                        a.setDescription(cell.getStringCellValue());
                        break;
                }
            }
            // 待所有属性赋值完毕将市场活动对象放入集合中
            aList.add(a);
        }
        // 调用service方法保存数据
        activityService.saveActivityByList(aList);
        // 关闭流
        workbook.close();
        return HandleFlag.successTrue();
    }
}
