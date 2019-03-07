package processText

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.transform.Synchronized
import jdk.nashorn.internal.ir.annotations.Immutable

import java.util.concurrent.ConcurrentHashMap

@CompileStatic
class WordPairsToJSON {

    Set<String> internalNodes = [] as Set
    Set<String> allNodes = [] as Set

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

        Map tree = [:]

        wordPairWithCooc.collect { wordLink ->
            String word0 = wordLink.key.first
            String word1 = wordLink.key.second
            double coocValue = wordLink.value

            if (tree.isEmpty()) {
                tree = [name    : word0,
                        children: [[name: word1, cooc:coocValue]]]

                internalNodes.add(word0)
                allNodes.add(word0)
                allNodes.add(word1)
            } else {
                addPairToMap(tree, word0, word1, coocValue)
                addPairToMap(tree, word1, word0, coocValue)
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

                        //   m.children << [name: w1]

                        // m.children << [coocXX:cooc]
                        List mChildren = m.children as List
                       Map m2 = new ConcurrentHashMap()
                          m2  << [name:w1, cooc:cooc]// .addname: w1])//, cooc:cooc]  //concurrent error - use tramploine?
                        mChildren.add(m2)
                        //..def leaf = [name: w1]
                        //  Map m2 = [name:w1, coocBBB:cooc]
                        // m.children << m2
                        // m2.cooc = cooc
                        //mChildren.add(m2)//name:w1)//    [[name:w1, cooc:cooc]])//leaf)
                        //mChildren.add([cooc:cooc])

                    } else {

                        //do not create a new internal node if one already exists
                        if (internalNodes.add(it.value.toString())) {
                            m << [name: it.value,  children: [[name: w1]]]
                        }
                    }
                }
            }
        }
    }
}
