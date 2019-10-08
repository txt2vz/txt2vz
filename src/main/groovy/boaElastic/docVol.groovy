package boaElastic

import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
def jfile =  new File('docVol.json')
def jsonText = jsonSlurper.parseText(jfile.text)

def outCSV = new File ('secrecy/docVolb.csv')

println "hits.hits.size " +  jsonText.hits.hits.size()
outCSV.write "index, voldID, VolRef, DocID, DocRef, Title \n"

jsonText.hits.hits.eachWithIndex{it, index ->

    def vid = it.fields."volume.id"[0]
    def vref = it.fields.volume_ref[0]
    def doc = it._id
    def docRef = it.fields.document_ref[0]
    def title = it.fields."volume.title"[0]

    println "index $index volID $vid volRef $vref doc $doc  docRef $docRef title $title"

    outCSV.append "$index , $vid , $vref , $doc , $docRef, $title \n"
}
