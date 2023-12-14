package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/showSchedule.html", "/schedule/*"})
public class ScheduleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 参数父转子
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 校验 session 中的用户名是否为空
        String username = (String) request.getSession().getAttribute("username");
        if (username != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendRedirect("/index.html");
        }

    }
}
