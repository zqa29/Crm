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
    <title>transaction</title>
    <script src="ECharts/echarts.min.js"></script>
    <script src="jquery/jquery-3.5.1.js"></script>
    <script>
        function getCharts() {
            // ajax
            $.ajax({
                url:"workbench/chart/transaction/getCharts.do",
                type:"get",
                dataType:"json",
                success(data){
                    // 基于准备好的dom，初始化echarts实例
                    let myChart = echarts.init(document.getElementById('main'));

                    // 指定图表的配置项和数据
                    let option = {
                        title: {
                            text: '漏斗图',
                            subtext: '统计交易阶段的漏斗图'
                        },
                        tooltip: {
                            trigger: 'item',
                            formatter: "{a} <br/>{b} : {c}%"
                        },
                        toolbox: {
                            feature: {
                                dataView: {readOnly: false},
                                restore: {},
                                saveAsImage: {}
                            }
                        },
                        legend: {
                            data: ['展现','点击','访问','咨询','订单']
                        },

                        series: [
                            {
                                name:'交易漏斗图',
                                type:'funnel',
                                left: '10%',
                                top: 60,
                                //x2: 80,
                                bottom: 60,
                                width: '80%',
                                // height: {totalHeight} - y - y2,
                                min: 0,
                                max: data.total,
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: data.dataList
                                    /*
                                        [
                                            {value: 60, name: '访问'},
                                            {value: 40, name: '咨询'},
                                            {value: 20, name: '订单'},
                                            {value: 80, name: '点击'},
                                            {value: 100, name: '展现'}
                                        ]
                                    */
                            }
                        ]
                    };

                    // 使用刚指定的配置项和数据显示图表。
                    myChart.setOption(option);
                }
            })



        }

        $(function () {
            // 页面加载完毕后绘制统计图表
            getCharts();
        })



    </script>
</head>
<body>
    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="main" style="width: 600px;height:400px;"></div>
</body>
</html>
