package processText

import groovy.json.JsonSlurper
import groovy.servlet.GroovyServlet

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class TextToJsonServlet extends GroovyServlet {

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

    void init(ServletConfig config) {
        System.out.println "TextToJsonServlet Servlet initialized"
    }

    void service(HttpServletRequest request, HttpServletResponse response) {
        String text = request.getParameter("text");
        String boostWord = request.getParameter("boostWord")
        String fileName = request.getParameter('fileName')

        println "In Servlet boostWord:  $boostWord fileName: $fileName"

        WordPairsExtractor wpe = new WordPairsExtractor()

        boolean jsonFile = fileName.endsWith('.json')
        def wordPairAndCoocFreqBoost

        if (jsonFile) {
            def jsonSlurper = new JsonSlurper()
            wpData = jsonSlurper.parseText(text)
            Map<Tuple2<String, String>, Double> t2Cooc = wpData.first

            // def stemInfo = wpData.second
            wordPairAndCoocFreqBoost = LinkBoost.linkBoost(t2Cooc, boostWord)
        } else {

            wpData = wpe.processText(text, boostWord)
        }


        WordPairsToJSON wptj = new WordPairsToJSON(wpData.second)

        String json = (jsonFile) ? wptj.getJSONtree(wordPairAndCoocFreqBoost) :
                wptj.getJSONtree(wpData.first)

        response.getWriter().println(json)
    }
}