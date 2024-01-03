package controller;

import common.Result;
import pojo.SysUser;
import service.SysUserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static common.ResultCodeEnum.*;
import static util.MD5Util.encrypt;
import static util.WebUtil.readJson;
import static util.WebUtil.writeJson;

@WebServlet("/user/*")
public class UserController extends BaseController {
    private final static SysUserServiceImpl sysUserService = new SysUserServiceImpl();

    protected void regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 接收要注册的用户信息
        SysUser sysUser = readJson(req, SysUser.class);
        // 调用服务层方法,将用户注册进入数据库
        Result<Object> result = Result.build(SUCCESS);
        if (sysUserService.insert(sysUser) == 0) {
            result = Result.build(USERNAME_USED);
        }
        writeJson(resp, result);
    }

    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取要登录的用户名密码
        SysUser inputUser = readJson(req, SysUser.class);
        // 调用服务层方法,根据用户名查询数据库中是否有一个用户
        List<SysUser> users = sysUserService.findByUsername(inputUser.getUsername());
        Result<Object> result;
        if (users.size() == 0) {
            // 没有根据用户名找到用户,说明用户名有误
            result = Result.build(USERNAME_ERROR);
        } else if (users.get(0).getUserPwd().equals(encrypt(inputUser.getUserPwd()))) {
            // 登录成功
            result = Result.build(SUCCESS);
        } else {
            // 用户密码有误
            result = Result.build(PASSWORD_ERROR);
        }

        writeJson(resp, result);
    }

    protected void checkUsernameUsed(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<SysUser> users =
                sysUserService.findByUsername(req.getParameter("username"));
        // 放入String之外的数据类型，使用xmlHttpRequest.responseText解析时会出现乱码
        Result<Object> result = Result.build(SUCCESS);
        if (users.size() != 0) {
            result = Result.build(USERNAME_USED);
        }

        writeJson(resp, result);
    }
}