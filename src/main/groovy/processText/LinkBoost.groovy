package processText

import groovy.transform.CompileStatic

@CompileStatic
class LinkBoost {


    static Map<Tuple2<String, String>, Double> linkBoost(Map<Tuple2<String, String>, Double> t2cocOrig, String keyWord = '~') {
        println "t2coocOrig.size " + t2cocOrig.size()
        println "kyeword: $keyWord"


        Map<String, Integer> wordFrequencyCountMap = t2cocOrig.keySet().collectMany { t2 ->

            Tuple2<String, String> t2b = checkStringTuple(t2)
            [t2b.first, t2b.second]

        }.countBy {
            it
        }.sort { -it.value }.asImmutable()

        println "wordFrequencyCountMap: $wordFrequencyCountMap"
        println ""

        Map<Tuple2<String, String>, Double> t2bFreq = t2cocOrig.collectEntries { k, v ->

            Tuple2<String, String> t2b = checkStringTuple(k)

            final int frst = (Integer) wordFrequencyCountMap[t2b.first] ?: 0
            final int scnd = (Integer) wordFrequencyCountMap[t2b.second] ?: 0
            final int total = frst + scnd - 1

            final int minCount = Math.min(frst, scnd)
            assert total > 0 && minCount > 0

            int keyWordBoost = 1
            if (keyWord != '~' && keyWord in [t2b.first, t2b.second])
                keyWordBoost = 10

            [(k): v * keyWordBoost * total * minCount]         //[(k): v * total]
        } as Map<Tuple2<String, String>, Double>

        Map t2bFreqSorted = t2bFreq.sort { -it.value }

        println "t2bFreqSorted $t2bFreqSorted"
        println ""

        return t2bFreqSorted.asImmutable()
    }

    static Tuple2<String, String> checkStringTuple(def k) {

        String wrd0, wrd1

        if (k instanceof String) {
            List<String> wordPair = k.replaceAll(/\[|]|,/, '').tokenize()
            wrd0 = wordPair[0]
            wrd1 = wordPair[1]
        } else if (k instanceof Tuple2) {
            wrd0 = k.first
            wrd1 = k.second
        }
        return new Tuple2(wrd0, wrd1)
    }
}
