package processText

import groovy.transform.CompileStatic

@CompileStatic
class LinkBoost {

    static Map<Tuple2<String, String>, Double> linkBoost(Map<Tuple2<String, String>, Double> t2cocOrig, String boostWord = '~') {


        String boostWordStemmed = new PorterStemmer().stem(boostWord)
        println "linkboost kyeword: $boostWord boostWordStemmed $boostWordStemmed"

        Map<String, Integer> wordFrequencyCountMap = t2cocOrig.keySet().collectMany { t2 ->

            Tuple2<String, String> t2b = checkStringTuple(t2)
            [t2b.first, t2b.second]

        }.countBy {
            it
        }.sort { -it.value }.asImmutable()

        println "wordFrequencyCountMap: $wordFrequencyCountMap"
        println ""

        final double maxCooc = t2cocOrig.max {it.value}.value
        println "maxCooc: $maxCooc"

        Map<Tuple2<String, String>, Double> t2bFreq = t2cocOrig.collectEntries { k, v ->

            Tuple2<String, String> t2b = checkStringTuple(k)

            final int frst = (Integer) wordFrequencyCountMap[t2b.first]
            final int scnd = (Integer) wordFrequencyCountMap[t2b.second]
            final int total = frst + scnd - 1

            final int minCount = Math.min(frst, scnd)
            assert total > 0 && minCount > 0

            final double returnVal = (boostWordStemmed in [t2b.first, t2b.second]) ? Double.MAX_VALUE : v * total * minCount

            [(k): returnVal]

        } as Map<Tuple2<String, String>, Double>

        Map t2bFreqSorted = t2bFreq.sort { -it.value }

        println "LinkBoost t2bFreqSorted $t2bFreqSorted"
        println ""

        return t2bFreqSorted.asImmutable()
    }

    //when loading from JSON file we may get string instead of tuple2
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
