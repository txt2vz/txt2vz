package processText

import groovy.json.JsonSlurper

class R3Spec extends spock.lang.Specification {

	def "reuters 3 oil "() {
		given:
		def wpe = new WordPairsExtractor()
		JsonSlurper slurper = new JsonSlurper()

		when:
		def jsonText = wpe.getJSONnetwork('indexes/R3', 'oil')
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
		def jsonText = wpe.getJSONnetwork('indexes/R3', 'wheat')
		def json = slurper.parseText(jsonText)

		then:
		json.name == "mln"
		json.children[0].name == "tonnes"
		json.children[0].children[0].name == 'wheat'
	}
}