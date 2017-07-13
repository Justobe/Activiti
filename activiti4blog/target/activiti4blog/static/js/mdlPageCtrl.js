/**
 * Created by YanMing on 2017/6/12.
 */

$(function () {
    $('.ui.dropdown')
        .dropdown({
            on: 'hover'
        });

    getAllModel();
});

function getAllModel() {
    $.ajax({
        url:'/model/getAllModel',
        success:function (data,status) {
            var tblContent = "";
            $.each(data,function (i,jsonStr) {
                var modelCtime = moment(jsonStr.modelCtime).utc().zone(+6).format("YYYY-MM-DD / HH:mm:ss");
                var modelLMtime = moment(jsonStr.modelLMtime).utc().zone(+6).format("YYYY-MM-DD / HH:mm:ss");
                tblContent = tblContent + "<tr>";
                //alert(jsonStr);
                tblContent = tblContent + "<td>" + jsonStr.modelId +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.modelKey +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.modelName +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.modelVersion +"</td>";
                tblContent = tblContent + "<td>" + modelCtime +"</td>";
                tblContent = tblContent + "<td>" + modelLMtime +"</td>";
                tblContent = tblContent + "<td>" + jsonStr.modelMetaData +"</td>";
                /*tblContent = tblContent + "<td>" + "<button class='btn btn-success' onclick='editModel(this)'>编辑</button>" +
                    "<button class='btn btn-success onclick='deployModel(this)'>部署</button></br>"+
                    "<button class='btn btn-success' onclick='exportModel(this)'>导出</button>" +
                    "<button class='btn btn-success' onclick='deleteModel(this)'>删除</button>" +
                    "</td>";*/
                tblContent = tblContent + "<td>" + '<div class="btn-group"><button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">操&nbsp;&nbsp;作&nbsp;&nbsp;&nbsp;&nbsp;<span class="caret"></span></button> <ul class="dropdown-menu"> <li onclick="editModel(this)"><a href="javascript:void(0)">编&nbsp;&nbsp;&nbsp;&nbsp;辑</a></li> <li onclick="deployModel(this)"><a href="javascript:void(0)">部&nbsp;&nbsp;&nbsp;&nbsp;署</a></li> <li onclick="exportModel(this)"><a href="javascript:void(0)">导&nbsp;&nbsp;&nbsp;&nbsp;出</a></li> <li onclick="deleteModel(this)"><a href="javascript:void(0)">删&nbsp;&nbsp;&nbsp;&nbsp;除</a></li> </ul> </div>';
                tblContent = tblContent + "</tr>"
            });
            //console.log(tblContent);
            $("#m_tbid").html(tblContent);
        }
    });

}
function editModel(obj) {
    var td = $(obj).parent().parent().parent();
    var modelId = td.prev().prev().prev().prev().prev().prev().prev().html();
    window.location.href=("/modeler.html?modelId="+modelId);
}
function deployModel(obj) {
    var td = $(obj).parent().parent().parent();
    var modelId = td.prev().prev().prev().prev().prev().prev().prev().html();
    var dataInfo ={
        modelId:modelId
    };
    console.log(modelId);
    $.ajax({
        url:"/model/deployModel",
        data:dataInfo,
        method:'post',
        success:function (data,status) {
            if(data =="right"){
                swal("部署成功！","","success");
            }else{
                swal("部署失败~","","error");
            }
        }
    })
}

function exportModel(obj) {
    var td = $(obj).parent().parent().parent();
    var modelId = td.prev().prev().prev().prev().prev().prev().prev().html();
    var dataInfo ={
        modelId:modelId
    };
    //console.log(modelId);
    window.open("/model/exportModel?modelId=" + modelId);
}
function deleteModel(obj) {
    var td = $(obj).parent().parent().parent();
    var modelId = td.prev().prev().prev().prev().prev().prev().prev().html();
    var dataInfo ={
        modelId:modelId
    };
    $.ajax({
        url:"/model/deleteModel",
        data:dataInfo,
        method:'post',
        success:function (data,status) {
            if(data === "right"){
                swal("删除成功~","","success");
                getAllModel();
            }
            else{
                swal("删除失败!","","error");
            }

        }
    })
}
function addModel() {
    var name = $("#name").val();
    var key = $("#key").val();
    var description = $("#description").val();
    window.location.href = "/model/create?name=" + name + "&key=" + key + "&description=" + description;
}