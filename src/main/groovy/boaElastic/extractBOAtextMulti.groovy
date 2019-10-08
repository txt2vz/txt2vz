package boaElastic

import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('test.json')

def jsonText = jsonSlurper.parseText(jfile.text)

def outFile = new File ('testMulti.txt')

outFile.write(jsonText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()

println jsonText.hits.hits.size()

jsonText.hits.hits.eachWithIndex{it, index->

    println it.inner_hits.pages.hits.hits._source.text.toString()

    def outFile2 = new File ('testMulti' + index + '.txt')

    outFile2.write(it.inner_hits.pages.hits.hits._source.text.toString())

    println "$index *********************************************************************************************************************************************************************************************"
}

