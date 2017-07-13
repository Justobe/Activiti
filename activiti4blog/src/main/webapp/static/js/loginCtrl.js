/**
 * Created by YanMing on 2017/6/14.
 */
$(function () {

});

function clickLogin() {
    //alert("hello");
    //alert(ctx);
    var username = $("#username").val();
    var password = $("#password").val();

    var logInfo = {
        username:username,
        password:password
    };

    $.post("/login",logInfo,function (data,status) {
        if(data === "right"){
            window.location.href ="/loginOK";
        }
        else{
            swal("用户名密码错误！","","error");
        }
    })
}