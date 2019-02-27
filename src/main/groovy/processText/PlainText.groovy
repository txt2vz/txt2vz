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
        Map m = request.getParameterMap();

        String networkType = m['networkType'][0];
        float powerValue = m['cooc'][0] as Float
        int maxWordPairs = m['maxLinks'][0] as Integer
        int highFreqWords = m['maxWords'][0] as Integer

        WordPairsExtractor wpe = new WordPairsExtractor(powerValue, maxWordPairs, highFreqWords)

        Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> stemT2 = wpe.getWordPairWithCooc(text)

        WordPairsToJSON wptj = new WordPairsToJSON()

        String json = (networkType == 'forceNet') ? wptj.getJSONgraph(stemT2) : wptj.getJSONtree(stemT2)

        response.getWriter().println(json)
    }
}