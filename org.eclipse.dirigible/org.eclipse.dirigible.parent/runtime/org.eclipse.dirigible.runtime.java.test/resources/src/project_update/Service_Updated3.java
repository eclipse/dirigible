package project_update;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Service {
	
	public void service(HttpServletRequest request, HttpServletResponse response, Map<String, Object> scope) throws Exception {
		System.out.print("Hello from Service Updated 3!");
	}
}