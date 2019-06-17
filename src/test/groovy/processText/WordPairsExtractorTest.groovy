package processText

import jdk.nashorn.internal.runtime.regexp.joni.Regex
import org.apache.tika.Tika
import spock.lang.Specification

class WordPairsExtractorTest extends Specification

{

    def tikaTest() {

        Tika t = new Tika()

        when:
        File sec1 = new File(/boaData\text\secrecy\590\ev590doc10903.txt/)
        File sec2 = new File(/boaData\text\secrecy\590\ev590doc10933.txt/)
        File sec3 = new File(/boaData\text\secrecy\590\ev590doc10930.txt/)

        File test = new File(/boaData\text\test.txt/)
        File coffee26 = new File(/boaData\text\coffee\0000026/)

        then:

        test.text + '\n' == t.parseToString(test)
        coffee26.text + '\n' == t.parseToString(coffee26)
        sec2.text + '\n' == t.parseToString(sec2)

        sec1.text.take(20) == t.parseToString(sec1).take(20)
        sec2.text.take(70) == t.parseToString(sec2).take(70)
        sec3.text.take(20) == t.parseToString(sec3).take(20)

        def sec3plusN = sec3.text + '\n'
        sec3plusN.reverse().take(20) == t.parseToString(sec3).reverse().take(20)

        def wordsF = sec2.text.replaceAll('[^a-zA-Z0-9 -]', '')
        def wordsT = t.parseToString(sec2).replaceAll('[^a-zA-Z0-9 -]', '')
        wordsF.size() == wordsT.size()
    }


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
        t2List.each { t2 ->
            String firstWord = t2.first
            final int n0 = wrdMap.get(firstWord) ?: 0
            wrdMap.put(firstWord, n0 + 1)

            String secondWord = t2.second
            final int n1 = wrdMap.get(secondWord) ?: 0
            wrdMap.put(secondWord, n1 + 1)
        }


        then:
        def g = 2
        println "t2List " + t2List
        println "wrdMap $wrdMap"
        //  json.name == 'one1


    }
}