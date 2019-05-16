package processText

import groovy.io.FileType
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.apache.tika.Tika

@CompileStatic
class WordPairsExtractor {

    public static final boolean USE_LOG = false

    private int highFreqWords = 80
    private int maxWordPairs = 40
    private float powerValue = 0.5

    private PorterStemmer stemmer = new PorterStemmer()
    private Tika t = new Tika();

    private Map<String, Map<String, Integer>> stemInfo = [:]
    private Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]

    WordPairsExtractor(Float powerIn, int maxL, int hfq) {
        this.powerValue = powerIn
        this.maxWordPairs = maxL
        this.highFreqWords = hfq
    }

    WordPairsExtractor() {
    }


    Tuple3<Map<Tuple2<String, String>, Double>, Map<Tuple2<String, String>, Double>,  Map<String, Map<String, Integer>>> processDirectory(File f) {

        int fileCount = 0
        f.eachFileRecurse(FileType.FILES) { file ->
            //     println file.name.charAt(6)
            //     if (file.name.charAt(6) == 'B' ) {
                println "File $fileCount: " + file.getAbsoluteFile()
                analyseDocument(t.parseToString(file))
                fileCount++
        }
        println "Total fileCount: $fileCount"

      //  Map<Tuple2<String, String>, Double> t2Freq = t2CoocMapLinkBoost( tuple2CoocMap.take(maxWordPairs).asImmutable() )

        Map<Tuple2<String, String>, Tuple3<Double, Integer, Integer>> t2Freq = t2CoocMapLinkBoost( tuple2CoocMap.take(maxWordPairs).asImmutable() )

        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()

        println "t2Freq $t2Freq"
        println "t2cooc $t2Cooc"

        //        return new Tuple2(t2Cooc, stemInfo)
        return new Tuple3(t2Cooc, t2Freq, stemInfo)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    //give a boost to cooc value based of frequency of an item in the list
 //   Map<Tuple2<String, String>, Double> t2CoocMapLinkBoost(Map<Tuple2<String, String>, Double> t2cocOrig) {
    Map<Tuple2<String, String>, Tuple3<Double, Integer, Integer>> t2CoocMapLinkBoost(Map<Tuple2<String, String>, Double> t2cocOrig) {
        println "t2coocOrig.size " + t2cocOrig.size()

        Map<String, Integer> wordLinkCount = t2cocOrig.keySet().collectMany { [it.first, it.second] }.countBy {
            it
        }.sort { -it.value }

        println "wordLinkCount: $wordLinkCount"
        println ""

        Map t2bFreq = t2cocOrig.collectEntries { k,  v ->

            final int frst = (Integer) wordLinkCount[k.first] ?: 0
            final int scnd = (Integer) wordLinkCount[k.second] ?: 0

            assert frst>0 && scnd >0
            final int total = frst + scnd
            final int minLins = Math.min(frst,scnd)
            final int srtValue = Math.min(frst, scnd)  * v * total
            [(k): new Tuple3(v, frst, scnd)]

           // [(k): v * (total + 1) * scnd]
        }
        def ordby = new OrderBy([ {it.value.second }, {it.value.first}])
        // Map m2 =  t2bFreq.sort { - Math.min(it.value.second, it.value.third) }
        Map m2 =  t2bFreq.sort ( ordby )

        println "m2 $m2"

        return  m2//t2bFreq.sort { - Math.min(it.value.second, it.value.first) }
    }

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> processText(String s) {
        analyseDocument(s)
        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
        Map<Tuple2<String, String>, Tuple3<Double, Integer, Integer>>  t2Freq = t2CoocMapLinkBoost(tuple2CoocMap).take(maxWordPairs).asImmutable()

       // return new Tuple2(t2Cooc, stemInfo)
        return new Tuple2(t2Freq, stemInfo)
    }


    private void analyseDocument(String s) {

        List<String> words = s.replaceAll(/\W/, "  ").toLowerCase().tokenize().minus(StopSet.stopSet).findAll {
            it.size() > 2 && it.charAt(0).isLetter() && it.charAt(1).isLetter()
        }
        println "Words size: " + words.size() + " Unique words " + words.unique(false).size()

        Map<String, List<Integer>> stemmedWordPositionsMap = buildStemMaps(words)

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