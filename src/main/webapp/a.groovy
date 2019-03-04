import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()
//def object = jsonSlurper.parseText('{ "name": "John Doe" } /* some comment */')

def jfile =  new File('labour1925.json')

def te = jsonSlurper.parseText(jfile.text)

println "te $te"
println""

println te.hits.hits.inner_hits.pages.hits.hits._source.text


