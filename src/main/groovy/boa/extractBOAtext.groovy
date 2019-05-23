package boa

import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

def jfile =  new File('OfficersDiary3210.json')

def jsonText = jsonSlurper.parseText(jfile.text)

def outFile = new File ('officersDiary3210.txt')

outFile.write(jsonText.hits.hits.inner_hits.pages.hits.hits._source.text.toString())

println "File size: " + outFile.size()

println


