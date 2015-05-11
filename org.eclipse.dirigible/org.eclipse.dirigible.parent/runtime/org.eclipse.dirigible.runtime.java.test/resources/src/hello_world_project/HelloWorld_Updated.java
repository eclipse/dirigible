package hello_world_project;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorld {
	
	public void service(HttpServletRequest request, HttpServletResponse response, Map<String, Object> scope) throws Exception {
		System.out.print("Hello World Updated!");
	}
}