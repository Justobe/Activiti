/**
 * Created by YanMing on 2017/6/23.
 */
var hisTaskId = "";
$(function () {
    /*swal({
     title: "Welcome Back!",
     text: "欢迎回来",
     type: "success",
     confirmButtonText: "OK",
     timer: 2000
     });*/

    $('.ui.dropdown')
        .dropdown({
            on: 'hover'
        });
    getMyAssign();
    getHisProc();
});

function getMyAssign() {
    var username = $("#username").text();
    var dataInfo = {
        username: username
    };
    $.ajax({
        url: "/task/getTaskByUsername",
        data: dataInfo,
        success: function (data, status) {
            var tblContent = "";
            $.each(data, function (i, jsonStr) {
                var startTime = moment(jsonStr.startTime).utc().zone(+6).format("YYYY-MM-DD / HH:mm:ss");
                //var startTime = jsonStr.startTime;
                tblContent = tblContent + "<tr style='text-align: center'>";
                tblContent = tblContent + "<td>" + jsonStr.taskId + "</td>";
                tblContent = tblContent + "<td>" + jsonStr.taskName + "</td>";
                tblContent = tblContent + "<td>" + startTime + "</td>";
                tblContent = tblContent + "<td>" + jsonStr.startMan + "</td>";
                tblContent = tblContent +'<td>'+'<div class="btn-group">'+
                    '<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">操&nbsp;&nbsp;作&nbsp;&nbsp;&nbsp;&nbsp;<span class="caret"></span></button>'+
                    '<ul class="dropdown-menu">'+
                    '<li onclick="getProgress(this)" data-toggle="modal" data-target="#processImageModal"><a href="javascript:void(0)">查看进度</a></li>'+
                    '<li onclick="getDetail(this)"><a href="javascript:void(0)">办理细节</a></li>'+
                    '<li onclick="clickComplete(this)" data-toggle="modal" data-target="#completeModal"><a href="javascript:void(0)">办&nbsp;&nbsp;&nbsp;&nbsp;理</a></li>'+
                    '</ul>'+
                    '</div>'+'</td>';
            });
            $("#ma_tbid").html(tblContent);
        }

    })
}
function clickComplete(obj) {
    var td = $(obj).parent().parent().parent();
    var taskId = td.prev().prev().prev().prev().html();
    var dataInfo = {
        taskId: taskId
    };
    $.ajax({
        url: "/task/getCmnt",
        data: dataInfo,
        success: function (data, status) {
            var fieldContent = "<form id='cmntForm'>";
            fieldContent = fieldContent + "<input class='form-control' name='taskId' value=" + taskId + " style='display:none;'/>";
            for (var i = 0; i < data.length; i++) {
                var name = data[i].fieldName;
                var type = data[i].fieldType;
                var val = data[i].fieldValue;
                fieldContent = fieldContent + "<label>" + name + "</label>";
                if (type === "string" || type === "long") {
                    fieldContent = fieldContent + "<input class='form-control' type='text' id=" + name + " name=" + name + " value="+ val +" >" + "<br/><br/>";
                }
                else if (type === "date") {
                    fieldContent = fieldContent + "<input class='form-control' type='date' id=" + name + " name=" + name + " >" + "<br/><br/>";
                }
                else if(type === "boolean"){
                    fieldContent = fieldContent +
                        "<select class='form-control' style='width: 200px' name=" + name +" >"+
                        "<option value ='true'>同意申请</option>"+
                        "<option value ='false'>驳回申请</option>"+
                        "</select>" +"<br/><br/>";
                }

            }
            fieldContent = fieldContent + "</form>";
            $("#modal-body").html(fieldContent);
        }

    })
}

function getDetail(obj) {
    $("#actiDiv").show();
    var td = $(obj).parent().parent().parent();
    hisTaskId = td.prev().prev().prev().prev().html();
    $.get("/process/getDetail?taskId=" + hisTaskId, function (data, status) {
        var tblContent = "";
        $.each(data, function (i, jsonStr) {
            var startTime = "Not Start";
            var endTime = "Not End";
            if(jsonStr.startTime!= ""){
                startTime = moment(jsonStr.startTime).utc().zone(+6).format("YYYY-MM-DD / HH:mm:ss");
            }
            if(jsonStr.endTime!=""){
                endTime = moment(jsonStr.endTime).utc().zone(+6).format("YYYY-MM-DD / HH:mm:ss");
            }
            tblContent = tblContent + "<tr style='text-align: center'>";
            tblContent = tblContent + "<td>" + jsonStr.id + "</td>";
            tblContent = tblContent + "<td>" + jsonStr.activityId + "</td>";
            tblContent = tblContent + "<td>" + jsonStr.activityName + "</td>";
            tblContent = tblContent + "<td>" + jsonStr.activityType + "</td>";
            tblContent = tblContent + "<td>" + startTime + "</td>";
            tblContent = tblContent + "<td>" + endTime + "</td>";
            tblContent = tblContent + "<td>" + jsonStr.assignee + "</td>";
            if(endTime!="Not End")
            {
                tblContent = tblContent + "<td>" + "<button class='btn btn-primary' onclick='getHisDetail(hisTaskId,this)' data-toggle='modal' data-target='#hisDtlModal'>查看详细数据</button>" + "</td>";
            }else{
                tblContent = tblContent + "<td>" +" 无详细数据" + "</td>";
            }
            tblContent = tblContent + "</tr>";
        });
        $("#ac_tbid").html(tblContent);
    });
}

function getHisDetail(taskId,obj) {
    //alert("123456");
    var td = $(obj).parent();
    var activityInsId = td.prev().prev().prev().prev().prev().prev().prev().html();
    $.get("/process/getHisDetail?taskId=" + taskId + "&activityInsId="+activityInsId, function (data, status) {
        if(data.length!=0){
            $("#hisDtlDiv").html('<table class="table table-bordered table-striped">'+
                '<thead>'+
                '<tr style="text-align: center">'+
                '<th style="text-align: center">表单字段名</th>'+
                '<th style="text-align: center">表单属性值</th>'+
                '</tr>'+
                '</thead>'+
                '<tbody id="hisdtl_tbid">'+
                '</tbody>'+
                '</table>');
            var tblContent = "";
            $.each(data,function (i,jsonStr) {
                tblContent = tblContent + "<tr style='text-align: center'>";
                tblContent = tblContent +"<td>" + jsonStr.fieldName +"</td>";
                tblContent = tblContent +"<td>" + jsonStr.fieldValue +"</td>";
                tblContent = tblContent + "</tr>";
                //tblContent = tblContent +
            });
            $("#hisdtl_tbid").html(tblContent);
        }else {
            $("#hisDtlDiv").html("<h3 class='text-info'>当前结点无表单数据哦~</h3>");
            //$("#hisDtlDiv").hide();
        }
    })
}
function completeTask() {
    $.ajax({
        url: "/task/completeTask",
        data: $("#cmntForm").serialize(),
        success: function (data, status) {
            if (data === "right") {
                swal("干得漂亮！", "任务办理成功！","success");
                getMyAssign();
            }else{
                swal("出错了~", "任务办理失败！","error");
            }
        }

    })
}


function getHisProc() {
    var username = $('#username').text();
    var dataInfo = {
        username:username
    };
    $.ajax({
        url:"/process/getHisProc",
        data:dataInfo,
        success:function (data,status) {
            var tblContent = "";
            $.each(data,function (i,jsonStr) {
                var hisProcStartTime = moment(jsonStr.hisProcStartTime).format("YYYY-MM-DD / HH:mm:ss");
                tblContent += "<tr>";
                tblContent = tblContent + "<td>" +jsonStr.hisProcId +"</td>";
                tblContent = tblContent + "<td>" +jsonStr.hisProcName +"</td>";
                tblContent = tblContent + "<td>" +hisProcStartTime +"</td>";
                if(jsonStr.hisProcStatus === "1") {
                    tblContent = tblContent + "<td>" + "已完结" + "</td>";
                }else {
                    tblContent = tblContent + "<td>" + "正在处理" + "</td>";
                }
                tblContent = tblContent + "<td>" +"<button class='btn btn-primary' onclick='getHisProgress(this)' data-toggle='modal' data-target='#historyProcessImageModal'>查看进度</button>" +"</td>";
                tblContent += "</tr>";
            });
            $("#his_tbid").html(tblContent)
        }
    })
}

function getProgress(obj) {
    var td = $(obj).parent().parent().parent();
    var taskId = td.prev().prev().prev().prev().html();
    console.log(taskId);
    //window.open("/process/getProgressImg?taskId=" + taskId);
    var dataInfo = {
        taskId:taskId
    };
    $.ajax({
        url:"/process/getProgressImg",
        data:dataInfo,
        success:function (data,status) {
            if(data != "error"){
                var htmlContent = '<img src="' + data +  '"/>';
                $('#process-image-modal-body').html(htmlContent);
            }else {
                swal("获取任务流程进度失败","","error");
            }

        }
    })

}

function getHisProgress(obj) {
    var td = $(obj).parent();
    var processId = td.prev().prev().prev().prev().html();
    console.log(processId);
    //window.open("/process/getHisProgressImg?processId=" + processId);
    var dataInfo = {
        processId:processId
    };
    $.ajax({
        url:"/process/getHisProgressImg",
        data:dataInfo,
        success:function (data,status) {
            if(data != "error"){
                var htmlContent = '<img src="' + data +  '"/>';
                $('#history-process-image-modal-body').html(htmlContent);
            }else {
                swal("获取历史流程进度失败","","error");
            }

        }
    })


}
