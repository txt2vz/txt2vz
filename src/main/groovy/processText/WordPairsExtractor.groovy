package processText

import groovy.transform.CompileStatic
import jdk.nashorn.internal.ir.annotations.Immutable

@CompileStatic
class WordPairsExtractor {

    private int highFreqWords = 40
    private int maxWordPairs = 80
    private float powerValue = 0.5

    WordPairsExtractor(Float powerIn, int maxL, int hfq) {
        this.powerValue = powerIn
        this.maxWordPairs = maxL
        this.highFreqWords = hfq
    }

    WordPairsExtractor() {
    }

    Map<Tuple2<String, String>, Double> wordPairCooc(String s) {

        s = s ?: "empty text"

        List<String> words = s.replaceAll(/\W/, "  ").toLowerCase().tokenize().minus(StopSet.stopSet)
        // smallStopSet2);//  stopSet)

        println " words size: " + words.size() + " unique words " + words.unique(false).size()

        //stemmed word is key, value is map of original word forms and their frequencies
        Map<String, Map<String, Integer>> stemInfo = [:]

       //stemmed word is key and value is a list of positions where any of the words occur
        Map<String, List<Integer>> stemmedWordPositionsMap = [:]

        PorterStemmer stemmer = new PorterStemmer()

        //min word size 1 or 2?
        words.findAll { it.size() >= 3  && it.charAt(0).isLetter() && it.charAt(1).isLetter()}
                .eachWithIndex { word, wordPositionIndex ->

            String stemmedWord = stemmer.stem(word)
            stemmedWordPositionsMap[stemmedWord] = stemmedWordPositionsMap.get(stemmedWord, []) << wordPositionIndex

            Map<String, Integer> forms = stemInfo.get((stemmedWord), [(word): 0])

            int n = forms.get((word)) ?: 0
            forms.put((word), n + 1)

            stemInfo[(stemmedWord)] = forms
        }

      //  Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]  //word pair tuple is key - value is sum of cooc


        Set<String> stemmedWords = stemmedWordPositionsMap.sort { -it.value.size() }.take(highFreqWords).keySet()

        Map<Tuple2<String, String>, Double> tuple2CoocMap =  compareWordPairs(stemmedWords, stemInfo, stemmedWordPositionsMap)

        println "Take 10 steminfo: " + stemInfo.take(20)
        println "Take 10 tuple2coocMap " + tuple2CoocMap.take(20)

        return tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
    }

    private LinkedHashMap<Tuple2<String, String>, Double> compareWordPairs(Set<String> stemmedWords, LinkedHashMap<String, Map<String, Integer>> stemInfo, LinkedHashMap<String, List<Integer>> stemmedWordPositionsMap) {

        Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]
//check every possible stemmed word pair
        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {

                String stemmedWord0 = stemmedWords[i]
                String stemmedWord1 = stemmedWords[j]

                String mostFrequentForm0 = stemInfo[stemmedWord0].max { it.value }.key
                String mostFrequentForm1 = stemInfo[stemmedWord1].max { it.value }.key
                Tuple2<String, String> wordPair = new Tuple2(mostFrequentForm0, mostFrequentForm1)

                final double coocDocValue = getCooc(stemmedWordPositionsMap[(stemmedWord0)] as int[], stemmedWordPositionsMap[(stemmedWord1)] as int[])
                double coocTotalValue = tuple2CoocMap[(wordPair)] ?: 0
                coocTotalValue = coocTotalValue + coocDocValue

                tuple2CoocMap.put(wordPair, coocTotalValue)
            }
        }
        return tuple2CoocMap
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