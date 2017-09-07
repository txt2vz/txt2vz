package url

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.tika.Tika
//import org.jsoup.Jsoup

import groovy.servlet.GroovyServlet
import processText.GenerateWordLinks

class UrlToJSON extends GroovyServlet{

	void init(ServletConfig config) {
		System.out.println " UrlToJSON Servlet initialized"
	}

	void service(HttpServletRequest request, HttpServletResponse response) {

		String urlText = request.getParameter("urlText");
		def url = urlText.toURL()
	
	//	def extractedText = Jsoup.parse(url.getText()).text()		
	
		Tika tika = new Tika();
		def extractedText = tika.parseToString(url)
	
		def m = request.getParameterMap();

		GenerateWordLinks gw = new GenerateWordLinks(m);
		def json = gw.getJSONnetwork(extractedText)

		response.getWriter().println(json)
	}
}