<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>我参与的流程</title>
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
<fieldset class="layui-elem-field">
    <legend>模型检索</legend>
    <div class="layui-field-box">
        <form class="layui-form">
            模型名称 :
            <div class="layui-inline" style="width: 15%">
                <input type="text" value="" name="name" class="layui-input search_input">
            </div>
            key:
            <div class="layui-inline" style="width: 15%">
                <input type="text" value="" name="key" class="layui-input search_input">
            </div>
            <div class="layui-inline">
                <a class="layui-btn" lay-submit="" lay-filter="searchForm">查询</a>
            </div>

            <div class="layui-inline">
                <a class="layui-btn layui-btn-danger" data-type="deleteSome">批量删除</a>
            </div>
            <div class="layui-inline">
                <a class="layui-btn layui-btn-danger" data-type="addModel">新建模型</a>
            </div>
        </form>
    </div>
</fieldset>
<div class="layui-form users_list">
    <table class="layui-table" id="test" lay-filter="demo"></table>

    <script type="text/html" id="barDemo">
        <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="deploy">发布</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>

    </script>
</div>
<div id="page"></div>
<script type="text/javascript" src="${base}/static/layui/layui.all.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js"></script>
<script type="text/javascript" src="${base}/static/js/jquery.min.js"></script>
<script>

    layui.use(['layer', 'form', 'table'], function () {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                table = layui.table,
                t;                  //表格数据变量

        table.render({
            elem: '#test',
            url: '/act/process-modelList',
            method: 'post',
            page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
                layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
                //,curr: 5 //设定初始在第 5 页
                groups: 3, //显示 2 个连续页码
                first: "首页", //显示首页
                last: "尾页", //显示尾页
                limits: [5, 10, 20, 30]
            },
            width: $(parent.window).width() - 223,
            cols: [[
                {type: 'checkbox'},
                {field: 'id', title: '流程ID', width: '10%'},
                {field: 'name', title: '流程名称', width: '15%'},
                {field: 'key', title: '流程KEY', width: '15%'},
                {field: 'version', title: '版本号', width: '10%'},
                {field: 'createTime', title: '创建时间', width: '15%'},
                {field: 'lastUpdateTime', title: '修改时间', width: '15%'},
                {fixed: 'right', width: '15%', title: '操作', align: 'center', toolbar: '#barDemo'}
            ]]
        });
        // table.render(t);

        //监听工具条
        table.on('tool(demo)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {
                window.open("/static/modeler.html?modelId=" + data.id)
            }
            else if (obj.event === 'deploy') {
                layer.confirm("你确定要发布该流程么？", {btn: ['是的,我确定', '我再想想']},
                        function () {
                            $.post("/act/deployment", {"id": data.id}, function (res) {
                                if (res.success) {
                                    layer.msg("流程发布成功", {time: 1000}, function () {
                                        table.reload('test', table);
                                    });
                                } else {
                                    layer.msg(res.message);
                                }

                            });
                        }
                )
            } else if (obj.event === "del") {
                layer.confirm("你确定要删除该模型么？", {btn: ['是的,我确定', '我再想想']},
                        function () {
                            $.post("/act/delModel", {"id": data.id}, function (res) {
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
            }
        });

        //功能按钮
        var active = {
            addModel: function () {

                window.open("/act/design-process")
            },
            deleteSome: function () {                        //批量删除
                var checkStatus = table.checkStatus('test'),
                        data = checkStatus.data;
                if (data.length > 0) {
                    console.log(JSON.stringify(data));
                    layer.confirm("你确定要删除这些流程模型吗？", {btn: ['是的,我确定', '我再想想']},
                            function () {
                                var deleteindex = layer.msg('删除中，请稍候', {icon: 16, time: false, shade: 0.8});
                                $.ajax({
                                    type: "POST",
                                    url: "/act/delModels",
                                    dataType: "json",
                                    contentType: "application/json",
                                    data: JSON.stringify(data),
                                    success: function (res) {
                                        layer.close(deleteindex);
                                        if (res.success) {
                                            layer.msg("删除成功", {time: 1000}, function () {
                                                table.reload('test', table);
                                            });
                                        } else {
                                            layer.msg(res.message);
                                        }
                                    }
                                });
                            })
                } else {
                    layer.msg("请选择需要删除的模型", {time: 1000});
                }
            }
        };

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


</script>
</body>
</html>