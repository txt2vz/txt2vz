import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('Rinella4363first13Pages.json')

def jText = jsonSlurper.parseText(jfile.text)

def outFile = new File ('rinella4363first13Pages.txt')

outFile.write(jText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()


