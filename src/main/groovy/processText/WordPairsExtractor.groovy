package processText

import opennlp.NER
import groovy.io.FileType
import groovy.transform.CompileStatic

@CompileStatic
class WordPairsExtractor {

    private int highFreqWords = 80
    private int maxWordPairs = 200
    private float powerValue = 0.999
    boolean useNER = true;

    private PorterStemmer stemmer = new PorterStemmer()

    private Map<String, Map<String, Integer>> stemInfo = [:]
    private Map<Tuple2<String, String>, Double> tuple2CoocMap = [:]

    WordPairsExtractor(float powerIn, int maxL, int hfqWords, boolean useNER) {
        this.powerValue = powerIn
        this.maxWordPairs = maxL
        this.highFreqWords = hfqWords
        this.useNER = useNER
    }

    WordPairsExtractor() {
    }

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> processText(File f) {

        println "file is $f size : " + f.size()

        analyseTextString(f.text)

        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()
        Map<Tuple2<String, String>, Double> t2CoocLinkBoost = LinkBoost.linkBoost(t2Cooc).asImmutable()

        return new Tuple2(t2CoocLinkBoost, stemInfo)
    }

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> processText(String s, String boostWord = '~') {
        analyseTextString(s)

        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()

        String stemmedBoostWord = stemmer.stem(boostWord)
        t2Cooc = LinkBoost.linkBoost(t2Cooc, stemmedBoostWord).asImmutable()

        println "t2cooc: $t2Cooc"

        return new Tuple2(t2Cooc, stemInfo)
    }

    Tuple2<Map<Tuple2<String, String>, Double>, Map<String, Map<String, Integer>>> processAndMergeDirectory(File f, String boostWord = '~') {

        assert f.isDirectory()

        int fileCount = 0
        f.eachFileRecurse(FileType.FILES) { file ->
            println "Analysiing file $fileCount: " + file.getAbsoluteFile()
            analyseTextString(file.text)
            fileCount++
        }
        println "Total fileCount: $fileCount"

        Map<Tuple2<String, String>, Double> t2Cooc = tuple2CoocMap.sort { -it.value }.take(maxWordPairs).asImmutable()

        String stemmedBoostWord = stemmer.stem(boostWord)
        t2Cooc = LinkBoost.linkBoost(t2Cooc, stemmedBoostWord).asImmutable()


        println "t2cooc: $t2Cooc"

        return new Tuple2(t2Cooc, stemInfo)
    }

    private void analyseTextString(String s) {
        List<String> words
      //  useNER = true

        if (useNER) {
            NER ner = new NER()
          //  onlpb.generateNERforModel(s, NER.NERModel.ORGANIZATION.path)   //NER.organizationModel)
            ner.generateNERforAllModels(s)
            words = ner.tokenizeWithNE(s)
        } else {

            words = s.replaceAll(/\W/, ' ').toLowerCase().tokenize().minus(StopSet.stopSet).findAll {
                it.size() > 1 && it.charAt(0).isLetter() //&& it.charAt(1).isLetter()
            }
        }

        println "Words size: " + words.size() + " Unique words: " + words.unique(false).size()
        println "words take 20: " + words.take(20)

        Map<String, List<Integer>> stemmedWordPositionsMap = buildStemMaps(words)

        compareWordPairs(stemmedWordPositionsMap.sort { -it.value.size() }.take(highFreqWords))

        println "StemInfo size: " + stemInfo.size() + " Take 20 steminfo: " + stemInfo.sort {
            -it.value.size()
        }.take(20)
        println "Tuple2Cooc size: " + tuple2CoocMap.size() + " Take 20 Sorted:" + tuple2CoocMap.sort {
            -it.value
        }.take(20)

        println ""
    }

     Map<String, List<Integer>> buildStemMaps(List<String> words) {

        //stemmed word is key and value is a list of positions where any of the words occur
        Map<String, List<Integer>> stemmedWordPositionsMap = [:]

        for (int wordPosition = 0; wordPosition < words.size(); wordPosition++) {

            String word = words[wordPosition]

            String stemmedWord = (word.charAt(0).isUpperCase()) ? word : stemmer.stem(word)
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

                //boos NEs
                final int multiplier0 = (stemmedWord0.charAt(0).isUpperCase()) ? 2 : 1
                final int multiplier1 = (stemmedWord1.charAt(0).isUpperCase()) ? 2 : 1

                final double coocDocValue = getCooc(stemmedWordPositionsMap[(stemmedWord0)] as int[], stemmedWordPositionsMap[(stemmedWord1)] as int[]) * (multiplier0 * multiplier1)

                if (coocDocValue > 0) {
                    double coocTotalValue = tuple2CoocMap[(wordPair)] ?: 0

                    coocTotalValue = coocTotalValue + coocDocValue
                    tuple2CoocMap.put(wordPair, coocTotalValue)
                }
            }
        }
    }

    private double getCooc(int[] w0Positions, int[] w1Positions) {
        final int MAX_DISTANCE = 30;
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