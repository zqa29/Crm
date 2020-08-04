<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String basePath = request.getScheme() + "://"
		+ request.getServerName() + ":"
		+ request.getServerPort()
		+ request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
	<meta charset="UTF-8">
	<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
	<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
	<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script>
		$(function () {
			// 如果当前窗口不是顶级窗口将它变为顶级窗口
			if (window.top != window) {
				window.top.location = window.location;
			}

			// 页面加载完毕后将文本框的内容清空
			$("#loginAct").val("");

			// 让用户名文本框自动获得焦点
			$("#loginAct").focus();

			// 为登录按钮绑定事件，执行登录操作
			$("#submitBtn").click(function () {
				loginVer();
			});

			// 为当前登陆窗口绑定键盘按下事件
			$(window).keydown(function (event) {
				if (event.keyCode == 13) { // 捕捉回车键值（回车能进行提交）
					loginVer();
				}
			});



		});

		// 验证登录方法封装(写在function之外)
		function loginVer() {
			// 使用trim去掉账号密码左右空格
			let loginAct = $.trim($("#loginAct").val());
			let loginPwd = $.trim($("#loginPwd").val());
			// 验证账号密码是否为空
			if (loginAct == "" || loginPwd == "") {
				// 有一方为空都为错误进行提示以及强制终止
				$("#msg").html("账号密码不能为空");
				return false;
			}

			// 验证免登录是否选中
			let loginCheck = "";
			if ($("#loginCheck").prop("checked")) {
				// 如果被选中修改loginCheck的值
				loginCheck = "selected";
			}


			// 去后台验证登录相关操作
			$.ajax({
				url:"settings/user/login.do",
				data:{
					"loginAct":loginAct,
					"loginPwd":loginPwd,
					"loginCheck":loginCheck // 添加免登录参数
				},
				type:"post",
				dataType:"json",
				success:function (data) {
					if (data.success) {
						// 登录成功跳转到工作台的初始地
						window.location.href = "workbench/index.jsp";
					} else {
						$("#msg").html(data.msg);
					}

				}
			})
		}
	</script>
</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2019&nbsp;蛙课网</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="jsp/workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						<label>
							<input type="checkbox" id="loginCheck" value="selected"> 十天内免登录
						</label>
						&nbsp;&nbsp;
						<span id="msg" style="color: red"></span>
					</div>
					<button  type="button" class="btn btn-primary btn-lg btn-block"  id="submitBtn" style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>