package boa

import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('test0.json')

def jsonText = jsonSlurper.parseText(jfile.text)

//def outFile = new File ('secrecy/eb2.txt')

//outFile.write(jsonText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

//println "File size: " + outFile.size()

println "hits.hits.size " +  jsonText.hits.hits.size()

jsonText.hits.hits.eachWithIndex{it, index ->

   // println "volud id " + it.fields.volume_ref

    def v = it.fields."volume.id"[0]
    def doc = it.inner_hits.pages.hits.hits._parent[0]
    println "v $v index $index"
    def outV = new File('secrecy/ev' + v + 'doc' + doc + '.txt')
    outV.write(it.inner_hits.pages.hits.hits._source.text.toString())



}



//jsonText.hits.hits.eachWithIndex{it, index->
//
//  //  println it.inner_hits.pages.hits.hits._source.text.toString()
//    println it.volume_id
//
//    def outFile2 = new File ('eb2' + index + '.txt')
//
//    outFile2.write(it.inner_hits.pages.hits.hits._source.text.toString())
//
//    println "$index *********************************************************************************************************************************************************************************************"
//}

