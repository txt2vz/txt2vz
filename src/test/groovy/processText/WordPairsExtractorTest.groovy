package processText

import spock.lang.Specification

class WordPairsExtractorTest extends Specification

{
    def "tuple2 freq test "() {
//        given:
//        def wpe = new WordPairsExtractor()
        def wrdMap = [:]

        when:
        def t2List = [new Tuple2('a', 'b'), new Tuple2('a', 'd'), new Tuple2('b', 'a')]
       // def wrdMap = t2List.collect { it.first }
//        def wpTuple = wpe.processText("one1 two2 three3")
//        def wpCooc = wpTuple.first
//        def stemInfo = wpTuple.second
//        def wpj = new WordPairsToJSON(stemInfo)
//        def jsonText = wpj.getJSONtree(wpCooc)
        //    def json = slurper.parseText(jsonText)

        //	def stemT2 = wpe.processDirectory("one1 two2 three3")
        //	def jsonText = new WordPairsToJSON().getJSONtree(stemT2)

        //def json = slurper.parseText(jsonText)
        t2List.each{t2 ->
            String firstWord = t2.first
            final int n0 = wrdMap.get(firstWord)?: 0
            wrdMap.put(firstWord, n0 + 1)

            String secondWord = t2.second
            final int n1 = wrdMap.get(secondWord)?: 0
            wrdMap.put(secondWord, n1 + 1)
        }



        then:
        def g = 2
        println "t2List " + t2List
        println "wrdMap $wrdMap"
        //  json.name == 'one1


    }
}