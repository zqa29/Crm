CRM客户管理系统
    一、login.jsp
        访问后台验证登录
        添加登录窗口为顶层窗口

    二、workbench/index.jsp
        修改工作区的显示页面
        修改显示客户姓名(从session域中取得)
        修改客户姓名页面显示
        添加退出系统操作

    三、activity/index.jsp
        修改触发模态窗口操作，不能写死在元素当中
        触发模态窗口之前需要过一边后台，通过ajax来拿取数据
        默认下拉选项选择张三
        导入时间控制器，给时间class属性加上time的值
        dataDismiss表示关闭模态窗口
        市场活动列表进行分页展现
        加入分页插件
        加入修改模态窗口的隐藏id属性
        修改后的数据应该在第一条，删除后的数据应该回到第一页
        使用传统请求跳转到详细信息页

    四、activity/detail.jsp
        采用el表达式从session域中获取数据
        采用append方法或before方法追加兄弟标签
        加入编辑修改图标并改成红色(图标都来自于bootstrap)
        javascript:void(0);只能触发事件使用超链接
        动态形参传入函数必须用''廓起来

    五、缓存cache
        内存中的数据
        服务器缓存机制：将数据保存到服务器的内存中
        如果服务器处于开启状态，就一直能够从缓存中取得数据application(全局域，上下文作用域)
        application.setAttribute(),application.getAttribute()

    六、数据字典
        应用程序中，做表单元素选择内容用的相关数据
        *下拉框、单选框、复选框
        两张表tbl_dic_type、tbl_dic_value
        tbl_dic_type code作为主键存放字典类型编码（也是唯一）
        tbl_dic_value value程序员用、text文本客户使用，orderNo排序号，下拉列表，typeCode外键

    七、服务器缓存+数据字典
        不再需要每次都访问数据库，从application中取数据字典
        采用监听器技术，实现ServletContextListener接口
        获取application向域中保存value对象
        按照typeCode分类

    八、clue/index.jsp
        加入jstl，数据可以直接从缓存中获取

    九、clue/detail.jsp
        不通过后台直接传递数据
        id可以用get，字符数不超过浏览器限制也可以
        String fullname = request.getParameter("fullname") --> ${param.fullname}
        上面的param不能省略，否则就是从四大域中取值
        el没有request，只能通过pageContext获取
        还可以获取其他的内置对象

    十、clue/convert.ksp
        添加关闭和提交按钮
        提交按钮后需要将name赋值，还要再加一个id赋值到隐藏域
        提交线索到后台进行转换，发出传统请求
        添加标记判断是否需要提交表单

    十一、transaction/save.jsp
        创建交易页面通过el表达式让所有者默认选择登录用户
        ${user}从四个域取出user对象，输出的浏览器
        ${"user"}把普通的字符串"user"输出到浏览器
        添加自动补全插件
        处理可能性，与阶段一一对应，自动赋值，不需要客户去填
            1.数据量不是很大
            2.一种健值对对应关系
            不需要保存到数据库，保存到properties文件中
            （注意properties文件中，中文需要转码-->java/bin/native2ascii.exe）
            该文件在交易模块需要大量使用到，将其解析到服务器缓存中
        将map处理成json
        json进行取值是可变变量的话，需要json[key]进行取值
