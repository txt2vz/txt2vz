package boa

import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('t11020.json')

def jsonText = jsonSlurper.parseText(jfile.text)

println "jsontext $jsonText"

def outFile = new File ('t11020.txt')

outFile.write(jsonText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()



