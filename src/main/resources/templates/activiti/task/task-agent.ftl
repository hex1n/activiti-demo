<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>办理任务</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width,user-scalable=yes, minimum-scale=0.4, initial-scale=0.8,target-densitydpi=low-dpi"/>
    <link rel="stylesheet" href="${base}/static/layui/css/layui.css">
    <link rel="stylesheet" href="${base}/static/lenosp/ztree/css/metroStyle/metroStyle.css">
    <script type="text/javascript" src="${base}/static/lenosp/jquery.min.js"></script>
    <script type="text/javascript" src="${base}/static/layui/layui.all.js" charset="utf-8"></script>
    <script type="text/javascript" src="${base}/static/lenosp/tools/tool.js" charset="utf-8"></script>
</head>

<body>
<div class="x-body">
    <form class="layui-form layui-form-pane" style="margin-left: 20px;">
        <div style="width:98%;height:450px;overflow: auto;">
            <div class="layui-form-item">
                <fieldset class="layui-elem-field layui-field-title" style="margin-top: 10px;">
                    <legend style="font-size:16px;">请假信息</legend>
                </fieldset>
            </div>
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label for="beginTime" class="layui-form-label">
                        开始时间
                    </label>
                    <div class="layui-input-inline">
                        <input type="hidden" name="processInstanceId" value="${processInstanceId}">
                        <input type="hidden" name="taskId" value="${taskId}">
                        <input type="text" id="beginTime" readonly value="${leave.beginTime}" class="layui-input">
                    </div>
                </div>
                <div class="layui-inline">
                    <label for="endTime" class="layui-form-label">
                        结束时间
                    </label>
                    <div class="layui-input-inline">
                        <input type="text" id="endTime" readonly value="${leave.endTime}" class="layui-input">
                    </div>
                </div>
            </div>
            <div class="layui-form-item layui-form-text">
                <label class="layui-form-label">原因</label>
                <div class="layui-input-block">
                    <textarea class="layui-textarea" readonly>${leave.reason}</textarea>
                </div>
            </div>
            <div class="layui-form-item layui-form-text">
                <label class="layui-form-label">审批意见</label>
                <div class="layui-input-block">
                    <textarea name="opinion" id="opinion" lay-verify="opinion" class="layui-textarea"></textarea>
                </div>
            </div>
        </div>
        <div style="width: 100%;height: 55px;background-color: white;border-top:1px solid #e6e6e6;
  position: fixed;bottom: 1px;margin-left:-20px;">
            <div class="layui-form-item" style=" float: right;margin-right: 10px;margin-top: 8px">
                <input name="flag" type="hidden">
                <button class="layui-btn layui-btn-normal" lay-filter="ok" lay-submit>
                    通过
                </button>
                <button class="layui-btn layui-btn-normal" lay-filter="no" lay-submit>
                    不通过
                </button>
                <button class="layui-btn layui-btn-primary" data-type="close">
                    取消
                </button>
            </div>
        </div>
    </form>
</div>
<script>
    layui.use(['form', 'layer'], function () {
        $ = layui.jquery;
        var form = layui.form
                , layer = layui.layer;


        //自定义验证规则
        form.verify({
            opinion: function (value) {
                if (value.trim() == "") {
                    return "审核信息不能为空";
                }
            }
        });
        var $ = layui.$, active = {
            close: function () {
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            }
        }
        eleClick(active, '.layui-form-item .layui-btn');

        form.on('submit(no)', function (data) {
            data.field.flag = false;
            layerAjax('/leave/complete', data.field, 'taskList');
           /* $.ajax({
                url: '/complete',
                data: data.field,
                type: 'post',
                success: function (re) {
                    layer.alert("审核成功" + (data.field.flag ? "<font style='color:green'>[通过]</font>" : "<font style='color:red'>[未通过]</font>"))
                },
                error: function (re) {

                    layer.alert("办理失败");
                }
            })*/
            return false;
        });

        form.on('submit(ok)', function (data) {
            data.field.flag = true;
            layerAjax('/leave/complete',data.field,'taskList');

           /* $.ajax({
                url: '/leave/complete',
                data: data.field,
                success: function (re) {
                    layer.alert("审核成功" + (data.field.flag ? "<font style='color:green'>[通过]</font>" : "<font style='color:red'>[未通过]</font>"))
                },
                error: function (re) {
                    console.log(re)
                    layer.msg("办理失败");
                }
            })*/
            return false;
        });
        form.render();
    });
</script>
</body>

</html>
