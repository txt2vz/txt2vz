package lucene

import groovy.servlet.GroovyServlet
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import processText.WordPairsExtractor

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.file.Path
import java.nio.file.Paths

class LuceneToJSON extends GroovyServlet {

    void init(ServletConfig config) {
        System.out.println " LuceneToJSON Servlet initialized"
    }

    void service(HttpServletRequest request, HttpServletResponse response) {

        def m = request.getParameterMap()

        def linuxpath = '/var/lib/jetty/webapps/Indexes/R10CrudeL'
        String localpath = 'Indexes/R3'

        def luceneQuery = request.getParameter("luceneQuery");
        def category = request.getParameter("category")

//        def q0 = "contents:$luceneQuery AND category:$category"

        println "category $category"
  //      println "q0 $q0"

        println "m.lucenequery is " + m.luceneQuery;

        Path indexPath = Paths.get(localpath)
        Directory directory = FSDirectory.open(indexPath)

        TermQuery  catQ = new TermQuery(new Term("category", category));
        println "Index info catQ: $catQ"

        BooleanQuery.Builder bqb = new BooleanQuery.Builder()
      //  if (!catQ == "*:*") {
            bqb.add(catQ, BooleanClause.Occur.FILTER)
      //
        Query q = new TermQuery(new Term("contents",luceneQuery))
        bqb.add(q, BooleanClause.Occur.SHOULD)

      ///  catsFreq [01_corn:237, 02_crude:578, 07_ship:286]
        BooleanQuery bq = bqb.build()


        println "bq $bq"
                //new MatchAllDocsQuery()
                // new QueryParser("contents", new StandardAnalyzer()).parse(q0);

        WordPairsExtractor wpe = new WordPairsExtractor(m);
      //  def json = wpe.getJSONnetwork('Indexes/R10CrudeL', 'bp')
       // def json = wpe.getJSONnetwork(linuxpath, 'bp')
        //def json = wpe.getJSONnetwork(localpath, 'bp')
        def json = wpe.getJSONnetwork(directory, bq)


       // def json = new WordPairsExtractor(m).getJSONnetwork('Indexes/R10CrudeL', 'bp')
        response.getWriter().println(json)
    }
}



