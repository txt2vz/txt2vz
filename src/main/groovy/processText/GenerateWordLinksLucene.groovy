package processText

import groovy.json.JsonBuilder
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.MultiFields
import org.apache.lucene.index.PostingsEnum
import org.apache.lucene.index.Terms
import org.apache.lucene.index.TermsEnum
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.BytesRef

import java.nio.file.Path
import java.nio.file.Paths

class GenerateWordLinksLucene {

    private int highFreqWords = 14
    private int maxWordPairs = 60
    private float powerValue = 0.5
    private String networkType = "tree"

    GenerateWordLinksLucene(String netType, Float cin, int maxL, int hfq) {
        networkType = netType
        this.powerValue = cin
        this.maxWordPairs = maxL
        this.highFreqWords = hfq

        println "**GenerateWordLinks constructor - cocoIn: $powerValue maxWordPairs: $maxWordPairs highFreqWords: $highFreqWords "
    }


    GenerateWordLinksLucene() {
    }

    GenerateWordLinksLucene(Map userParameters) {
        networkType = userParameters['networkType'][0];
        powerValue = userParameters['cooc'][0] as Float
        maxWordPairs = userParameters['maxLinks'][0] as Integer
        highFreqWords = userParameters['maxWords'][0] as Integer

        maxWordPairs = 20
        highFreqWords = 14

        println "in GWL construction highFreqWords = $highFreqWords netTYpe $networkType"
    }

    String getJSONnetwork() {

        StandardAnalyzer analyzer = new StandardAnalyzer();
        String querystr = "*:*";
        Query q = //new MatchAllDocsQuery()
                new QueryParser("contents", analyzer).parse(querystr);

        int hitsPerPage = 2000;

        Path indexPath = //Paths.get('Indexes/QueensLandFloods')
                Paths.get('Indexes/R10CrudeL')

        Directory directory = FSDirectory.open(indexPath)
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        println "Found " + hits.length + " hits."

        Bits liveDocs = MultiFields.getLiveDocs(reader);

        //s=new File ('athenaBookChapter.txt').text
        //s = s ?: "empty text"

        //def words = s.replaceAll(/\W/, "  ").toLowerCase().tokenize().minus(StopSet.stopSet)
        // smallStopSet2);//  stopSet)

        //	println " words size: " + words.size() + " unique words " + words.unique(false).size()

        def stemmer = new PorterStemmer()
        def stemInfo = [:] //stemmed word is key and value is a map of a particular word form and its frequency
        def tuple2CoocMap = [:]  //word pair tuple is key - value is cooc value summed across all docs
        def wordPairList = []

        hits.each {
            int docNumber = it.doc;
            Document d = searcher.doc(docNumber);

            //stemmed word is key and value is a list of positions where any of the words occur
            def stemmedWordToPositionsMap = [:]

            if (liveDocs == null || liveDocs.get(docNumber)) {
                def doc = reader.document(docNumber);

                //https://lucene.apache.org/core/6_2_0/core/index.html?org/apache/lucene/index/CheckIndex.Status.TermVectorStatus.html
                Terms tv = reader.getTermVector(docNumber, "contents");
                //   if (tv.is(org.apache.lucene.index.TermsEnum)) {
                TermsEnum terms = tv.iterator();
                PostingsEnum p = null;

                BytesRef br = terms.next();
                while (br != null) {

                    String word = br.utf8ToString()
                    if (!StopSet.stopSet.contains(word)) {
                        String stemmedWord = stemmer.stem(word)
                        //   println "word:  $word stemmedWord: $stemmedWord"
                        p = terms.postings(p, PostingsEnum.POSITIONS);

                        //count and store word forms for a stemmed word
                        def forms = [:]
                        forms = stemInfo.get((stemmedWord), [(it): 0])
                        def n = forms.get((word)) ?: 0
                        forms.put((word), n + 1)
                        stemInfo[(stemmedWord)] = forms

                        def positions = []
                        while (p.nextDoc() != PostingsEnum.NO_MORE_DOCS) {
                            int freq = p.freq();
                            //   println "freq: $freq"
                            freq.times {
                                int position = p.nextPosition();
                                //    println "Occurence $it :  position $position"
                                positions << position
                                // wordToPositionsMap[stemmedWord] = wordToPositionsMap.get(stemmedWord, []) << pos
                            }
                            //   println "stemmedWord: $stemmedWord positions: $positions"
                            stemmedWordToPositionsMap << [(stemmedWord): positions]
                        }
                    }
                    br = terms.next();

                }
            }
            //sort by word frequency (number of positions)
            stemmedWordToPositionsMap = stemmedWordToPositionsMap.sort { -it.value.size() }
            //wordToFormsMap = wordToFormsMap.drop(wordToFormsMap.size() - highFreqWords)
            stemmedWordToPositionsMap = stemmedWordToPositionsMap.take(highFreqWords)

              println "after take wordposmap $stemmedWordToPositionsMap  wortopositmap.size " + stemmedWordToPositionsMap.size()
            // Set tups

            //      println "subseqs " + stemmedWordToPositionsMap.keySet().toList().subsequences().findAll { it.size == 2 }
            //  [stemmedWordToPositionsMap.keySet(), stemmedWordToPositionsMap.keySet()].combinations {
            stemmedWordToPositionsMap.keySet().toList().subsequences().findAll { it.size() == 2 }.each {
              //  it.sort()
                String stemmedWord0 = it[0]
                String stemmedWord1 = it[1]
                // assert stemmedWord0 < stemmedWord1
                // if (stemmedWord0 != stemmedWord1) {
                Tuple2 t2 = new Tuple2(stemmedWord0, stemmedWord1)
                double coocDocValue = getCooc(stemmedWordToPositionsMap[stemmedWord0], stemmedWordToPositionsMap[stemmedWord1])
                double coocTotalValue = tuple2CoocMap[t2] ?: 0
                coocTotalValue = coocTotalValue + coocDocValue
                tuple2CoocMap << [(t2): coocTotalValue]

              //  wordPairList << new WordPair(word0: stemmedWord0, word1: stemmedWord1, cooc: coocTotalValue, sortVal: 0.5)
                // }
            }
        }

        tuple2CoocMap = tuple2CoocMap.sort { -it.value }.take(maxWordPairs)

        //wordPairList = wordPairList.sort { -it.cooc }
        //    wordPairList = wordPairList.sort { -it.sortVal }
        println "tuple2CoocMap take 5: " + tuple2CoocMap.take(5)

        //  wordPairList = wordPairList.take(maxWordPairs)
        //def json = getJSONgraph(wordPairList, stemInfo)
        ///    def json;

        //   if (networkType == "forceNet") json = getJSONgraph(wordPairList, stemInfo)
        //    else
        //        json = getJSONtree(wordPairList, stemInfo)

        //println "json is $json"
        println "tubl2CoocMap $tuple2CoocMap"


        def wordPairListK = tuple2CoocMap.keySet()
        println "wordpairlistk $wordPairListK"
      //  println "wordpairlist $wordPairList"

        def json = getJSONtree(tuple2CoocMap, stemInfo)
       // def json = getJSONgraph(wordPairList, stemInfo)
        //def json =  getJSONtree(wordPairListK, stemInfo)
        println "json $json"


        return json
    }

    private def internalNodes = [] as Set
    private def allNodes = [] as Set

    private String getJSONtree(Map tupleMap, Map stemMap) {
        def tree = [:]

        tupleMap.collect {tuple2 ->
            def word0 = stemMap[tuple2.getKey().get(0)].max { it.value }.key
            def word1 = stemMap[tuple2.getKey().get(1)].max { it.value }.key

            if (tree.isEmpty()) {
                tree <<
                        [name    : word0, cooc: tuple2.value,
                         children: [[name: word1]]]
                internalNodes.add(word0)
                allNodes.add(word0)
                allNodes.add(word1)
            } else {
                addPairToMap(tree, word0, word1, tuple2.value)
                addPairToMap(tree, word1, word0, tuple2.value)
            }
        }
        def json = new JsonBuilder(tree)
        return json
    }

    private void addPairToMap(Map m, String w0, String w1, def cooc) {

        assert w0 != w1

        m.each {

            if (it.value in List) {
                it.value.each {
                    assert it in Map
                    addPairToMap(it, w0, w1, cooc)
                }
            } else {

                if (it.value == w0 && allNodes.add(w1)) {

                    //the node has children.  Check the other word is not also an internal node
                    if (m.children && !internalNodes.contains(w1)) {

                        m.children << ["name": w1]

                    } else {

                        //do not create a new internal node if one already exists
                        if (internalNodes.add(it.value)) {
                            m << ["name": it.value, "cooc": cooc, "children": [["name": w1]]]
                        }
                    }
                }
            }
        }
    }


    private String getJSONgraph(List wl, Map stemMap) {

        def data = [

                links: wl.collect {

                    def src = stemMap[it.word0].max { it.value }.key
                    def tgt = stemMap[it.word1].max { it.value }.key

                    [source: src,
                     target: tgt,
                     cooc  : it.cooc,
                    ]
                }
        ]

        def json = new JsonBuilder(data)
        return json
    }

    private double getCooc(List w0Positions, List w1Positions) {
        final int MAX_DISTANCE = 10;
        double coocVal =
                [w0Positions, w1Positions].combinations().findAll{w1, w2 -> w1 != w2}.collect
                { a, b -> Math.abs(a - b) - 1 }
                        .findAll { it <= MAX_DISTANCE }
                        .sum {
                    //lookup table should be faster    //powers[it]
                    //Math.pow(0.5, it)
                    Math.pow(powerValue, it)
                } ?: 0.0;

        return coocVal ?: 0.0;
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

    static main(args) {
        def gwl = new GenerateWordLinksLucene()
        //y.getWordPairs("""houses tonight  houses tonight content contents contents housed house houses housed zoo zoo2""")

        gwl.getJSONnetwork()
//        def ali = gwl.getJSONnetwork(mAli)
//        def dd = gwl.getJSONnetwork("zzza ttttk ffffe")
//        println "dd $dd"
//        println "ali $ali"
    }

    def final static mAli =
            '''
I am America. I am the part you wonâ€™t recognise. But get used to me â€“ black, confident, cocky; my name, not yours; my religion, not yours; my goals, my own. Get used to me.â€�
Muhammad Ali: the man behind the icon Read moreMuhammad Ali loved the sound of his own voice, and so did everyone else. His words were predictably impossible to top on Saturday, as America mourned the loss of a colossus not only in the boxing ring but the arenas of politics, religion and popular culture.
Born in the south before Rosa Parks refused to give up her seat for a white bus passenger, he died at the age of 74, having seen the first African American elected to the White House. Barack Obama led tributes to the incandescent athlete, activist, humanitarian, poet and showman with a statement that caught the mood of many.
It said: â€œMuhammad Ali was the Greatest. Period. If you just asked him, heâ€™d tell you. Heâ€™d tell you he was the double greatest; that heâ€™d â€˜handcuffed lightning, thrown thunder into jailâ€™. But what made the Champ the greatest â€“ what truly separated him from everyone else â€“ is that everyone else would tell you pretty much the same thing.â€�
The president continued: â€œLike everyone else on the planet, Michelle and I mourn his passing. But weâ€™re also grateful to God for how fortunate we are to have known him, if just for a while; for how fortunate we all are that the Greatest chose to grace our time.â€�
Muhammad Ali shook up the world. And the world is better for it. We are all better for it
Barack Obama Obama said he kept a pair of Aliâ€™s gloves on display in his private study, under a celebrated photo of him, aged 23, â€œroaring like a lionâ€� over a fallen Sonny Liston. His name was â€œas familiar to the downtrodden in the slums of south-east Asia and the villages of Africa as it was to cheering crowds in Madison Square Gardenâ€�, the president said.
â€œMuhammad Ali shook up the world. And the world is better for it. We are all better for it.â€�
The three-time world heavyweight champion died late on Friday evening, a day after he was admitted to a Phoenix-area hospital with a respiratory ailment. His family were gathered around him.
Ali had long battled Parkinsonâ€™s disease, which impaired his speech and made the irrepressible athlete â€“ known for saying he could float like a butterfly and sting like a bee â€“ something of a prisoner in his own body.
On Saturday, family spokesman Bob Gunnell said Ali died from septic shock due to unspecified natural causes. He did not suffer, Gunnell said. A White House statement said Obama had called Lonnie Ali, the championâ€™s fourth wife, â€œto offer his familyâ€™s deepest condolences for the passing of her husbandâ€�.
View image on Twitter
'''
}