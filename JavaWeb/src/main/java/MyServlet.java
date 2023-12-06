import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/bill")
public class MyServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String name = req.getParameter("name");

        String info = "yes";
        if ("菠萝".equals(name)) {
            info = "菠萝不行！";
        }

        res.setContentType("text/html");
        res.setCharacterEncoding("utf-8");
        PrintWriter writer = res.getWriter();
        writer.write(info);
    }
}
