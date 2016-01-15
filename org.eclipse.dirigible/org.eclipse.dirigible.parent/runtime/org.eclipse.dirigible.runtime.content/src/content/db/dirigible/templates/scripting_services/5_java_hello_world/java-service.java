package ${packageName};

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ${fileNameNoExtension} {

    public void service(HttpServletRequest request, HttpServletResponse response, Map<String, Object> scope) throws Exception {
        // print in system output
        System.out.println("Hello World!");
        // print in response
        response.setContentType("text/html");
        response.getWriter().println("Hello World!");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
