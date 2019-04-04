package processText

import groovy.io.FileType
import groovy.transform.CompileStatic
import org.apache.tika.Tika

@CompileStatic
class WordPairsExtractor {

    private int highFreqWords = 40
    private int maxWordPairs = 80
    private float powerValue = 0.5
    private PorterStemmer stemmer = new PorterStemmer()

    private Map<String, Map<String, Integer>> stemInfo = [:]
    private Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]

    WordPairsExtractor(Float powerIn, int maxL, int hfq) {
        this.powerValue = powerIn
        this.maxWordPairs = maxL
        this.highFreqWords = hfq
    }

    WordPairsExtractor() {
    }

   Tuple2< Map<Tuple2<String, String>, Double>,Map<String, Map<String, Integer>> > wordPairCooc(File dir) {

        Tika t = new Tika();
       // Map<Tuple2<String, String>, Double> tuple2CoocMap

        dir.eachFileRecurse(FileType.FILES) { file ->
            def fileText = t.parseToString(file)

            List<String> words = fileText.replaceAll(/\W/, "  ").toLowerCase().tokenize().minus(StopSet.stopSet).findAll {
                it.size() >= 2 && it.charAt(0).isLetter() && it.charAt(1).isLetter()
            }
            println " words size: " + words.size() + " unique words " + words.unique(false).size()

            Map<String, List<Integer>> stemmedWordPositionsMap = buildStemMaps(words)
            Set<String> stemmedWords = stemmedWordPositionsMap.sort { -it.value.size() }.take(highFreqWords).keySet()

         //   Map<Tuple2<String, String>, Double> tuple2CoocMap = compareWordPairs(stemmedWords, stemmedWordPositionsMap,)
            compareWordPairs(stemmedWords, stemmedWordPositionsMap,)

            println "Take 10 steminfo: " + stemInfo.take(20)
//            println "Take 10 tuple2coocMap " + tuple2CoocMap.take(20)


        }
        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
        return new Tuple2 (t2Cooc, stemInfo)
        //tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
       // return tuple2CoocMap
    }



   // Map<Tuple2<String, String>, Double> wordPairCooc(String s) {

      //  s = s ?: "empty text"


        // smallStopSet2);//  stopSet)




     //   Tuple2<Map<String, List<Integer>>, Map<String, Map<String, Integer>>> stemMaps = buildStemMaps(words)

                //stemMaps.first
      //  Map<String, Map<String, Integer>> stemInfo = stemMaps.second





      // return tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
   // }

    private Map <String, List<Integer>> buildStemMaps(List<String> words) {

//stemmed word is key, value is map of original word forms and their frequencies


        //stemmed word is key and value is a list of positions where any of the words occur
        Map<String, List<Integer>> stemmedWordPositionsMap = [:]

        for (int wordPosition = 0; wordPosition < words.size(); wordPosition++) {

            String word = words[wordPosition]
            String stemmedWord = stemmer.stem(word)
            stemmedWordPositionsMap[stemmedWord] = stemmedWordPositionsMap.get(stemmedWord, []) << wordPosition

            Map<String, Integer> forms = stemInfo.get((stemmedWord), [(word): 0])

            int n = forms.get((word)) ?: 0
            forms.put((word), n + 1)

            stemInfo[(stemmedWord)] = forms
        }
        return stemmedWordPositionsMap
    }

    private void compareWordPairs(Set<String> stemmedWords, Map<String, List<Integer>> stemmedWordPositionsMap) {

        //Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]
//check every possible stemmed word pair
        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {

                String stemmedWord0 = stemmedWords[i]
                String stemmedWord1 = stemmedWords[j]
                Tuple2<String, String> wordPair = new Tuple2(stemmedWord0, stemmedWord1)
               // String mostFrequentForm0 = stemInfo[stemmedWord0].max { it.value }.key
               // String mostFrequentForm1 = stemInfo[stemmedWord1].max { it.value }.key
               // Tuple2<String, String> wordPair = new Tuple2(mostFrequentForm0, mostFrequentForm1)

                final double coocDocValue = getCooc(stemmedWordPositionsMap[(stemmedWord0)] as int[], stemmedWordPositionsMap[(stemmedWord1)] as int[])
   //             tuple2CoocMap.put(wordPair, coocDocValue)

                double coocTotalValue = tuple2CoocMap[(wordPair)] ?: 0
                coocTotalValue = coocTotalValue + coocDocValue
                tuple2CoocMap.put(wordPair, coocTotalValue)
            }
        }
       // return tuple2CoocMap
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