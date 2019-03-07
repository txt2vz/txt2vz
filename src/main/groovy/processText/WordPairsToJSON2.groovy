package processText

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic

@CompileStatic
class WordPairsToJSON2 {

    Set<String> internalNodes = [] as Set
    Set<String> allNodes = [] as Set

     String getJSONtree(Set<Tuple2<String, String>> wordPairs) {

        Map tree = [:]

        wordPairs.collect { wordLink ->
            String word0 = wordLink.first
            String word1 = wordLink.second

            if (tree.isEmpty()) {
                tree = [name    : word0,
                        children: [[name: word1]]]

                internalNodes.add(word0)
                allNodes.add(word0)
                allNodes.add(word1)
            } else {
                addPairToMap(tree, word0, word1)
                addPairToMap(tree, word1, word0)
            }
        }
        println "tree $tree"

        def json = new JsonBuilder(tree)
        return json
    }

     private void addPairToMap(Map m, String w0, String w1) {

        assert w0 != w1

        m.each { mapElement ->

            //list means array of children - each child should be a map
            if (mapElement.value in List) {
                mapElement.value.each {childListElement ->
                    assert childListElement in Map
                    addPairToMap(childListElement as Map, w0, w1)  //should not need as Map?
                }
            } else {

                //if word0 is the map value and word1 is not already a node
                //could be mapElement.name?
                if (mapElement.value == w0 && allNodes.add(w1)) {

                    //the node has children.  Check the other word is not also an internal node
                    if (m.children && !internalNodes.contains(w1)) {

                        List mChildren = m.children as List
                        mChildren.add(new HashMap([name: w1]))

                    } else {

                        //do not create a new internal node if one already exists
                        //we could call this w0?
                        if (internalNodes.add(w0)) {
                            m << [name: w0, children: [[name: w1]]]
                        }
                    }
                }
            }
        }
    }
}
