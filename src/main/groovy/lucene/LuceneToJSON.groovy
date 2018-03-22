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

		//for docker
		String linuxpath = '/var/lib/jetty/webapps/Indexes/R3'

		String localpath ='Indexes/R3'

		String luceneQuery = request.getParameter("luceneQuery");
		String category = request.getParameter("category")

		//        def q0 = "contents:$luceneQuery AND category:$category"
		// new QueryParser("contents", new StandardAnalyzer()).parse(q0);
		///  catsFreq [01_corn:237, 02_crude:578, 07_ship:286]
		//new MatchAllDocsQuery()

		println "category: $category"
		println "m.lucenequery: " + m.luceneQuery;

		//Path indexPath = Paths.get(localpath)
		Path indexPath = Paths.get(linuxpath)
		Directory directory = FSDirectory.open(indexPath)
		BooleanQuery.Builder bqb = new BooleanQuery.Builder()

		if (category != '*:*'){
			TermQuery  catQ = new TermQuery(new Term(BuildIndex.FIELD_CATEGORY_NAME, category));
			println "catQ: $catQ"
			bqb.add(catQ, BooleanClause.Occur.FILTER)
		}

		Query q = new TermQuery(new Term(BuildIndex.FIELD_CONTENTS,luceneQuery))
		bqb.add(q, BooleanClause.Occur.MUST)
		BooleanQuery bq = bqb.build()

		println "bq: $bq"

		WordPairsExtractor wpe = new WordPairsExtractor(m);
		def json = wpe.getJSONnetwork(directory, bq)
		response.getWriter().println(json)
	}
}



