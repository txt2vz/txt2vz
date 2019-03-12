import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('labour1925.json')

def jText = jsonSlurper.parseText(jfile.text)

def outFile = new File ('boaJ.txt')

outFile.write(jText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()


