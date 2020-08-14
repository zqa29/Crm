package per.zqa.crm.web.listener;

import org.springframework.web.context.support.WebApplicationContextUtils;
import per.zqa.crm.settings.domain.DicValue;
import per.zqa.crm.settings.service.DicService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {

    /*
        该方法用来监听上下文域对象的方法，当服务器启动，application对象创建完毕后，立马执行该方法
        event:
            该参数能够取得监听的对象
     */
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("------------------------------服务器缓存处理数据字典开始---------------------------");
        // 获取上下文对象
        ServletContext application = event.getServletContext();

        //使用 WebApplicationContextUtils 工具类,获取到spring容器的引用,进而获取到我们需要的bean实例(DicService)
        DicService dicService = WebApplicationContextUtils.getWebApplicationContext(application).getBean(DicService.class);

        // 调用业务层获取返回的map
        Map<String, List<DicValue>> dicMap = dicService.getAll();

        // 解析map
        Set<String> keySet = dicMap.keySet();
        for (String key: keySet) {
            // 将分好类的数据字典保存到服务器缓存中
            application.setAttribute(key, dicMap.get(key));

        }

        System.out.println("------------------------------服务器缓存处理数据字典结束---------------------------");


        System.out.println("------------------------------服务器缓存处理阶段可能性开始---------------------------");
        Map<String,String> proMap = new HashMap<>();
        // 解析properties文件,能处理中文和安全性高
        ResourceBundle bundle = ResourceBundle.getBundle("/properties/Stage2Possibility");
        Enumeration<String> keys = bundle.getKeys();
        // 迭代枚举
        while (keys.hasMoreElements()) {
            // 阶段
            String key = keys.nextElement();
            // 可能性
            String value = bundle.getString(key);
            proMap.put(key, value);
        }

        // 将分好类的数据字典保存到服务器缓存中
        application.setAttribute("proMap", proMap);
        System.out.println("------------------------------服务器缓存处理阶段可能性结束---------------------------");
    }

}
