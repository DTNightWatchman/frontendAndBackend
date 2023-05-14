import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * created by YT
 * description:
 * User:lenovo
 * Data:2022-07-28
 * Time:20:24
 */
@WebServlet("/getlist")
public class DocSearcherServlet extends HttpServlet {

    private static DocSearcher docSearcher = new DocSearcher();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        if (query == null || query.equals("")) {
            System.out.println("参数不合法");
            resp.sendError(404,"参数不合法");
            return;
        }
        //System.out.println("query= " + query);

        List<Result> results = docSearcher.search(query);
        resp.setContentType("application/json;charset=utf8");
        objectMapper.writeValue(resp.getWriter(),results);

    }


}
