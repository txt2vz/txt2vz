package processText

import groovy.servlet.GroovyServlet

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


public class TextToJSON extends GroovyServlet {

	void init(ServletConfig config) {
		System.out.println " Text Servlet initialized"
	}	

	void service(HttpServletRequest request, HttpServletResponse response) {
		def text = request.getParameter("text");
		def m = request.getParameterMap();	
	
		//WordPairsExtractor wpe = new WordPairsExtractor(m);
		def json =  new WordPairsExtractor(m).getJSONnetwork(text)

		response.getWriter().println(json)
	}
}