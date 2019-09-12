package processText

import groovy.json.JsonSlurper
import groovy.servlet.GroovyServlet

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class TextToJsonServlet extends GroovyServlet {

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wordPairData

    void init(ServletConfig config) {
        System.out.println "TextToJsonServlet Servlet initialized"
    }

    void service(HttpServletRequest request, HttpServletResponse response) {
        String text = request.getParameter("text");
        String boostWord = request.getParameter("boostWord")
        String fileName = request.getParameter('fileName')

        WordPairsExtractor wpe = new WordPairsExtractor()

        boolean jsonFile = fileName.endsWith('.json')
        def wordPairAndCoocFreqBoost

        if (jsonFile) {
            def jsonSlurper = new JsonSlurper()
            wordPairData = jsonSlurper.parseText(text)
            Map<Tuple2<String, String>, Double> t2Cooc = wordPairData.first

            wordPairAndCoocFreqBoost = LinkBoost.linkBoost(t2Cooc, boostWord)
        } else {

            wordPairData = wpe.processText(text, boostWord)
        }

        WordPairsToJSON wptj = new WordPairsToJSON()

        String json = (jsonFile) ? wptj.getJSONtree(wordPairAndCoocFreqBoost, wordPairData.second) :
                wptj.getJSONtree(wordPairData.first, wordPairData.second)

        response.getWriter().println(json)
    }
}