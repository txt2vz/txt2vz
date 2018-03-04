package twitter

import groovy.servlet.GroovyServlet

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import twitter4j.*
import twitter4j.conf.*

import processText.GenerateWordLinks

class TwitterToJSON extends GroovyServlet{

	void init(ServletConfig config) {
		System.out.println " Text Servlet initialized"
	}

	void service(HttpServletRequest request, HttpServletResponse response) {
		
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

		for   (i in 0..10){//(;;) {
			result = twitter.search(query);
			def tweets = result.getTweets()

			tweets.each {
				twCount++
				combinedTweetText = combinedTweetText + it.getText()
			}

			query = result.nextQuery()
			if (query == null) break;
		}
		
		println( "twCount : $twCount" )
		println "combined tweetText first 40" + combinedTweetText.take(40)
		
		def m = request.getParameterMap()
		GenerateWordLinks gw = new GenerateWordLinks(m)
		def json = gw.getJSONnetwork(combinedTweetText)
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