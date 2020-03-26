function login() {
    var username = $("input[name=username]").val();
    var password = $("input[name=password]").val();
    var rememberMe = $("input[name=remember-me]").is(":checked");
    if (username === "" || password === "") {
        alert("用户名或密码不能为空");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/authentication/form",
        data: {
            "username": username,
            "password": password,
            "remember-me":rememberMe
        },
        success: function (e) {
            alert("登陆成功");
            setTimeout(function () {
                location.href = '/login-success';
            }, 1500);
        },
        error: function (e) {
            alert("登陆失败")
        }
    });
}
