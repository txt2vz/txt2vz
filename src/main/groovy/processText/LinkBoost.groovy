package processText

class LinkBoost {

//    private Map<Tuple2<String, String>, Double> tuple2CoocMap
//
//    LinkBoost(Map<Tuple2<String, String>, Double> t2cocOrig){
//        tuple2CoocMap = t2cocOrig
//    }

   static Map<Tuple2<String, String>, Double> linkBoost (Map<Tuple2<String, String>, Double> t2cocOrig, String keyWord = '~') {
        println "t2coocOrig.size " + t2cocOrig.size()

//get frequency of each word in word pair list
        Map<String, Integer> wordFrequencyCountMap = t2cocOrig.keySet().collectMany {t2-> [t2.first, t2.second] }.countBy {
            it
        }.sort { -it.value }.asImmutable()

        println "wordFrequencyCountMap: $wordFrequencyCountMap"
        println ""

        Map <Tuple2<String, String>, Double> t2bFreq = t2cocOrig.collectEntries { k, v ->

            final int frst = (Integer) wordFrequencyCountMap[k.first] ?: 0
            final int scnd = (Integer) wordFrequencyCountMap[k.second] ?: 0
            final int total = frst + scnd - 1

            final int minCount = Math.min(frst,scnd)
            assert total > 0  && minCount > 0

            def keyWordBoost = 1
            if (keyWord != '~' && (k.first == 'parti' || k.second == 'parti'))
                keyWordBoost=10

            [(k): v * keyWordBoost * total * minCount ]         //[(k): v * total]
        } as Map<Tuple2<String, String>, Double>

        Map t2bFreqSorted = t2bFreq.sort {-it.value }

        println "t2bFreqSorted $t2bFreqSorted"
        println ""

        return  t2bFreqSorted.asImmutable()
    }



}
