package processText

import spock.lang.Specification
import groovy.json.JsonSlurper

class WordPairsToJSONTest extends Specification {

    def "json from map"() {

        given:
        def stemmer = new PorterStemmer()
        Map<Tuple2<String, String>, Double> wordPairMap = [['ice', 'sheet']: 8.029296875, ['ice', 'melting']: 5.888671875, ['rain', 'winter']: 3.62890625, ['ice', 'greenland']: 3.455078125, ['rain', 'snow']: 2.958984375, ['study', 'scientists']: 2.765625, ['ice', 'darker']: 2.638671875, ['warm', 'flow']: 2.5703125, ['image', 'caption']: 2.25, ['rain', 'important']: 2.0390625, ['found', 'analysis']: 2.03125, ['snow', 'temperatures']: 2.0234375, ['scientists', 'find']: 2.0, ['taking', 'place']: 2.0, ['sea', 'level']: 2.0, ['rain', 'melting']: 1.970703125, ['melting', 'greenland']: 1.75, ['winter', 'falls']: 1.75, ['ice', 'rain']: 1.72265625, ['rain', 'falls']: 1.625] as Map<Tuple2<String, String>, Double>

        def wrds = []
        Map<Tuple2<String, String>, Double> wordTupleMap = wordPairMap.collectEntries { k, v ->

            wrds << k[0]
            wrds << k[1]
            [(new Tuple2(stemmer.stem(k[0]), stemmer.stem(k[1]))): v]

        }

        WordPairsToJSON wptj = new WordPairsToJSON([:])
        JsonSlurper slurper = new JsonSlurper()
        WordPairsExtractor wpe = new WordPairsExtractor()
        println "wrds $wrds"
        wpe.buildStemMaps(wrds)
        Map<String, Map<String, Integer>> stemInfo = wpe.stemInfo
        println "st $stemInfo "
        println "wordpartuplemap $wordTupleMap"

        when:
        println "stemInfor keyset " + stemInfo.keySet()
        println "stemInfor $stemInfo"
        def jsonText = wptj.getJSONtree(wordTupleMap, stemInfo)
        def json = slurper.parseText(jsonText)
        println "json: $json"

        then:
        json.name == 'ice'
        json.children[0].name == 'sheet'
    }
}
