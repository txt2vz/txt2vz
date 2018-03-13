package lucene

import groovy.servlet.GroovyServlet
import processText.WordPairsExtractor

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LuceneToJSON extends GroovyServlet {

    void init(ServletConfig config) {
        System.out.println " LuceneToJSON Servlet initialized"
    }

    void service(HttpServletRequest request, HttpServletResponse response) {

         def m = request.getParameterMap();

       //println "yo"

        WordPairsExtractor wpe = new WordPairsExtractor(m);
        def json = wpe.getJSONnetwork('Indexes/R10CrudeL', 'bp')


      // def json = new WordPairsExtractor(m).getJSONnetwork('Indexes/R10CrudeL', 'bp')
        response.getWriter().println(json)
    }
}



