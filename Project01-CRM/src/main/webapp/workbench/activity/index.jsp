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
	<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){
        // 日历插件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		// 给创建按钮绑定单击事件
		$("#addBtn").click(function () {

			// 采用ajax去后台取用户数据
			$.ajax({

				url:"workbench/activity/getUserList.do",
				type:"get",
				dataType:"json",
				success(data) {

					let html = "<option></option>";

					// 遍历出来的obj就是每一个user对象
					$.each(data, function (i,obj) {
						// 给用户看的是名字，但实际保存的是id
						html += "<option value='" + obj.id + "'>" + obj.name + "</option>";
					})

					// 给select下拉选框写入选项
					$("#create-owner").html(html);

					// 通过el取得用户id
					let id = "${user.id}";

					// 让下拉选项默认选择张三
					$("#create-owner").val(id);


					// 所有者下来框处理完毕后，展现模态窗口
					// 调用modal方法，打开添加操作的模态窗口
					$("#createActivityModal").modal("show"); // show:显示模态窗口, hide:关闭模态窗口
				},
				error() {
					window.location.href = "index.jsp";
				}
			})

		});

        // 为创建模态窗口的保存按钮绑定事件执行添加操作
        $("#saveBtn").click(function () {
            // 通过ajax来保存
            $.ajax({
                url: "workbench/activity/saveActivity.do",
                data:{
                    "owner":$.trim(($("#create-owner")).val()),
                    "name":$.trim(($("#create-name")).val()),
                    "startDate":$.trim(($("#create-startDate")).val()),
                    "endDate":$.trim(($("#create-endDate")).val()),
                    "cost":$.trim(($("#create-cost")).val()),
                    "description":$.trim(($("#create-description")).val())
                },
                type: "post",
                dataType: "json",
                success(data) {
                    if (data.success) {
                        // 添加成功刷新市场活动信息列表,回到第一页
						pageList(1, $("#activityPage").bs_pagination('getOption', 'rowsPerPage')); // 操作后维持已经设置好的每页展现的记录数
                        // 模态窗口添加完毕之后清空模态窗口之中的数据(需要转换成dom对象再reset)
                        $("#activityAddForm")[0].reset();

                        // 关闭添加操作的模态窗口
                        $("#createActivityModal").modal("hide");
                    } else {

                    }

                }
            })

        });

        // 给修改按钮绑定单击事件
        $("#editBtn").click(function () {
            // 首先找出所有复选框jq对象
            let $sb = $("input[name=selectBox]:checked");

            // 判断复选框选择的条数
            if ($sb.length == 1) {
                // 只选一条则获取id值
                let id = $sb.val();
                // 采用ajax去后台取用户数据
                $.ajax({

                    url:"workbench/activity/getUserListAndActivity.do",
                    data: {"id":id},
                    type:"get",
                    dataType:"json",
                    success(data) {
                        // 处理下拉框
                        let html = "<option></option>";
                        // 给用户看的是名字，但实际保存的是id
                        html += "<option value='" + data.user.id + "'>" + data.user.name + "</option>";
                        // 对下拉框进行写入
                        $("#edit-owner").html(html);
                        // 通过el取得用户id
                        let id = "${user.id}";
                        // 让下拉选项默认选择所选所有者
                        $("#edit-owner").val(id);

                        // 为修改模态窗口写入数据
                        $("#edit-id").val(data.activity.id);
                        $("#edit-name").val(data.activity.name);
                        $("#edit-startDate").val(data.activity.startDate);
                        $("#edit-endDate").val(data.activity.endDate);
                        $("#edit-cost").val(data.activity.cost);
                        $("#edit-description").text(data.activity.description)

                        // 所有值修改完毕后，展现模态窗口
                        // 调用modal方法，打开添加操作的模态窗口
                        $("#editActivityModal").modal("show"); // show:显示模态窗口, hide:关闭模态窗口
                    },
                    error() {
                        window.location.href = "index.jsp";
                    }
                })
            } else if ($sb.length == 0) {
                alert("请选择要修改的记录");
            } else {
                alert("只能选择一条进行修改");
            }

        });

        // 为修改模态窗口的更新按钮绑定事件进行更新
        $("#updateBtn").click(function () {
            // 直接发送ajax请求进行更新
            $.ajax({
                url:"workbench/activity/updateActivity.do",
                data: {
                    "id":$.trim(($("#edit-id")).val()),
                    "owner":$.trim(($("#edit-owner")).val()),
                    "name":$.trim(($("#edit-name")).val()),
                    "startDate":$.trim(($("#edit-startDate")).val()),
                    "endDate":$.trim(($("#edit-endDate")).val()),
                    "cost":$.trim(($("#edit-cost")).val()),
                    "description":$.trim(($("#edit-description")).val())
                },
                type:"post",
                dataType:"json",
                success(data){
                    if (data.success) {
                        alert("更新成功");
                        // 重新调用分页函数
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage') // 操作后停留在当前页
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage')); // 操作后维持已经设置好的每页展现的记录数
                        // 更新完毕后关闭模态窗口
                        $("#editActivityModal").modal("hide");
                    } else {
                        alert(data.message);
                    }
                }
            })


        })

		// 为删除按钮绑定事件，执行市场活动删除操作
		$("#deleteBtn").click(function () {
			// 首先找出所有复选框jq对象
			let $sb = $("input[name=selectBox]:checked");

			if ($sb.length == 0) {
				// 没有选择则弹出提示
				alert("请选择要删除的记录");
			} else {
				// 添加确认删除弹窗
				if (confirm("确定删除所选中的记录吗？")) {
					// 定义参数进行拼接
					let param = "";

					// 遍历jq对象取出对应的id
					for (let i = 0; i < $sb.length; i++) {
						param += "id=" + $($sb[i]).val();

						// 不是最后一个元素，需要在后面追加字符&
						if (i < $sb.length - 1) {
							param += "&";
						}
					}

					// 发出ajax进行删除
					$.ajax({
						url:"workbench/activity/deleteActivity.do",
						data: param,
						type:"post",
						dataType:"json",
						success(data){
							// 判断是否删除成功
							if (data.success) {
								// 删除成功后调用分页函数，刷新记录
								alert("删除成功！");
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage')); // 操作后维持已经设置好的每页展现的记录数
							} else {
								alert("删除市场活动失败");
							}
						}
					})
				}
			}
		})

		// 页面加载完毕后调用分页函数
		pageList(1,5); // 默认展开第一页，每页展现5条记录

		// 为查询按钮绑定事件，触发单击事件调用分页函数
		$("#searchBtn").click(function () {
			// 将搜索框中的信息保存到隐藏域
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,5);
		});

		// 为全选框绑定单击按钮事件
		$("#selectAll").click(function () {
			// 选择input标签下name属性名为selectBox的标签调用prop方法，判断条件为全选框checked，this代表全选框
			$("input[name=selectBox]").prop("checked", this.checked);
		});

		// 给导出全部市场活动按钮绑定单击事件
		$("#exportActivityAllBtn").click(function () {
			// 提示
			if (confirm("确定要导出所有数据吗")) {
				// 必须是传统请求
				window.location.href="workbench/activity/exportActivityAll.do"
			}
		});


		// 给导出选择数市场活动按钮绑定事件
		$("#exportActivityXzBtn").click(function () {
			// 取出选择的选项框
			let $sb = $("input[name=selectBox]:checked");

			// 判断选择的条数
			let param = "";
			if ($sb.length === 0) {
				alert("请选择要导出的数据")
			} else {
				// 遍历取出所有的value（即为市场活动的id）
				for(let i=0;i<$sb.length;i++) {
					param += "id=" + $($sb[i]).val();
					if (i < $sb.length - 1) {
						param += "&";
					}
				}
				// 提示
				if (confirm("确定要导出所有数据吗")) {
					// 必须是传统请求
					window.location.href="workbench/activity/exportActivitySelected.do?" + param;
				}
			}

		})

        // 为导入按钮绑定事件
        $("#importActivityBtn").click(function () {
            //收集参数
            let fileName = $("#activityFile").val();
            // 取得后缀
            let suffix = fileName.substr(fileName.lastIndexOf(".") + 1);
            // 判断后缀是否以xls或xlsx文件结尾
            if(!(suffix==='xls' || suffix==='xlsx')){
                alert("仅支持.xls 或.xlsx 格式的文件!");
                return false;
            }
            //取得文件对象
            let activityFile = $("#activityFile")[0].files[0];
            if(activityFile.size > 1024*1024*80){
                alert("文件大小不超过 80MB!");
                return;
            }
            //发送请求
            //FormData 是 ajax 定义的接口,可以模拟键值对向服务器提交数据
            //FormData 类型的作用是可以提交文本数据,还可以提交二进制数据.
            let formData = new FormData();
            formData.append("myFile",$("#activityFile")[0].files[0]);
            $.ajax({
                url:'workbench/activity/importActivity.do',
                data:formData,
                type:'post',
                // 主要是配合 contentType 使用的,默认情况下,ajax 把所有数据进行 application/x-www-form-urlencoded 编码之前,会把所有数据统一转化为字符串;
                // 把 processData 设置为 false,可以阻止这种行为.
                processData:false,
                //默认情况下,ajax 向服务器发送数据之前,把所有数据统一按照 application/x-www-form-urlencoded 编码格式进行编码;把contentType 设置为 false,能够阻止这种行为.
                contentType:false,
                success:function(data){
                    if(data.success){
                        //提示成功导入记录的条数
                        alert("导入数据成功");
                        $("#activityFile").val("");
                        //关闭模态窗口
                        $("#importActivityModal").modal("hide");
                        //刷新列表
                        pageList(1,2);
                    }else{
                        alert("导入数据失败");
                    }
                }
            });
        })

		/*
			为复选框绑定单击按钮事件
				动态生成的元素，要以on方法的形式来触发事件
				语法:$(需要绑定元素的有效的外层元素).on(绑定事件的方式,需要绑定的元素的jquery对象，回调函数)
		 */
		$("#activityBody").on("click", $("input[name=selectBox]"), function () { // td、tr都无效
			// 选择id名为selectAll的标签调用prop方法，判断条件为复选框的选择数==复选框的总数
			$("#selectAll").prop("checked", $("input[name=selectBox]:checked").length === $("input[name=selectBox]").length);
		});

    });

	/*
		分页展现基础组件pageNo和pageSize
		入口：6个
			1.市场活动超连接
			2.创建、修改、删除，需要刷新市场活动列表
			3.点击查询按钮的时候
			4.点击下方的分页组件的时候
		以上入口执行完毕后需要调用分页函数
	 */
	function pageList(pageNo, pageSize) { // pageNo:页码 pageSize:每页展现的记录数
		// 通过ajax请求访问后台，取数据返回json
		// 查询前，将隐藏域中的信息取出来，重新赋值到搜索框中
		// 每次刷新记录都要将全选框取消选中
		// 直接使用
		// pageList($("#activityPage").bs_pagination('getOption', 'currentPage') // 操作后停留在当前页
		// 		,$("#activityPage").bs_pagination('getOption', 'rowsPerPage')); // 操作后维持已经设置好的每页展现的记录数

		$("#selectAll").prop("checked", false);

		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url:"workbench/activity/pageList.do",
			data: {
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"endDate":$.trim($("#search-endDate").val()),
			},
			type:"get",
			dataType:"json",
			// 需要 1:市场活动数据对象 2:总记录数
			success(data){
				// 定义变量用于标签拼接
				let html = "";

				// 进行遍历拼接市场活动数据列表标签
				$.each(data.dataList, function (i, obj) { // obj就是市场活动对象
					html += '<tr class="active">';
					html += '<td><input type="checkbox" value="' + obj.id + '" name="selectBox"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/activityDetail.do?id='+ obj.id +'\';" >' + obj.name + '</a></td>';
					html += '<td>' + obj.owner + '</td>';
					html += '<td>' + obj.startDate +'</td>';
					html += '<td>' + obj.endDate + '</td>' ;
					html += '</tr>';
				});

				// 给表格写入数据
				$("#activityBody").html(html);

				// 计算总页数
				let totalPages = (data.total % pageSize) === 0? data.total/pageSize: parseInt(data.total/pageSize) + 1;

				// 数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数
					visiblePageLinks: 3, // 显示几个卡片
					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			},
			error() {
				window.location.href = "index.jsp";
			}
		})
	}

</script>
</head>
<body>

	<%-- 使用隐藏域来将查询到的信息保存 --%>
	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-id"/>

						<div class="form-group">
							<label for="edit-owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
								</select>
							</div>
                            <label for="edit-Name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-Name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate" >
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<!--
									文本域textarea
										1.标签对成对出现，标签对紧挨着
										2.属于表单元素范畴，对于textarea取值和赋值操作，统一使用val()方法
								-->
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls或.xlsx格式]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS/XLSX的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	<!-- 标题 -->
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>

	<!-- 界面 -->
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn" "><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="selectAll"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 80px; position: relative;top: 40px;" id="activityPage">

			</div>
			
		</div>
		
	</div>
</body>
</html>