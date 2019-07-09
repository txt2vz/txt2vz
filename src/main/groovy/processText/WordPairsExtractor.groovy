package processText

import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode


@CompileStatic
class WordPairsExtractor {

    public static final boolean USE_LOG = false

    private int highFreqWords = 80
    private int maxWordPairs = 200
    private float powerValue = 0.5

    private PorterStemmer stemmer = new PorterStemmer()

    private Map<String, Map<String, Integer>> stemInfo = [:]
    private Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]

    WordPairsExtractor(Float powerIn, int maxL, int hfqWords) {
        this.powerValue = powerIn
        this.maxWordPairs = maxL
        this.highFreqWords = hfqWords
    }

    WordPairsExtractor() {
    }

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> processText(File f) {

        println "file is $f size : " + f.size()

        analyseDocument(f.text)

        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
     //   Map<Tuple2<String, String>, Double> t2Freq = t2CoocMapLinkBoost( t2Cooc).asImmutable()
    //    Map<Tuple2<String, String>, Double> t2Freq = LinkBoost.linkBoost( t2Cooc, 'japanes').asImmutable()

    //    println "t2Freq $t2Freq"
        println "t2cooc $t2Cooc"

        return new Tuple2(t2Cooc, stemInfo)
    }

 //   Tuple3<Map<Tuple2<String, String>, Double>, Map<Tuple2<String, String>, Double>,  Map<String, Map<String, Integer>>> processText(String s) {
    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> processText(String s, String boosttWord='~') {
        analyseDocument(s)

        String stemmedBoostWord = stemmer.stem(boosttWord)
        println "stemmedBoostWord: $stemmedBoostWord"
        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
       // Map<Tuple2<String, String>, Double> t2Freq = t2CoocMapLinkBoost( t2Cooc).asImmutable()
        Map<Tuple2<String, String>, Double> t2Freq = LinkBoost.linkBoost( t2Cooc, stemmedBoostWord).asImmutable()

        println "t2Freq $t2Freq"
        println "t2cooc $t2Cooc"

        return new Tuple2( t2Freq, stemInfo)
    }

    Tuple3<Map<Tuple2<String, String>, Double>, Map<Tuple2<String, String>, Double>,  Map<String, Map<String, Integer>>> processDirectory(File f) {

        int fileCount = 0
        f.eachFileRecurse(FileType.FILES) { file ->
            println "Analysiing file $fileCount: " + file.getAbsoluteFile()
            analyseDocument(file.text)
            fileCount++
        }
        println "Total fileCount: $fileCount"

        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
      //  Map<Tuple2<String, String>, Double> t2Freq = t2CoocMapLinkBoost( t2Cooc).asImmutable()

        Map<Tuple2<String, String>, Double> t2Freq = LinkBoost.linkBoost( t2Cooc).asImmutable()
        println "t2Freq $t2Freq"
        println "t2cooc $t2Cooc"

        return new Tuple3(t2Cooc, t2Freq, stemInfo)
    }


    private void analyseDocument(String s) {

     //   List<String> words = s.replaceAll(~/^\W/, '').toLowerCase().tokenize().minus(StopSet.stopSet).findAll {
        println "s take 200 " + s.take(200)

       // s.replaceAll(/\W/, "  ")

        List<String> words = s.replaceAll(/\W/, ' ').toLowerCase().tokenize().minus(StopSet.stopSet).findAll {
            it.size() > 1 && it.charAt(0).isLetter() //&& it.charAt(1).isLetter()
        }

        println "Words size: " + words.size() + " Unique words " + words.unique(false).size()
        println "words 20 " + words.take(20)

        Map<String, List<Integer>> stemmedWordPositionsMap = buildStemMaps(words)

       // println "stemmedWordPostionnsMap: $stemmedWordPositionsMap"

        compareWordPairs(stemmedWordPositionsMap.sort { -it.value.size() }.take(highFreqWords))

        println "StemInfo size: " + stemInfo.size() + " Take 10 steminfo: " + stemInfo.sort {
            -it.value.size()
        }.take(20)
        println "Tuple2Cooc size: " + tuple2CoocMap.size() + " Take 20 Sorted:" + tuple2CoocMap.sort {
            -it.value
        }.take(20)

        println ""
    }

    private Map<String, List<Integer>> buildStemMaps(List<String> words) {

        //stemmed word is key and value is a list of positions where any of the words occur
        Map<String, List<Integer>> stemmedWordPositionsMap = [:]

        for (int wordPosition = 0; wordPosition < words.size(); wordPosition++) {

            String word = words[wordPosition]
            String stemmedWord = stemmer.stem(word)
            stemmedWordPositionsMap[stemmedWord] = stemmedWordPositionsMap.get(stemmedWord, []) << wordPosition

            Map<String, Integer> wordForms = stemInfo.get((stemmedWord), [(word): 0])

            final int n = wordForms.get((word)) ?: 0
            wordForms.put((word), n + 1)

            stemInfo[(stemmedWord)] = wordForms
        }
        return stemmedWordPositionsMap
    }

    private void compareWordPairs(Map<String, List<Integer>> stemmedWordPositionsMap) {

        Set<String> stemmedWords = stemmedWordPositionsMap.keySet()

//check every possible stemmed word pair
        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {

                String stemmedWord0 = stemmedWords[i]
                String stemmedWord1 = stemmedWords[j]
                Tuple2<String, String> wordPair = new Tuple2(stemmedWord0, stemmedWord1)

                final double coocDocValue = getCooc(stemmedWordPositionsMap[(stemmedWord0)] as int[], stemmedWordPositionsMap[(stemmedWord1)] as int[])

                if (coocDocValue > 0) {
                    double coocTotalValue = tuple2CoocMap[(wordPair)] ?: 0

                    coocTotalValue = USE_LOG ? coocTotalValue + Math.log(coocDocValue) : coocTotalValue + coocDocValue
                    tuple2CoocMap.put(wordPair, coocTotalValue)
                }
            }
        }
    }

    private double getCooc(int[] w0Positions, int[] w1Positions) {
        final int MAX_DISTANCE = 10;
        double coocValue = 0

        for (int w0pos : w0Positions) {
            for (int w1pos : w1Positions) {
                assert w0pos != w1pos
                final int distance = Math.abs(w0pos - w1pos) - 1
                if (distance < MAX_DISTANCE) {
                    coocValue = coocValue + Math.pow(powerValue, distance)
                }
            }
        }
        return coocValue
    }
}