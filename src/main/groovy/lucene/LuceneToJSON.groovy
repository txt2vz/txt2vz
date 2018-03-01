package lucene

import groovy.servlet.GroovyServlet
import org.apache.tika.Tika
import processText.GenerateWordLinksLucene

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LuceneToJSON extends GroovyServlet {

    void init(ServletConfig config) {
        System.out.println " LuceneToJSON Servlet initialized"
    }

    void service(HttpServletRequest request, HttpServletResponse response) {

         def m = request.getParameterMap();

        GenerateWordLinksLucene gw = new GenerateWordLinksLucene(m);
        def json = gw.getJSONnetwork()

        response.getWriter().println(json)
    }
}



