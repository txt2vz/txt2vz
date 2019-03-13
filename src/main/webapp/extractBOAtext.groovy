import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('Ranella4363.json')

def jText = jsonSlurper.parseText(jfile.text)

def outFile = new File ('ranella4363.txt')

outFile.write(jText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()


