<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>首页--layui后台管理模板</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all" />
    <link rel="stylesheet" href="//at.alicdn.com/t/font_tnyc012u2rlwstt9.css" media="all" />
    <link rel="stylesheet" href="${base}/static/css/main.css" media="all" />
</head>
<body class="childrenBody">
<div class="panel_box row">
    <#if (userMenu?size>0)>
        <#list userMenu as items>
            <div class="panel col <#if (!items_has_next)>max_panel</#if> ">
                <a href="javascript:" data-url="${base}${items.href}">
                    <div class="panel_icon" <#if (items.bgColor != "")>style="background-color: ${items.bgColor}"<#else>style="background-color: #54ade8"</#if>>
                        <i class="layui-icon" data-icon="${items.icon}">${items.icon}</i>
                    </div>
                    <div class="panel_word newMessage">
                        <span>${items.dataCount}</span>
                        <cite>${items.name}</cite>
                    </div>
                </a>
            </div>
        </#list>
    </#if>
</div>
<div class="row">
    <div id="container" style="height: 500px"></div>
    <script type="text/javascript" src="${base}/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="${base}/static/js/echarts.min.js"></script>
    <script type="text/javascript">
        var dom = document.getElementById("container");
        var myChart = echarts.init(dom);
        var dateArray = [];
        function getDay(day){
            var today = new Date();

            var targetday_milliseconds=today.getTime() + 1000*60*60*24*day;

            today.setTime(targetday_milliseconds); //注意，这行是关键代码

            var tYear = today.getFullYear();
            var tMonth = today.getMonth();
            var tDate = today.getDate();
            tMonth = doHandleMonth(tMonth + 1);
            tDate = doHandleMonth(tDate);
            return tYear+"-"+tMonth+"-"+tDate;
        }
        function doHandleMonth(month){
            var m = month;
            if(month.toString().length === 1){
                m = "0" + month;
            }
            return m;
        }
        for(i=-14; i<=0;i++){
            console.log(getDay(i));
            dateArray.push(getDay(i));
        }

    </script>
</div>

<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script>
    layui.use(['layer','jquery','form'],function(){
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form;

        $(".panel a").on("click",function(){
            window.parent.addTab($(this));
        });
    });
</script>
</body>
</html>