package processText

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import jdk.nashorn.internal.ir.annotations.Immutable

import java.util.concurrent.ConcurrentHashMap

@CompileStatic
class WordPairsToJSON {

    Set<String> internalNodes = []
    Set<String> allNodes = []

    String getJSONgraph(Map<Tuple2<String, String>, Double> wordPairCooc) {

        def data = [

                links: wordPairCooc.collect {

                    [source: it.key.first,
                     target: it.key.second,
                     cooc  : it.value,
                    ]
                }
        ]
        return new JsonBuilder(data)
    }

    String getJSONtree(Map<Tuple2<String, String>, Double> wordPairWithCooc) {

        Map tree = new ConcurrentHashMap()

        wordPairWithCooc.each { k, v ->
            String word0 = k.first
            String word1 = k.second
            double coocValue = v

            if (tree.isEmpty()) {
                List listOfChildren = new ArrayList<Map>().asSynchronized()
                Map childMapElements = new ConcurrentHashMap()
                childMapElements << [name: word1, cooc: coocValue]
                listOfChildren.add(childMapElements)

                tree << [name: word0, children: listOfChildren]//.asSynchronized()

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
