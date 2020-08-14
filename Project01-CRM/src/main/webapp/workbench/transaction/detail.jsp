<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="per.zqa.crm.settings.domain.DicValue" %>
<%@ page import="per.zqa.crm.workbench.domain.Tran" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% String basePath = request.getScheme() + "://"
		+ request.getServerName() + ":"
		+ request.getServerPort()
		+ request.getContextPath() + "/";

	// 阶段与可能性对应关系
	Map<String,String> proMap = (Map<String, String>) application.getAttribute("proMap");

	// 取得proMap中的key集合
	Set<String> set = proMap.keySet();

	// 取得字典类型为stage的字典值集合
	List<DicValue> stageList = (List<DicValue>) application.getAttribute("stage");

	// 定义临界点
	int point = 0;

	// 遍历集合获取临界点
	for (int i = 0; i < stageList.size(); i++) {
		// 取得每一个字典值
		DicValue dicValue = stageList.get(i);

		// 取得字典值的value属性，即为proMap中的key
		String proMapKey = dicValue.getValue();

		// 根据key取得对应的可能性
		String possibility = proMap.get(proMapKey);

		// 判断可能性的值
		if ("0".equals(possibility)) {
			// 找到临界点，给分界点赋值
			point = i;
			break; // 跳出循环
		}
	}

%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">
.mystage{
	font-size: 20px;
	vertical-align: middle;
	cursor: pointer;
}
.closingDate{
	font-size : 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	let cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#e6e6e6");
		});

		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		// 展现交易历史列表
		showHistoryList();
	});

	function showHistoryList() {
		// ajax
		$.ajax({
			url:"workbench/transaction/getHistoryListByTranId.do",
			data:{
				"tranId":"${tran.id}"
			},
			type:"get",
			dataType:"json",
			success(data){
				let html = "";
				// 遍历List
				$.each(data, function (i, n) {
					html += "<tr>";
					html += "<td>"+ n.stage +"</td>";
					html += "<td>"+ n.money +"</td>";
					html += "<td>"+ n.possibility +"</td>";
					html += "<td>"+ n.expectedDate +"</td>";
					html += "<td>"+ n.createTime +"</td>";
					html += "<td>"+ n.createBy +"</td>";
					html += "</tr>";
				})

				// 写入
				$("#activityTableTbody").html(html);
			}
		})

	}

	function changeIcon(stage, i) {
		// 当前阶段
		let currentStage = stage;

		// 当前阶段的可能性
		let currentPossibility = $("#possibility").html();

		// 当前阶段额下标
		let index = i;

		// 分界点下标
		let point = "<%=point%>";

		alert(currentStage)
		alert(currentPossibility)
		alert(index)
		alert(point)

	}

	// 改变当前交易阶段，需要改变当前阶段的下标
	function changeStage(stage,i) {
		// ajax
		$.ajax({
			url:"workbench/transaction/updateStage.do",
			data: {
				"id":"${tran.id}",
				"stage":stage,
				// 修改人为当前登录用户
				"editBy":"${user.name}",
				// 交易历史需要的信息
				"money":"${tran.money}",
				"expectedDate":"${tran.expectedDate}"
			},
			type:"post",
			dataType:"json",
			success(data){
				if (data.success) {
					// 成功，刷新阶段、可能性、修改人、修改时间
					// 取得要修改的标签并进行赋值
					$("#stage").html(data.tranVo.stage);
					$("#possibility").html(data.tranVo.possibility);
					$("#editBy").html(data.tranVo.editBy);
					$("#editTime").html(data.tranVo.editTime);

					// 将所有的阶段图标重新判断，重新赋予样式及颜色
					changeIcon(stage,i);
				} else {
					alert("改变交易历史阶段失败")
				}
			}
		})
	}
	
	
	
</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${tran.customerId}-${tran.name} <small>￥${tran.money}</small></h3>
		</div>
		
	</div>

	<br/>
	<br/>
	<br/>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%
			// 取得当前阶段
			Tran tran = (Tran) request.getAttribute("tran");
			String currentStage = tran.getStage();

			// 取得当前阶段的可能性
			String currentPossibility = proMap.get(currentStage);

			// 判断当前阶段
			if (!"0".equals(currentPossibility)) {
				// 当前阶段可能性不为0，前7个可能是绿圈、绿色标记、黑圈，后面两个一定是黑叉
				// 准备当前阶段下标
				int currentPoint = 0;

				// 遍历阶段取得下标
				for (int i = 0; i < stageList.size(); i++) {
					// 取得当前阶段的value
					DicValue dicValue = stageList.get(i);
					String listStage = dicValue.getValue();
					// 判断是否当前阶段
					if (listStage.equals(currentStage)) {
						// 是当前阶段
						currentPoint = i;
						break; // 跳出循环
					}
				}

				// 遍历集合，取出每个阶段的可能性
				for (int i = 0; i < stageList.size(); i++) {
					DicValue dicValue = stageList.get(i);
					String listStage = dicValue.getValue();
					String possibility = proMap.get(listStage);

					// 判断
					if (!"0".equals(possibility)) {
						// 前七个，前7个可能是绿圈、绿色标记、黑圈，后面两个一定是黑叉
						// 判断当前阶段是否第一个
						if (i == currentPoint) {
							// 绿色标记
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
			  class="glyphicon glyphicon-map-marker mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dicValue.getText()%>" style="color: #90F790;"></span>
		-----------
		<%
						} else if (i < currentPoint) {
							// 绿圈
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
			  class="glyphicon glyphicon-ok-circle mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dicValue.getText()%>" style="color: #90F790;"></span>
		-----------
		<%
						} else if (i > currentPoint) {
							// 黑圈
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
			  class="glyphicon glyphicon-record mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
						}
					} else {
						// 后两个，都是黑叉。
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
			  class="glyphicon glyphicon-remove mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
					}
				}
			} else {
				// 当前阶段为0，则前7个一定是黑圈，后两个一个是红叉、一个是黑叉。
				// 遍历集合，取出每个阶段的可能性
				for (int i = 0; i < stageList.size(); i++) {
					DicValue dicValue = stageList.get(i);
					String listStage = dicValue.getValue();
					String possibility = proMap.get(listStage);

					// 判断
					if ("0".equals(possibility)) {
						// 后两个，一个是红叉、一个是黑叉。
						// 判断是否当前阶段
						if (listStage.equals(currentStage)) {
							// 当前阶段，为红叉
		%>
			<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
				  class="glyphicon glyphicon-remove mystage"
				  data-toggle="popover" data-placement="bottom"
				  data-content="<%=dicValue.getText()%>" style="color: #FF0000;"></span>
			-----------
		<%
						} else {
							// 非当前阶段，为黑叉
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
			  class="glyphicon glyphicon-remove mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
						}
					} else {
						// 前七个，全部是黑圈
		%>
		<span id="<%=i%>" onclick="changeStage('<%=listStage%>', '<%=i%>')"
			  class="glyphicon glyphicon-record mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dicValue.getText()%>" style="color: #000000;"></span>
		-----------
		<%
					}

				}
			}
		%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="资质审查" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="需求分析" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="价值建议" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="确定决策者" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-map-marker mystage" data-toggle="popover" data-placement="bottom" data-content="提案/报价" style="color: #90F790;"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="谈判/复审"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="成交"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="丢失的线索"></span>--%>
<%--		-------------%>
<%--		<span class="glyphicon glyphicon-record mystage" data-toggle="popover" data-placement="bottom" data-content="因竞争丢失关闭"></span>--%>
<%--		-------------%>
		<span class="closingDate">${tran.expectedDate}</span>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.money}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}-${tran.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${tran.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.type}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">${tran.possibility}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.activityId}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.createBy} &nbsp;</b><small style="font-size: 10px; color: gray;">${tran.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${tran.editBy}&nbsp;&nbsp;</b><small id="editTime" style="font-size: 10px; color: gray;">${tran.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${tran.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${tran.contactSummary}&nbsp;
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.nextContactTime}&nbsp;</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>蛙课网-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>蛙课网-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>阶段历史</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>阶段</td>
							<td>金额</td>
							<td>可能性</td>
							<td>预计成交日期</td>
							<td>创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="activityTableTbody">
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>