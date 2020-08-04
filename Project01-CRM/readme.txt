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