package lucene

import groovy.servlet.GroovyServlet
import groovy.transform.CompileStatic
import org.apache.lucene.index.Term
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

@CompileStatic
class LuceneToJSON extends GroovyServlet {

	void init(ServletConfig config) {
		System.out.println " LuceneToJSON Servlet initialized"
	}

	void service(HttpServletRequest request, HttpServletResponse response) {

		def m = request.getParameterMap()

		String luceneQuery = request.getParameter("luceneQuery");
		String category = request.getParameter("category")

		println "category: $category lucenequery: $luceneQuery"

		Path localPath = Paths.get('Indexes/R3')
		Path linuxPath = Paths.get('/var/lib/jetty/webapps/Indexes/R3')

		Directory directory = localPath.toFile().exists() ? FSDirectory.open(localPath): FSDirectory.open(linuxPath)
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
		String json = wpe.getJSONnetwork(directory, bq)
		response.getWriter().println(json)
	}
}

//R3  catsFreq [01_corn:237, 02_crude:578, 07_ship:286]


