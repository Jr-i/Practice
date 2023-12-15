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

function checkRegistUsername() {
    if (checkUsername() === false) {
        return false
    }

    const xmlHttpRequest = new XMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function () {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200) {
            // console.log(xmlHttpRequest.responseText)
            const response = JSON.parse(xmlHttpRequest.responseText);
            if (response.code === 505) {
                const usernameMsgSpan = document.getElementById("usernameMsg");
                usernameMsgSpan.innerText = "已占用"
                return false
            }
        }
    }

    // 设置请求方式和请求的资源路径
    const username = document.getElementById("usernameInput").value;
    xmlHttpRequest.open("GET", "/user/checkUsernameUsed?username=" + username);
    // 发送请求
    xmlHttpRequest.send();
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