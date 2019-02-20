package processText

import groovy.json.JsonSlurper
import processText.GenerateWordLinks
import spock.lang.*

class WordLinksSpec extends spock.lang.Specification {

	def "word links produced correctly short "() {
		given:
		def gwl = new GenerateWordLinks()		
		JsonSlurper slurper = new JsonSlurper()

		when:
		def jsonText = gwl.getJSONnetwork("one1 two2 three3")
		def json = slurper.parseText(jsonText)

		then:
		jsonText == '''{"name":"one1","cooc":1.0,"children":[{"name":"two2","cooc":1.0,"children":[{"name":"three3"}]}]}'''
		json.name == "one1"
		json.children[0].name == "two2"
	}

	def "Muhammad Ali text wordlinks test"(){
		given:
		def  mAli =
				'''
I am America. I am the part you won’t recognise. But get used to me – black, confident, cocky; my name, not yours; my religion, not yours; my goals, my own. Get used to me.”
Muhammad Ali: the man behind the icon Read moreMuhammad Ali loved the sound of his own voice, and so did everyone else. His words were predictably impossible to top on Saturday, as America mourned the loss of a colossus not only in the boxing ring but the arenas of politics, religion and popular culture.
Born in the south before Rosa Parks refused to give up her seat for a white bus passenger, he died at the age of 74, having seen the first African American elected to the White House. Barack Obama led tributes to the incandescent athlete, activist, humanitarian, poet and showman with a statement that caught the mood of many.
It said: “Muhammad Ali was the Greatest. Period. If you just asked him, he’d tell you. He’d tell you he was the double greatest; that he’d ‘handcuffed lightning, thrown thunder into jail’. But what made the Champ the greatest – what truly separated him from everyone else – is that everyone else would tell you pretty much the same thing.”
The president continued: “Like everyone else on the planet, Michelle and I mourn his passing. But we’re also grateful to God for how fortunate we are to have known him, if just for a while; for how fortunate we all are that the Greatest chose to grace our time.”
Muhammad Ali shook up the world. And the world is better for it. We are all better for it
Barack Obama Obama said he kept a pair of Ali’s gloves on display in his private study, under a celebrated photo of him, aged 23, “roaring like a lion” over a fallen Sonny Liston. His name was “as familiar to the downtrodden in the slums of south-east Asia and the villages of Africa as it was to cheering crowds in Madison Square Garden”, the president said.
“Muhammad Ali shook up the world. And the world is better for it. We are all better for it.”
The three-time world heavyweight champion died late on Friday evening, a day after he was admitted to a Phoenix-area hospital with a respiratory ailment. His family were gathered around him.
Ali had long battled Parkinson’s disease, which impaired his speech and made the irrepressible athlete – known for saying he could float like a butterfly and sting like a bee – something of a prisoner in his own body.
On Saturday, family spokesman Bob Gunnell said Ali died from septic shock due to unspecified natural causes. He did not suffer, Gunnell said. A White House statement said Obama had called Lonnie Ali, the champion’s fourth wife, “to offer his family’s deepest condolences for the passing of her husband”.
View image on Twitter
'''
		def gwl = new GenerateWordLinks()
		JsonSlurper slurper = new JsonSlurper()

		when:
		def jsonText = gwl.getJSONnetwork(mAli)
		def json = slurper.parseText(jsonText)

		then:	
		json.name == "ali"
		json.children[0].name == "muhammad"
	}
}