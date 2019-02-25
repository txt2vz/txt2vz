package processText

class WordPairsExtractor {

    private int highFreqWords = 40
    private int maxWordPairs = 80
    private float powerValue = 0.5
    private String networkType = "tree"
    private def stemmer = new PorterStemmer()

    WordPairsExtractor(String netType, Float cin, int maxL, int hfq) {
        networkType = netType
        this.powerValue = cin
        this.maxWordPairs = maxL
        this.highFreqWords = hfq

        println "**GenerateWordLinks constructor - cocoIn: $powerValue maxWordPairs: $maxWordPairs highFreqWords: $highFreqWords "
    }

    WordPairsExtractor() {
    }

    WordPairsExtractor(Map userParameters) {
        networkType = userParameters['networkType'][0];
        powerValue = userParameters['cooc'][0] as Float
        maxWordPairs = userParameters['maxLinks'][0] as Integer
        highFreqWords = userParameters['maxWords'][0] as Integer
        println "in GWL construction highFreqWords = $highFreqWords netTYpe $networkType userParameters: $userParameters"
    }

    String getJSONnetwork(String s) {

        //s=new File ('athenaBookChapter.txt').text
        s = s ?: "empty text"

        def words = s.replaceAll(/\W/, "  ").toLowerCase().tokenize().minus(StopSet.stopSet)   // smallStopSet2);//  stopSet)

        println " words size: " + words.size() + " unique words " + words.unique(false).size()
     
        def stemInfo = [:] //stemmed word is key and value is a map of a particular word form and its frequency
        def stemmedWordPositionsMap = [:] //stemmed word is key and value is a list of positions where any of the words occur
        def tuple2CoocMap = [:]  //word pair tuple is key - value is cooc value summed across all docs

        //min word size 1 or 2?
        words.findAll { it.size() >= 2 }
                .eachWithIndex { it, indexWordPosition ->

            def stemmedWord = stemmer.stem(it)
            stemmedWordPositionsMap[stemmedWord] = stemmedWordPositionsMap.get(stemmedWord, []) << indexWordPosition

            def forms = [:]
            forms = stemInfo.get((stemmedWord), [(it): 0])

            def n = forms.get((it)) ?: 0
            forms.put((it), n + 1)

            stemInfo[(stemmedWord)] = forms
        }

        println "take 5 steminfo: " + stemInfo.take(5)

        //sort by size of list (word frequency)
        stemmedWordPositionsMap = stemmedWordPositionsMap.sort { -it.value.size() }

        //wordToFormsMap = wordToFormsMap.drop(wordToFormsMap.size() - highFreqWords)
        stemmedWordPositionsMap = stemmedWordPositionsMap.take(highFreqWords)

        println "after take wordposmap $stemmedWordPositionsMap  wortopositmap.size " + stemmedWordPositionsMap.size()

        def wordPairList = []

        buildTuple2CoocMap(stemmedWordPositionsMap, tuple2CoocMap)
        String json = new WordPairsToJSON().getJSON(tuple2CoocMap, stemInfo, maxWordPairs, networkType)
        println "json: $json"
        return json
    }

    private void buildTuple2CoocMap(Map stemmedWordPositionsMap, Map tuple2CoocMap) {

//check every possible stemmed word pair
        def stemmedWords = stemmedWordPositionsMap.keySet()
        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                String stemmedWord0 = stemmedWords[i]
                String stemmedWord1 = stemmedWords[j]

                Tuple2 wordPair = new Tuple2(stemmedWord0, stemmedWord1)

                double coocDocValue = getCooc(stemmedWordPositionsMap[(stemmedWord0)] as int[], stemmedWordPositionsMap[(stemmedWord1)] as int[])
                double coocTotalValue = tuple2CoocMap[(wordPair)] ?: 0
                coocTotalValue = coocTotalValue + coocDocValue
                tuple2CoocMap << [(wordPair): coocTotalValue]
            }
        }
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

    //powers for 0.9 - power function could be expensive
    def final powers = [
            0 : 1,
            1 : 0.9,
            2 : 0.81,
            3 : 0.729,
            4 : 0.6561,
            5 : 0.59049,
            6 : 0.531441,
            7 : 0.4782969,
            8 : 0.43046721,
            9 : 0.387420489,
            10: 0.34867844,
            11: 0.313810596,
            12: 0.282429536,
            13: 0.254186583,
            14: 0.228767925
    ]
}