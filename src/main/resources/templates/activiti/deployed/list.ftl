<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>已部署流程列表</title>
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
    <legend>流程检索</legend>
    <div class="layui-field-box">
        <form class="layui-form">
            部署id :
            <div class="layui-inline" style="width: 15%">
                <input type="text" value="" name="deploymentId" class="layui-input search_input">
            </div>
            流程名称:
            <div class="layui-inline" style="width: 15%">
                <input type="text" value="" name="name" class="layui-input search_input">
            </div>
            <div class="layui-inline">
                <a class="layui-btn" lay-submit="" lay-filter="searchForm">查询</a>
            </div>

            <div class="layui-inline">
                <a class="layui-btn layui-btn-danger" data-type="deleteSome">批量删除</a>
            </div>
        </form>
    </div>
</fieldset>
<div>
    <button type="button" class="layui-btn" id="uploadTest"><i class="layui-icon"></i>上传流程文件</button>
    <font style="color: grey">文件格式:zip/rar/bar/bpmn/bpmn20.xml</font>
</div>
<div class="layui-form users_list">
    <table class="layui-table" id="test" lay-filter="demo"></table>

    <script type="text/html" id="barDemo">
        <#--<a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>-->
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="seePicture">查看流程图</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="seeXML">查看XML资源</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>

    </script>
</div>
<div id="page"></div>
<script type="text/javascript" src="${base}/static/layui/layui.js"></script>
<script type="text/javascript" src="${base}/static/js/tools.js"></script>
<script>
    layui.use(['layer', 'form', 'table'], function () {
        var layer = layui.layer,
                $ = layui.jquery,
                form = layui.form,
                table = layui.table,
                t;                  //表格数据变量

        table.render({
            elem: '#test',
            url: '${base}/act/deployed-process',
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
                {field: 'id', title: '流程定义ID', width: '10%'},
                {field: 'deploymentId', title: '部署ID', width: '10%'},
                {field: 'name', title: '流程定义名称', width: '15%'},
                {field: 'key', title: '流程定义KEY', width: '15%'},
                {field: 'version', title: '版本号', width: '12%'},
                {fixed: 'right', width: '30%', title: '操作', align: 'center', toolbar: '#barDemo'}
            ]]
        });
        // table.render(t);

        //监听工具条
        table.on('tool(demo)', function (obj) {
            var data = obj.data;
               if (obj.event === 'edit') {
                   window.open("/static/modeler.html?modelId=" + data.id)
                   //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                   $(window).resize(function () {
                       layer.full(editIndex);
                   });
                   layer.full(editIndex);
               }
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
            }
            if (obj.event === "seePicture" || obj.event === 'seeXML') {

                if (obj.event === "seePicture") {
                    layer.open({
                        id: 'resource-image',
                        type: 2,
                        area: ['880px', '400px'],
                        fix: false,
                        maxmin: true,
                        shadeClose: false,
                        shade: 0.4,
                        title: '流程图',
                        content: '/act/read-resource/?id=' + data.id + "&resourceName=" + data.diagramResourceName
                    });
                }
                else {

                    window.open("/act/read-resource?id=" + data.id + "&resourceName=" + data.resourceName);
                }
            }
        })

        //功能按钮
        var active = {
            addUser: function () {
                var addIndex = layer.open({
                    title: "添加用户",
                    type: 2,
                    content: "${base}/admin/system/user/add",
                    success: function (layero, addIndex) {
                        setTimeout(function () {
                            layer.tips('点击此处返回用户列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        }, 500);
                    }
                });
                //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
                $(window).resize(function () {
                    layer.full(addIndex);
                });
                layer.full(addIndex);
            },
            deleteSome: function () {                        //批量删除
                var checkStatus = table.checkStatus('test'),
                        data = checkStatus.data;
                if (data.length > 0) {
                    console.log(JSON.stringify(data));
                    layer.confirm("你确定要删除这些流程吗？", {btn: ['是的,我确定', '我再想想']},
                            function () {
                                var deleteindex = layer.msg('删除中，请稍候', {icon: 16, time: false, shade: 0.8});
                                $.ajax({
                                    type: "POST",
                                    url: "/act/delDeployeds",
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
                    layer.msg("请选择需要删除的流程", {time: 1000});
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

    /*文件上传*/
    layui.use('upload', function () {
        var $ = layui.jquery
                , upload = layui.upload,
                layer = layui.layer;

        upload.render({
            elem: '#uploadTest',
            url: '/act/upload-process?flag='+true,
            accept: 'file',
            exts: 'zip|rar|bar|bpmn|bpmn20.xml',
            done: function (res) {
                console.log(res)
                layer.msg("流程上传成功", {icon: 6}, function () {
                    location.href = "/act/deployed-process";
                })
            }
        })
    })

</script>
</body>
</html>