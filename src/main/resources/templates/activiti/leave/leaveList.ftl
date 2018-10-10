<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>我发起的请假流程</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="shortcut icon">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all"/>
    <link rel="stylesheet" href="${base}/static/css/user.css" media="all"/>
</head>
<body class="childrenBody">
<div>
    <div class="layui-field-box">
        <form class="layui-form">
            开始时间：
            <div class="layui-inline">
                <input class="layui-input" placeholder="yyyy-MM-dd" height="20px" id="beginTime" autocomplete="off">
            </div>
            结束时间：
            <div class="layui-inline">
                <input class="layui-input" placeholder="yyyy-MM-dd" height="20px" id="endTime" autocomplete="off">
            </div>
            <div class="layui-inline">
                <a class="layui-btn" lay-submit="" lay-filter="searchForm">查询</a>
            </div>
            <div>
                <div class="layui-inline">
                    <a class="layui-btn layui-btn-normal" data-type="addUserLeave">新建请假</a>
                </div>
            </div>
        </form>
    </div>

</div>
<blockquote class="layui-elem-quote">
    <span style="margin: auto;font-size: 15px;width: 60px">我发起的请假流程:</span>
</blockquote>
<div class="layui-form users_list" style="position: relative ;top: -20px">
    <table class="layui-table" id="leaveList" lay-filter="demo"></table>

    <script type="text/html" id="barDemo">
        <#--<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>-->
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="getProcImage"><i class="layui-icon">&#xe640;</i>查看流程图</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="leaveDetail"><i class="layui-icon">&#xe640;</i>查看详情</a>

    </script>
</div>
<div id="page"></div>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js"></script>
<script>
    /*日期选择*/
    layui.use('laydate', function () {
        var laydate = layui.laydate;

        laydate.render({
            elem: '#beginTime'
        })
        laydate.render({
            elem: '#endTime'
        })
    })

    layui.use(['layer', 'form', 'table'], function () {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                table = layui.table,
                t;                  //表格数据变量

        table.render({
            elem: '#leaveList',
            url: '${base}/leave/showLeaveList',
            method: 'post',
            page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
                //,curr: 5 //设定初始在第 5 页
                groups: 3, //显示 2 个连续页码
                first: "首页", //显示首页
                last: "尾页", //显示尾页
                limits: [5, 10, 20, 30]
            },
            // width: $(parent.window).width()-223,
            cols: [[
                {type: 'checkbox'},
                {field: 'userId', title: '申请人', width: '10%'},
                {field: 'beginTime', title: '开始时间', width: '10%'},
                {field: 'endTime', title: '结束时间', width: '15%'},
                // {field: 'taskName', title: '状态', width: '15%'},
                {field: 'reason', title: '原因', width: '12%'},
                {field: 'days', title: '天数', width: '12%'},
                {field: 'processInstanceId', title: '流程实例id', width: '12%'},
                {fixed: 'right', width: '30%', title: '操作', align: 'center', toolbar: '#barDemo'}
            ]]
        });
        // table.render(t);

        //监听工具条
        table.on('tool(demo)', function (obj) {
            var data = obj.data;
            /*   if (obj.event === 'edit') {
                   var editIndex = layer.open({
                       title: "编辑用户",
                       type: 2,
                       content: "/admin/system/user/edit?id=" + data.id,
                       success: function (layero, index) {
                           setTimeout(function () {
                               layer.tips('点击此处返回会员列表', '.layui-layer-setwin .layui-layer-close', {
                                   tips: 3
                               });
                           }, 500);
                       }
                   });
                   //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                   $(window).resize(function () {
                       layer.full(editIndex);
                   });
                   layer.full(editIndex);
               }*/
            if (obj.event === "del") {
                layer.confirm("你确定要删除该流程么？", {btn: ['是的,我确定', '我再想想']},
                        function () {
                            $.post("/act/delDeploy", {"id": data.deploymentId}, function (res) {
                                if (res.success) {
                                    layer.msg("删除成功", {time: 1000}, function () {
                                        table.reload('test', table);
                                    });
                                } else {
                                    layer.msg(res.message);
                                }

                            });
                        }
                )
            } else if (obj.event === "getProcImage") {


                layer.open({
                    id: 'resource-image',
                    type: 2,
                    area: ['880px', '400px'],
                    fix: false,
                    maxmin: true,
                    shadeClose: false,
                    shade: 0.4,
                    title: '流程图',
                    content: '/leave/getShineProcImage/?id=' + data.processInstanceId
                });
            } else if (obj.event == 'leaveDetail') {
                layer.open({
                    id: 'leave-detail',
                    type: 2,
                    area: ['880px', '400px'],
                    fix: false,
                    maxmin: true,
                    shadeClose: false,
                    shade: 0.4,
                    title: '审核详情',
                    content: "leaveDetail?processId=" + data.processInstanceId
                });
            }
        });

        //功能按钮
        var active = {
            addUserLeave: function () {

                add("申请请假", 'addLeave', 700, 450);
            }
        }

        $('.layui-inline .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        //搜索
        form.on("submit(searchForm)", function (data) {

            table.where = data.field;
            table.reload('test', table);
        })
        return false;
    });

    function add(title, url, w, h) {
        if (title == null || title == '') {
            title = false;
        }
        ;
        if (url == null || url == '') {
            url = "404.html";
        }
        ;
        if (w == null || w == '') {
            w = ($(window).width() * 0.9);
        }
        ;
        if (h == null || h == '') {
            h = ($(window).height() - 50);
        }
        ;
        layer.open({
            id: 'leave-add',
            type: 2,
            area: [w + 'px', h + 'px'],
            fix: false,
            maxmin: true,
            shadeClose: false,
            shade: 0.4,
            title: title,
            content: url
        });
    }

</script>
</body>
</html>