package boaElastic

import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('tenDocs.json')

def jsonText = jsonSlurper.parseText(jfile.text)

def outFile = new File ('testMulti10.txt')

outFile.write(jsonText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()

println jsonText.hits.hits.size()

jsonText.hits.hits.eachWithIndex{it, index->

    //println it.inner_hits.pages.hits.hits._source.text.toString()
    def docNumber = it.inner_hits.pages.hits.hits._parent
    println "docNumber $docNumber index: $index"

    def outFile2 = new File ('testMulti' + index + '.txt')

    outFile2.append(it.inner_hits.pages.hits.hits._source.text.toString())
    outFile2.append('\n  *****************************************  \n')

    println "$index *********************************************************************************************************************************************************************************************"
}

