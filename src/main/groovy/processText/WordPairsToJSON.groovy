package processText

import groovy.json.JsonBuilder

class WordPairsToJSON {

    private def internalNodes = [] as Set
    private def allNodes = [] as Set

    String getJSON(LinkedHashMap tuple2CoocMap, LinkedHashMap stemInfo, int maxWordPairs, String networkType) {
        tuple2CoocMap = tuple2CoocMap.sort { -it.value }
        tuple2CoocMap = tuple2CoocMap.take(maxWordPairs)
        println "tuple2CoocMap take 5: " + tuple2CoocMap.take(5)

        def json = (networkType == 'forceNet') ? getJSONgraph(tuple2CoocMap, stemInfo) : getJSONtree(tuple2CoocMap, stemInfo)
        return json
    }

    private String getJSONgraph(Map wm, Map stemMap) {

        def data = [

                links: wm.collect {

                    def src = stemMap[it.key.first].max { it.value }.key
                    def tgt = stemMap[it.key.second].max { it.value }.key

                    [source: src,
                     target: tgt,
                     cooc  : it.value,
                    ]
                }
        ]

        def json = new JsonBuilder(data)
        return json
    }

    private String getJSONtree(Map wl, Map stemMap) {
        def tree = [:]

        wl.collect { wordLink ->
            def word0 = stemMap[wordLink.key.first].max { it.value }.key
            def word1 = stemMap[wordLink.key.second].max { it.value }.key

            if (tree.isEmpty()) {
                tree <<
                        [name    : word0, cooc: wordLink.value,
                         children: [[name: word1]]]
                internalNodes.add(word0)
                allNodes.add(word0)
                allNodes.add(word1)
            } else {
                addPairToMap(tree, word0, word1, wordLink.value)
                addPairToMap(tree, word1, word0, wordLink.value)
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
}
