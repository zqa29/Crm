<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){
		// 日历插件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-left"
		});

		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});

		// 为打开搜索按钮绑定事件
		$("#openSearchModal").click(function () {
			// 打开模态窗口
			$("#searchActivityModal").modal("show");

		})

		// 为搜索操作模态窗口的搜索框绑定事件，执行搜索并展现市场活动列表的操作
		$("#searchId").keydown(function (event) {
			if (event.keyCode == 13) {
				$.ajax({
					url:"workbench/clue/getActivityListByName.do",
					data: {
						"condition":$.trim($("#searchId").val())
					},
					type:"get",
					dataType:"json",
					success(data){
						let html = "";

						// 遍历
						$.each(data, function (i,n) {
							html += '<tr>';
							html += '<td><input type="radio" name="select" value="'+ n.id +'"/></td>';
							html += '<td id="'+ n.id +'">'+ n.name +'</td>';
							html += '<td>'+ n.startDate +'</td>';
							html += '<td>'+ n.endDate +'</td>';
							html += '<td>'+ n.owner +'</td>';
							html += '</tr>';
						})

						// 写入
						$("#searchTBody").html(html);
					}
				})

				// 展现完列表后，禁用模态窗口的默认回车行为
				return false;
			}
		})

		// 为搜索操作模态窗口的提交按钮绑定事件，填充市场活动源(名字+id)
		$("#submitBtn").click(function () {
			// 取得选中的id
			let $s = $("input[name=select]:checked");
			let id = $s.val();

			// 取得选中市场活动的名字
			let name = $("#"+id).html();

			// 将以上两项信息填充到交易表单市场活动源中
			$("#activity").val(name);
			$("#activityId").val(id);

			// 关闭模态窗口
			$("#searchActivityModal").modal("hide");
		})

		// 为转换按钮绑定事件，执行线索转化操作
		$("#changeId").click(function () {
			// 判断是否需要创建交易
			if ($("#isCreateTransaction").prop("checked")) {
				// 需要创建交易clueId+交易表单的信息
				<%--window.location.href = "workbench/clue/changeConvert.do?clueId=${param.id}";--%>
				// 以上方式表单一扩充，挂载的参数有可能超出浏览器地址栏的上限
				// 使用提交交易表单，不用手动挂加，而且还可以提交post请求
				$("#tranFormId").submit();
			} else {
				// 不需要创建交易,只需要clueId
				window.location.href = "workbench/clue/changeConvert.do?clueId=${param.id}";
			}

			// 发出传统请求

		})
	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="searchId" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="searchTBody">
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary" id="submitBtn">提交</button>
				</div>
			</div>
		</div>
	</div>

	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<h4>转换线索 <small>${param.fullname}${param.appellatino}-${param.company}</small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：${param.company}
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建联系人：${param.fullname}${param.appellatino}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #f7f7f7; display: none;" >
	
		<form id="tranFormId" action="workbench/clue/changeConvert.do" method="post">
			<input type="hidden" name="flag" value="a">
			<input type="hidden" name="clueId" value="${param.id}">
			<input type="hidden" name="createBy" value="${user.name}">
		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" id="amountOfMoney" name="money">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" id="tradeName" name="name">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" id="expectedClosingDate" name="expectedDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage"  class="form-control" name="stage">
		    	<option></option>
		    	<c:forEach items="${stage}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
		    </select>
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activity">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);"  id="openSearchModal" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
		    <input type="text" class="form-control" id="activity" placeholder="点击上面搜索" readonly>
			  <input type="hidden" id="activityId" name="activityId">
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b>${param.owner}</b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary"  id="changeId" type="button" value="转换">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>