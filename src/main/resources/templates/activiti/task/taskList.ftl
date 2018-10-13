<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>我的任务</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css">
    <link rel="stylesheet" href="${base}/static/lenosp/ztree/css/metroStyle/metroStyle.css">
    <script type="text/javascript" src="${base}/static/lenosp/jquery.min.js"></script>
    <script type="text/javascript" src="${base}/static/layui/layui.all.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/static/lenosp/tools/tool.js" charset="utf-8"></script>
</head>

<body>
<div class="lenos-search">
    <div class="select">
        操作用户：
        <div class="layui-inline">
            <input class="layui-input" height="20px" id="userName" autocomplete="off">
        </div>
        操作类型：
        <div class="layui-inline">
            <input class="layui-input" height="20px" id="type" autocomplete="off">
        </div>
        <button class="select-on layui-btn layui-btn-sm" data-type="select"><i class="layui-icon"></i>
        </button>
    </div>
</div>

<table id="taskList" class="layui-hide" lay-filter="task"></table>
<script type="text/html" id="toolBar">
    {{# if(d.flag){ }}
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="update"><i class="layui-icon">&#xe640;</i>编辑</a>
    {{# }else{ }}
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="handle"><i class="layui-icon">&#xe640;</i>办理</a>
    {{# } }}
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="leaveDetail"><i
            class="layui-icon">&#xe640;</i>查看详情</a>
</script>
<script>
    layui.laytpl.toDateString = function (d, format) {
        var date = new Date(d || new Date())
                , ymd = [
            this.digit(date.getFullYear(), 4)
            , this.digit(date.getMonth() + 1)
            , this.digit(date.getDate())
        ]
                , hms = [
            this.digit(date.getHours())
            , this.digit(date.getMinutes())
            , this.digit(date.getSeconds())
        ];

        format = format || 'yyyy-MM-dd HH:mm:ss';

        return format.replace(/yyyy/g, ymd[0])
                .replace(/MM/g, ymd[1])
                .replace(/dd/g, ymd[2])
                .replace(/HH/g, hms[0])
                .replace(/mm/g, hms[1])
                .replace(/ss/g, hms[2]);
    };

    //数字前置补零
    layui.laytpl.digit = function (num, length, end) {
        var str = '';
        num = String(num);
        length = length || 2;
        for (var i = num.length; i < length; i++) {
            str += '0';
        }
        return num < Math.pow(10, length) ? str + (num | 0) : num;
    };

    document.onkeydown = function (e) { // 回车提交表单
        var theEvent = window.event || e;
        var code = theEvent.keyCode || theEvent.which;
        if (code == 13) {
            $(".select .select-on").click();
        }
    }

    layui.use('table', function () {
        var table = layui.table;
        //方法级渲染
        table.render({
            id: 'taskList',
            elem: '#taskList'
            , url: '/leave/showTodoTasks'
            , cols: [[
                {checkbox: true, fixed: true, width: '5%'}
                , {field: 'id', title: '任务编码', width: '10%', sort: true}
                , {field: 'userId', title: '申请人', width: '10%', sort: true}
                , {field: 'reason', title: '原因', width: '15%', sort: true}
                , {field: 'beginTime', title: '开始时间', width: '10%', sort: true}
                , {field: 'endTime', title: '结束时间', width: '10%', sort: true}
                , {
                    field: 'createTime',
                    title: '创建时间',
                    width: '10%',
                    sort: true,
                    templet: '<div>{{ layui.laytpl.toDateString(d.createTime,"yyyy-MM-dd") }}</div>'
                }
                , {field: 'processInstanceId', title: '流程实例ID', width: '15%', sort: true}
                , {fixed: 'right', title: '操作', width: '20%', toolbar: '#toolBar'}

            ]]
            , page: true
            , height: 'full-46'
        });

        var $ = layui.$, active = {
            select: function () {
                var userName = $('#userId').val();
                var type = $('#type').val();
                table.reload('taskList', {
                    where: {
                        userId: userId
                    }
                });
            }
            , del: function () {
                var checkStatus = table.checkStatus('taskList')
                        , data = checkStatus.data;
                if (data.length == 0) {
                    layer.msg('请选择要删除的数据', {icon: 5});
                    return false;
                }
                var ids = [];
                for (item in data) {
                    ids.push(data[item].id);
                }
                del(ids);
            }
            , reload: function () {
                $('#userName').val('');
                $('#type').val('');
                table.reload('taskList', {
                    where: {
                        userName: null,
                        type: null
                    }
                });
            },
        };
        //监听工具条
        table.on('tool(task)', function (obj) {
            var data = obj.data;
            if (obj.event === 'handle') {
                // popup('办理', '/leave/agent', 700, 500, 'task-agent');
                layer.open({
                    id: 'leave-task',
                    type: 2,
                    area: ['100%', '100%'],
                    fix: false,
                    maxmin: true,
                    shadeClose: false,
                    shade: 0.4,
                    titile: '任务办理',
                    content: '/leave/agent?processInstanceId='+data.processInstanceId
                })
            } else if (obj.event === 'update') {
                popup('编辑', '${base}/leave/updateLeave/' + data.id, 700, 500, 'task-update');
            } else if (obj.event === 'leaveDetail') {
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

        $('.select .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

    });
    /**批量删除id*/
    /*function del(ids) {
      $.ajax({
        url: "del",
        type: "post",
        data: {ids: ids},
        dataType: "json", traditional: true,
        success: function (data) {
          layer.msg(data.msg, {icon: 6});
          layui.table.reload('actList');
        }
      });
    }*/
    /*function start(id) {
      $.ajax({
        url: "start",
        type: "post",
        data: {key: id},
        dataType: "json", traditional: true,
        success: function (data) {
          layer.msg(data.msg, {icon: 6});
          layui.table.reload('actList');
        }
      });
    }*/

</script>
</body>

</html>
