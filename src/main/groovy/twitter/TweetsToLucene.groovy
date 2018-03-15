package twitter

import groovy.servlet.GroovyServlet
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.RAMDirectory
import processText.WordPairsExtractor

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import twitter4j.*
import twitter4j.conf.*

class TweetsToLucene extends GroovyServlet{

	void init(ServletConfig config) {
		System.out.println " Text Servlet initialized"
	}

	void service(HttpServletRequest request, HttpServletResponse response) {

		RAMDirectory ramDir = new RAMDirectory()
		IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer())
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE)

		FieldType ft = new FieldType();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorPositions(true);
		ft.setStored(true)
		ft.setTokenized(true)

		IndexWriter writer = new IndexWriter(ramDir, iwc)
		
		String queryText = request.getParameter("q");

		Twitter twitter = getTwitterAuth();
		Query query = new Query(queryText + "-filter:retweets");
		query.setCount(40)
		//GeoLocation sheffield = new GeoLocation(53.377127, -1.467705);
		//query.setGeoCode(sheffield, distance, Query.KILOMETERS)
		println "twitter query text $queryText"
		QueryResult result = twitter.search(query);

		int twCount = 0;
		String combinedTweetText= ""

		for   (i in 0..20){//(;;) {
			result = twitter.search(query);
			def tweets = result.getTweets()

			tweets.each {
				twCount++
			//	combinedTweetText = combinedTweetText + it.getText()

				def doc = new Document()
				doc.add(new Field("contents", it.getText(), ft))
				writer.addDocument(doc);
			}

			query = result.nextQuery()
			if (query == null) break;
		}

		println "Total docs in index: ${writer.maxDoc()}"
		writer.close()
		println( "twCount : $twCount" )
	//	println "combined tweetText first 40" + combinedTweetText.take(40)
		
		def m = request.getParameterMap()
		//GenerateWordLinks gw = new GenerateWordLinks(m)
		//def json = gw.getJSONnetwork(combinedTweetText)
		//def json =  new WordPairsExtractor(m).getJSONnetwork(combinedTweetText)

		def json =  new WordPairsExtractor(m).getJSONnetwork(ramDir, '*:*')
		println "in twitterTOJSON Json is $json"

		response.getWriter().println(json)
	}	

	private Twitter getTwitterAuth(){

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(TwitterKeys.getConsumerKey())
				.setOAuthConsumerSecret(TwitterKeys.getConsumerSecret())
				.setOAuthAccessToken(TwitterKeys.getAccessToken())
				.setOAuthAccessTokenSecret(TwitterKeys.getAccessTokenSecret());
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		return tf.getInstance();
	}
}