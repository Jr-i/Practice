package controller;

import pojo.SysUser;
import service.SysUserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static util.MD5Util.encrypt;

@WebServlet("/user/*")
public class UserController extends BaseController {
    private final static SysUserServiceImpl sysUserService = new SysUserServiceImpl();

    protected void regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1 将参数放入一个SysUser对象中,在调用regist方法时传入
        SysUser sysUser = new SysUser();
        sysUser.setUsername(req.getParameter("username"));
        sysUser.setUserPwd(req.getParameter("userPwd"));
        // 2 调用服务层方法,完成注册功能。根据注册结果(成功  失败) 做页面跳转
        if (sysUserService.insert(sysUser) == 0) {
            resp.sendRedirect("/registFail.html");
        } else {
            resp.sendRedirect("/registSuccess.html");
        }

    }

    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<SysUser> users = sysUserService.findByUsername(req.getParameter("username"));
        if (users.size() == 0) {
            // 跳转到用户名有误提示页
            resp.sendRedirect("/loginUsernameError.html");
        } else if (users.get(0).getUserPwd().equals(encrypt(req.getParameter("userPwd")))) {
            //4 跳转到首页
            resp.sendRedirect("/showSchedule.html");
        } else {
            // 跳转到密码有误提示页
            resp.sendRedirect("/loginUserPwdError.html");
        }
    }
}