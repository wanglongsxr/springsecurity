function login() {
    var username = $("input[name=username]").val();
    var password = $("input[name=password]").val();
    var captcha = $("input[name=captcha]").val();
    var rememberMe = $("input[name=rememberMe]").is(":checked");
    if (username === "" || password === "") {
        alert("用户名或密码不能为空");
        return;
    }
    if (captcha === "" || captcha === null) {
        alert("验证码不能为空");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/authentication/form",
        data: JSON.stringify({
            "username": username,
            "password": password,
            "rememberMe":rememberMe,
            "captcha":captcha
        }),
        success: function (e) {
            alert("登陆成功");
            setTimeout(function () {
                location.href = '/login-success';
            }, 1500);
        },
        error: function (e) {
            console.log(e);
            alert(e.responseText);
            // alert("登陆失败")
        }
    });
}
