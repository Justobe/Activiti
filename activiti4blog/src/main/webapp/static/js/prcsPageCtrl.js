/**
 * Created by YanMing on 2017/6/12.
 */

$(function () {
    $('.ui.dropdown')
        .dropdown({
            on: 'hover'
        });
    getAllProcess();
    getSuspendProcess();
});

function getAllProcess() {
    $.ajax({
        url:'/process/getAllProcess',
        success:function (data,status) {
            var tblContent = "";
            $.each(data,function (i,jsonStr) {
                tblContent = tblContent + "<tr>";
                tblContent = tblContent + "<td>" + jsonStr.processId +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.deployId +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.processName +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.processKey +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.processDes +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.processVer +"</td>";
                tblContent = tblContent + "<td>" + "<button class='btn btn-success' onclick='getProcessXML(this)'>查看</button>" + "</td>";
                tblContent = tblContent + "<td>" + "<button class='btn btn-success' onclick='getProcessImg(this)' data-toggle='modal' data-target='#processImageModal'>查看</a>" + "</td>";
                if(jsonStr.processStatus === "1"){
                    tblContent = tblContent + "<td>" + "已激活" +"</td>";
                }else{
                    tblContent = tblContent + "<td>" + "已挂起" +"</td>";
                }
                tblContent = tblContent + "<td>" + '<div class="btn-group"> <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">操&nbsp;&nbsp;作&nbsp;&nbsp;&nbsp;&nbsp;<span class="caret"></span></button> <ul class="dropdown-menu"> <li onclick="deleteProcess(this)"><a href="javascript:void(0)">删&nbsp;&nbsp;&nbsp;&nbsp;除</a></li> <li onclick="convertProcess(this)"><a href="javascript:void(0)">转换为模型</a></li>';
                if(jsonStr.processStatus === "1"){
                    tblContent = tblContent + '<li onclick="suspendProcess(this)"><a href="javascript:void(0)">挂&nbsp;&nbsp;&nbsp;&nbsp;起</a></li>';
                }
                else{
                    tblContent = tblContent + '<li onclick="suspendProcess(this)"><a href="javascript:void(0)">激&nbsp;&nbsp;&nbsp;&nbsp;活</a></li>';
                }

                tblContent = tblContent + "</ul></div></td></tr>";
            });
            $("#p_tbid").html(tblContent);
        }
    })
}

function getProcessXML(obj) {
    var td = $(obj).parent();
    var processId = td.prev().prev().prev().prev().prev().prev().html();
    window.open("/process/getProcessXML?processId=" +processId);
}
function getProcessImg(obj) {
    var td = $(obj).parent();
    var processId = td.prev().prev().prev().prev().prev().prev().prev().html();
    //window.open("/process/getProcessImg?processId=" +processId);
    var dataInfo = {
        processId:processId
    };
    $.ajax({
      url:"/process/getProcessImg",
        data:dataInfo,
        success:function (data,status) {
            if(data != "error"){
                var htmlContent = '<img src="' + data +  '"/>';
                $('#process-image-modal-body').html(htmlContent);
            }else {
                swal("fail","","error");
            }
        }
    })
}

function deleteProcess(obj) {
    var td = $(obj).parent().parent().parent();
    var deployId = td.prev().prev().prev().prev().prev().prev().prev().prev().html();
    var dataInfo={
        deployId:deployId
    };
    $.ajax({
        url:'/process/deleteProcess',
        data:dataInfo,
        success:function (data,status) {
            if(data === "right"){
                swal("删除流程成功！","","success");
                getAllProcess();
                getSuspendProcess();
            }else{
               swal("删除流程失败","","error");
            }
        }
    })
}
function suspendProcess(obj) {
    var td = $(obj).parent().parent().parent();
    var processId = td.prev().prev().prev().prev()
        .prev().prev().prev().prev().prev().html();
    var dataInfo = {
        processId:processId
    };
    $.ajax({
        url:"/process/changeStatus",
        data:dataInfo,
        success:function (data,status) {
            if(data != "-1"){
                if(data ==="1"){
                    $(obj).html('<a href="javascript:void(0)">挂&nbsp;&nbsp;&nbsp;&nbsp;起</a>');
                    td.prev().html("已激活");
                    getAllProcess();
                    getSuspendProcess();
                }else{
                    $(obj).html('<a href="javascript:void(0)">激&nbsp;&nbsp;&nbsp;&nbsp;活</a>');
                    td.prev().html("已挂起");
                    getAllProcess();
                    getSuspendProcess();
                }
            }else{
                swal("更新失败！","","error");
            }
        }
    })

}

function convertProcess(obj) {
    var td = $(obj).parent().parent().parent();
    var processId = td.prev().prev().prev().prev().prev().prev().prev().prev().prev().html();
    var dataInfo = {
        processId:processId
    };
    $.ajax({
        url:"/process/convertToModel",
        data:dataInfo,
        success:function (data,status) {
            if(data === "right"){
                swal("转换模型成功！","","success");
            }else{
                swal("转换模型失败！","","error");
            }
        }
    })
}

function getSuspendProcess() {
    $.ajax({
        url: "/process/getUnsuspendProcess",
        success: function (data, status) {
            var sContent = "";
            $.each(data, function (i, jsonStr) {
                sContent = sContent + "<option value=" + jsonStr.processId + ">" + jsonStr.processKey + "版本号:" + jsonStr.processVer + "</option>"
            });
            $("#s_process").html(sContent);

        }
    })
}

function clickStart() {
    var option = $('#s_process option:selected');
    var processDefId = option.val();
    var username = $("#username").text();
             $.ajax({
                    url: "/process/startProcess?processId="+processDefId +"&startMan="+username,
                    success: function (data, status) {
                        if (data === "right") {
                            swal("提交申请成功！","","success");
                        }
                        else
                        {
                            swal("提交申请失败","","error");
                        }
                    }

                });
}

