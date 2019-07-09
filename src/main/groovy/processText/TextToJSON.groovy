package processText

import groovy.servlet.GroovyServlet

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class TextToJSON extends GroovyServlet {

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> wpData

    void init(ServletConfig config) {
        System.out.println " Text Servlet initialized"
    }

    void service(HttpServletRequest request, HttpServletResponse response) {
        String text = request.getParameter("text");
        String boostWord = request.getParameter("boostWord")

   //     println "Text is $text "
        println "boostWord:  $boostWord"

     //   def m = request.getParameterMap();

        WordPairsExtractor wpe = new WordPairsExtractor()

        wpData=wpe.processText(text, boostWord)
        WordPairsToJSON wptj = new WordPairsToJSON(wpData.second)

        String json = wptj.getJSONtree(wpData.first)
      //  println "json $json"

        response.getWriter().println(json)
    }
}