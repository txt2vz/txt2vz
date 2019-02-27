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

    void service(HttpServletRequest request, HttpServletResponse response) {

        String text = request.getParameter("text");
		String networkType = request.getParameter('networkType');
		final float powerValue = request.getParameter('cooc') as Float
		final int maxWordPairs = request.getParameter('maxLinks') as Integer
		final int highFreqWords = request.getParameter('maxWords') as Integer

        WordPairsExtractor wpe = new WordPairsExtractor(powerValue, maxWordPairs, highFreqWords)

        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> stemT2 = wpe.getWordPairWithCooc(text)

        WordPairsToJSON wptj = new WordPairsToJSON()

        String json = (networkType == 'forceNet') ? wptj.getJSONgraph(stemT2) : wptj.getJSONtree(stemT2)

        response.getWriter().println(json)
    }
}