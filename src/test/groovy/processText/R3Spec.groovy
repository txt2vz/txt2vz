package processText

import groovy.json.JsonSlurper
import lucene.BuildIndex
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory

import java.nio.file.Path
import java.nio.file.Paths

class R3Spec extends spock.lang.Specification {

	def "reuters 3 oil "() {
		given:
		def wpe = new WordPairsExtractor()
		JsonSlurper slurper = new JsonSlurper()

		when:
		def jsonText = wpe.getJSONnetwork('Indexes/R3', 'oil')
		def json = slurper.parseText(jsonText)

		then:
		json.name == "oil"
		json.children[0].name == "prices"
	}

	def "reuters 3 wheat "() {
		given:
		def wpe = new WordPairsExtractor()
		JsonSlurper slurper = new JsonSlurper()

		when:
		def jsonText = wpe.getJSONnetwork('Indexes/R3', 'wheat')
		def json = slurper.parseText(jsonText)

		then:
		json.name == "mln"
		json.children[0].name == "tonnes"
		json.children[0].children[0].name == 'wheat'
	}

	def "R3 index crude opec" (){
		given:
		JsonSlurper slurper = new JsonSlurper()
		// def linuxpath = '/var/lib/jetty/webapps/Indexes/R10CrudeL'
		String localpath ='Indexes/R3'

		Path indexPath = Paths.get(localpath)
		Directory directory = FSDirectory.open(indexPath)
		BooleanQuery.Builder bqb = new BooleanQuery.Builder()

		when:
		///  catsFreq [01_corn:237, 02_crude:578, 07_ship:286]
		TermQuery catQ = new TermQuery(new Term(BuildIndex.FIELD_CATEGORY_NAME, '02_crude'));
		Query q = new TermQuery(new Term(BuildIndex.FIELD_CONTENTS,'opec'))
		bqb.add(q, BooleanClause.Occur.MUST)
		BooleanQuery bq = bqb.build()

		WordPairsExtractor wpe = new WordPairsExtractor();
		def jsonText = wpe.getJSONnetwork(directory, bq)
		def json = slurper.parseText(jsonText)

		then:
		json.name == 'bpd'
		json.children[0].name == 'mln'
	}
}