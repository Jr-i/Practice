// 校验用户名的方法
function checkUsername() {
    // 定义正则
    const usernameReg = /^[a-zA-Z0-9]{5,10}$/;
    const username = document.getElementById("usernameInput").value;
    const usernameMsgSpan = document.getElementById("usernameMsg");
    if (!usernameReg.test(username)) {
        usernameMsgSpan.innerText = "请输入5到10位的字母和数字"
        return false
    }
    usernameMsgSpan.innerText = "OK"
    return true
}

// 校验密码的方法
function checkUserPwd() {
    // 定义正则
    const passwordReg = /^[0-9]{6}$/;
    const userPwd = document.getElementById("userPwdInput").value;
    const userPwdMsgSpan = document.getElementById("userPwdMsg");
    if (!passwordReg.test(userPwd)) {
        userPwdMsgSpan.innerText = "请输入6位数字"
        return false
    }
    userPwdMsgSpan.innerText = "OK"
    return true
}

// 校验密码的方法
function checkReUserPwd() {
    // 定义正则
    const passwordReg = /^[0-9]{6}$/;
    const userPwd = document.getElementById("userPwdInput").value;
    const reUserPwd = document.getElementById("reUserPwdInput").value;
    const reUserPwdMsgSpan = document.getElementById("reUserPwdMsg");
    if (userPwd !== reUserPwd) {
        reUserPwdMsgSpan.innerText = "密码不一致"
        return false

    }
    reUserPwdMsgSpan.innerText = "OK"
    return true
}

function checkLogin() {
    return checkUsername() && checkUserPwd()
}

function checkRegist() {
    return checkUsername() && checkUserPwd() && checkReUserPwd()
}