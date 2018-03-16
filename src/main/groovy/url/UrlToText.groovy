package url

import processText.WordPairsExtractor

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.tika.Tika
//import org.jsoup.Jsoup

import groovy.servlet.GroovyServlet

class UrlToText extends GroovyServlet{

	void init(ServletConfig config) {
		System.out.println " UrlToJSON Servlet initialized"
	}

	void service(HttpServletRequest request, HttpServletResponse response) {

		String urlText = request.getParameter("urlText");
		def url = urlText.toURL()
	
	//	def extractedText = Jsoup.parse(url.getText()).text()		

		def extractedText = new Tika().parseToString(url)
	
		def m = request.getParameterMap();

		def json =  new WordPairsExtractor(m).getJSONnetwork(extractedText)
		response.getWriter().println(json)
	}
}