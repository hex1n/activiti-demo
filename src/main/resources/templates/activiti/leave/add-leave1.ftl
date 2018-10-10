<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>新建请假</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css" media="all"/>
    <script type="text/javascript" src="${base}/static/layui/layui.js"></script>
    <script type="text/javascript" src="${base}/static/js/tools.js"></script>
</head>

<body>
<div>
    <form style="margin-left: 20px;">
        <div style="width:100%;height:400px;overflow: auto;">
            <div class="layui-form-item">
                <fieldset class="layui-elem-field layui-field-title" style="margin-top: 10px;">
                    <legend style="font-size:16px;">请假信息</legend>
                </fieldset>
            </div>
            <div class="layui-form-item">
                <label for="beginTime" class="layui-form-label">
                    <span style="color: red">*</span>开始时间
                </label>
                <div class="layui-input-inline">
                    <input type="text" id="beginTime" name="beginTime" lay-verify="beginTime" placeholder="yyyy-MM-dd"
                           autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label for="endTime" class="layui-form-label">
                        <span style="color: red">*</span>结束时间
                    </label>
                    <div class="layui-input-inline">
                        <input type="text" id="endTime" name="endTime" lay-verify="endTime" placeholder="yyyy-MM-dd"
                               autocomplete="off" class="layui-input">
                    </div>
                </div>
            </div>
            <div class="layui-form-item">
                <fieldset class="layui-elem-field layui-field-title" style="margin-top: 10px;">
                    <legend style="font-size:16px;">原因</legend>
                </fieldset>
            </div>
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label for="reason" class="layui-form-label">
                        <span style="color: red">*</span>请假原因
                    </label>
                    <div class="layui-input-inline">
                        <input type="text" id="reason" style="width: 300px;" name="reason" lay-verify="reason"
                               autocomplete="off" class="layui-input">
                    </div>
                </div>
            </div>
        </div>
        <div style="width: 100%;height: 55px;background-color: white;border-top:1px solid #e6e6e6;
  position: fixed;bottom: 1px;margin-left:-20px;">
            <div class="layui-form-item" style=" float: right;margin-right: 30px;margin-top: 8px">

                <button class="layui-btn layui-btn-normal" lay-filter="add" lay-submit>
                    申请
                </button>
                <button class="layui-btn layui-btn-primary" data-type="close">
                    取消
                </button>
            </div>
        </div>
    </form>
</div>
<script>
    layui.use(['form', 'layer', 'jquery', 'laydate'], function () {
        $ = layui.jquery;
        var form = layui.form
                , layer = layui.layer
                , laydate = layui.laydate;


        laydate.render({
            elem: '#beginTime'
        });
        laydate.render({
            elem: '#endTime'
        });
        var $ = layui.$, active = {
            close: function () {
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            }
        }
        $('.layui-form-item .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });


        //自定义验证规则
        form.verify({
            beginTime: function (value) {
                if (value.trim() == "") {
                    return "开始时间不能为空";
                }
            },
            endTime: function (value) {
                if (value.trim() == "") {
                    return "结束时间不能为空";
                }
            },
            reason: function (value) {
                if (value.trim() == "") {
                    return "请填写请假原因";
                }
            }
        });
        form.on('submit(close)', function (data) {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        })
        //监听提交
        form.on('submit(add)', function (data) {

            $.ajax({
                url: '/leave/addLeave',
                data: data.field,
                dataType: 'text',
                type: 'post',
                async: false,
                success: function (res) {
                    if (res == "成功") {
                        parent.location.href = "/leave/showLeaveList";
                    } else {
                        alert("失败")
                    }
                },
                error: function (res) {
                    layer.msg("申请失败")
                }
            })


            // return false;
        });
    });
</script>
</body>

</html>
