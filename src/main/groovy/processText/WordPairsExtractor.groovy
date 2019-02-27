package processText

import groovy.transform.CompileStatic

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

    Tuple2< Map<Tuple2<String,String>,Double>, Map<String,Map<String, Integer>>> getWordPairWithCooc(String s) {

       // println "s is $s"
        s = s ?: "empty text"

       List<String> words = s.replaceAll(/\W/, "  ").toLowerCase().tokenize().minus(StopSet.stopSet)   // smallStopSet2);//  stopSet)

        println " words size: " + words.size() + " unique words " + words.unique(false).size()
     
        Map<String,Map<String, Integer>> stemInfo = [:] //stemmed word is key and value is a map of a particular word form and its frequency
        Map<String, List<Integer>> stemmedWordPositionsMap = [:] //stemmed word is key and value is a list of positions where any of the words occur

        //min word size 1 or 2?
        words.findAll { it.size() >= 2 }
                .eachWithIndex { word, wordPositionIndex ->

            String stemmedWord = new PorterStemmer().stem(word)
            stemmedWordPositionsMap[stemmedWord] = stemmedWordPositionsMap.get(stemmedWord, []) << wordPositionIndex

            Map<String,Integer> forms = stemInfo.get((stemmedWord), [(word): 0])

            int n = forms.get((word)) ?: 0
            forms.put((word), n + 1)

            stemInfo[(stemmedWord)] = forms
        }

        println "Take 5 steminfo: " + stemInfo.take(5)

        Map<Tuple2<String,String>,Double> wordPairWithCooccurence = getTuple2CoocMap(stemmedWordPositionsMap.sort { -it.value.size() }.take(highFreqWords))

        return new Tuple2(wordPairWithCooccurence.sort { -it.value }.take(maxWordPairs), stemInfo)
    }

    Map<Tuple2<String,String>,Double> getTuple2CoocMap(Map <String, List<Integer>> stemmedWordPositionsMap) {

        Map<Tuple2<String,String>,Double> tuple2CoocMap = [:]  //word pair tuple is key - value is sum of cooc

//check every possible stemmed word pair
        Set stemmedWords = stemmedWordPositionsMap.keySet()

        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                String stemmedWord0 = stemmedWords[i]
                String stemmedWord1 = stemmedWords[j]

                Tuple2<String, String> wordPair = new Tuple2(stemmedWord0, stemmedWord1)

                double coocDocValue = getCooc(stemmedWordPositionsMap[(stemmedWord0)] as int[], stemmedWordPositionsMap[(stemmedWord1)] as int[])
                double coocTotalValue = tuple2CoocMap[(wordPair)] ?: 0
                coocTotalValue = coocTotalValue + coocDocValue
                tuple2CoocMap.put(wordPair,coocTotalValue)
            }
        }
        return tuple2CoocMap
    }

    double getCooc(int[] w0Positions, int[] w1Positions) {
        final int MAX_DISTANCE = 10;
        double coocValue = 0

        for (int w0pos: w0Positions){
            for (int w1pos: w1Positions){
                assert w0pos!=w1pos
                final int distance = Math.abs(w0pos - w1pos) - 1
                if (distance < MAX_DISTANCE) {
                    coocValue = coocValue + Math.pow(powerValue, distance)
                }
            }
        }
        return coocValue
    }
}