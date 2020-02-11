package processText

import spock.lang.Specification

class WordPairsExtractorTest extends Specification
{
    def "nlp 1 "(){

        when:
        WordPairsExtractor wpe = new WordPairsExtractor()   //(0.5, 100, 200])
        String s = 'once United Nations george OSS today'

    }


    def "tuple2 freq test "() {

        def wrdMap = [:]

        when:
        def t2List = [new Tuple2('a', 'b'), new Tuple2('a', 'd'), new Tuple2('b', 'a')]

        t2List.each { t2 ->
            String firstWord = t2.first
            final int n0 = wrdMap.get(firstWord) ?: 0
            wrdMap.put(firstWord, n0 + 1)

            String secondWord = t2.second
            final int n1 = wrdMap.get(secondWord) ?: 0
            wrdMap.put(secondWord, n1 + 1)
        }

        then:
        println "t2List " + t2List
        println "wrdMap $wrdMap"
        wrdMap['a'] == 3
        wrdMap['d'] == 1
    }
}