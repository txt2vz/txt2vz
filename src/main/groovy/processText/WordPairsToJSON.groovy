package processText

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic

@CompileStatic
class WordPairsToJSON {

    private Set<String> internalNodes = [] as Set
    private Set<String> allNodes = [] as Set

    @CompileStatic
     String getJSONgraph( Tuple2< Map<Tuple2<String,String>,Double>, Map<String,Map<String, Integer>>>   stemT2) {

        Map<String,Map<String, Integer>> stemInfo = stemT2.second
         Map <Tuple2<String,String>, Double> wm = stemT2.first
        def data = [

                links: wm.collect {

                    def src = stemInfo[it.key.first].max { it.value }.key
                    def tgt = stemInfo[it.key.second].max { it.value }.key

                    [source: src,
                     target: tgt,
                     cooc  : it.value,
                    ]
                }
        ]
        return new JsonBuilder(data)
    }

    @CompileStatic
    String getJSONtree(Tuple2< Map<Tuple2<String,String>,Double>, Map<String,Map<String, Integer>>>   stemT2   ) {
       Map<String,Map<String, Integer>> stemInfo = stemT2.second
       Map <Tuple2<String,String>, Double> wordPairWithCooc = stemT2.first
       Map tree = [:]

        wordPairWithCooc.collect { wordLink ->
            String word0 = stemInfo[wordLink.key.first].max { it.value }.key
            String word1 = stemInfo[wordLink.key.second].max { it.value }.key

            if (tree.isEmpty()) {
                tree =  [name    : word0, cooc: wordLink.value,
                         children: [[name: word1]]]
                internalNodes.add(word0)
                allNodes.add(word0)
                allNodes.add(word1)
            } else {
                addPairToMap(tree, word0, word1, wordLink.value)
                addPairToMap(tree, word1, word0, wordLink.value)
            }
        }
       println "tree $tree"

        def json = new JsonBuilder(tree)
        return json
    }

    private void addPairToMap(Map m, String w0, String w1, double cooc) {

        assert w0 != w1

        m.each {

            if (it.value in List) {
                it.value.each {
                    assert it in Map
                    addPairToMap(it as Map, w0, w1, cooc)
                }
            } else {

                if (it.value == w0 && allNodes.add(w1)) {

                    //the node has children.  Check the other word is not also an internal node
                    if (m.children && !internalNodes.contains(w1)) {

                        List mChildren = m.children as List
                        Map leafMap = ["name": w1]
                        mChildren.add(leafMap)

                    } else {

                        //do not create a new internal node if one already exists
                        if (internalNodes.add(it.value.toString())) {
                            m << ["name": it.value, "cooc": cooc, "children": [["name": w1]]]
                        }
                    }
                }
            }
        }
    }
}
