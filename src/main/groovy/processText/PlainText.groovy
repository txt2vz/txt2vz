package processText

import groovy.servlet.GroovyServlet
import groovy.transform.CompileStatic

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CompileStatic
public class PlainText extends GroovyServlet {

	void init(ServletConfig config) {
		System.out.println "Plain text Servlet initialized"
	}

	@CompileStatic
	void service(HttpServletRequest request, HttpServletResponse response) {
		String text = request.getParameter("text");
		Map m = request.getParameterMap();
		//System.out.println "m " + m + " m coooc  xxxx  " + m['cooc'][0]
	//	String json =  new WordPairsExtractor(m).getJSONnetwork(text)

	//	String networkType = m['networkType'][0];
	//	System.out.println "net typey " + networkType

		// String s = userParameters['cooc'][
		String networkType = m['networkType'][0];
		float     powerValue = m['cooc'][0] as Float
		int   maxWordPairs = m['maxLinks'][0] as Integer
		int  highFreqWords = m['maxWords'][0] as Integer


		WordPairsExtractor wpe = new WordPairsExtractor(networkType, powerValue, maxWordPairs, highFreqWords)
		String json = wpe.getJSONnetwork(text)

		response.getWriter().println(json)
	}
}