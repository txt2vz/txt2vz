package processText

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import jdk.nashorn.internal.ir.annotations.Immutable

import java.util.concurrent.ConcurrentHashMap

@CompileStatic
class WordPairsToJSON {

    private Set<String> internalNodes = []
    private Set<String> allNodes = []
    private Map<String, Map<String, Integer>> stemInfo = [:]

    WordPairsToJSON(Map<String, Map<String, Integer>> stInf){
        stemInfo = stInf
    }

    String getJSONgraph(Map<Tuple2<String, String>, Double> wordPairCooc) {

        def data = [

                links: wordPairCooc.collect {k, v ->

                    [
                    //        source: it.key.first,
                    //        target: it.key.second,
                     source: stemInfo[k.first].max { v }.key,
                     target: stemInfo[k.second].max { v }.key,
                     cooc  : v
                    ]
                }
        ]

        println "data $data"
        return new JsonBuilder(data)
    }

    String getJSONtree(Map<Tuple2<String, String>, Double> wordPairWithCooc) {

        Map tree = new ConcurrentHashMap()

        wordPairWithCooc.each { k, v ->
            String word0
            String word1

            if (stemInfo.isEmpty()){
                word0 = k.first
                word1 = k.second
            } else {
                word0 = stemInfo[k.first].max { it.value }.key
                word1 = stemInfo[k.second].max { it.value }.key
            }
            double coocValue = v

            if (tree.isEmpty()) {
                List listOfChildren = new ArrayList<Map>().asSynchronized()
                Map childMapElements = new ConcurrentHashMap()
                childMapElements << [name: word1, cooc: coocValue]
                listOfChildren.add(childMapElements)

                tree << [name: word0, cooc:coocValue, children: listOfChildren]//.asSynchronized()

                internalNodes.add(word0)
                allNodes.add(word0)
                allNodes.add(word1)
            } else {
                addPairToMap(tree, word0, word1, coocValue)
                addPairToMap(tree, word1, word0, coocValue)
            }
        }
        println "tree: $tree"

        def json = new JsonBuilder(tree)
        return json
    }

    private void addPairToMap(Map m, String w0, String w1, double cooc) {

        assert w0 != w1

        m.each { k, v ->

            //list is array of children - each child should be a map
            if (v in List) {
                v.each {listElement ->
                    assert listElement in Map
                    addPairToMap(listElement as Map, w0, w1, cooc)
                }
            } else {

                //if word0 is the map value and word1 is not already a node
                if (v == w0 && allNodes.add(w1)) {

                    //the node has children.  Check the other word is not also an internal node
                    //do not add if both words are internal nodes to avoid network rather than tree
                    if (m.children && !internalNodes.contains(w1)) {

                        List childrenList = m.children as List
                        childrenList.asSynchronized()

                        Map w0children = new ConcurrentHashMap()
                        w0children << [name: w1, cooc: cooc]
                        childrenList.add(w0children)

                    } else {

                        //do not create a new internal node if one already exists
                        if (internalNodes.add(w0.toString())) {
                            Map w1Map = new ConcurrentHashMap()
                            w1Map << [name: w1, cooc: cooc]
                            m << [name: w0, children: [w1Map]]
                        }
                    }
                }
            }
        }
    }
}
