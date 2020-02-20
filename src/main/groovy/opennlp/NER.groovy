package opennlp

import groovy.transform.CompileStatic
import groovyjarjarantlr.StringUtils
import jdk.nashorn.internal.ir.annotations.Immutable
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.util.Span
import processText.StopSet

@CompileStatic
class NER {

    enum NERModel {
        LOCATION('/models/en-ner-location.bin'),
        PERSON('/models/en-ner-person.bin'),
        ORGANIZATION('/models/en-ner-organization.bin')

        String path

        NERModel(String p) {
            path = p
        }
    }

    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    File nerMapFile = new File('ner.txt')


    static void main(String[] args) {

        File f = // new File(/D:\boa\test.txt/)
                //       new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\secrecy10\ev590doc10908.txt/)
                //     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\coffee10\0000402/)
                new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\test.txt/)
        //    new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\exp\ev592doc10962.txt/)
        //    new File(/boaData\text\exp\ev592doc10962.txt/)
        //     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\single\ev599doc11102.txt/)
        NER ner = new NER()
        ner.generateNERforAllModels(f.text)
        ner.tokenizeWithNE(f.text)

        //https://www.baeldung.com/apache-open-nlp
        //https://www.tutorialspoint.com/opennlp/opennlp_named_entity_recognition.htm
    }


    void generateNERforAllModels(String fileText) {
        Map neAll = [:]

        for (NERModel model : NERModel.values()) {
            //NERModel nm = NERModel.ORGANIZATION

            neAll << generateNERforModel(fileText, model)
            println "neAll Map (take 20): ${neAll.take(20)}"
        }
        nerMapFile.write(neAll.inspect())
    }

    Map<String, Integer> generateNERforModel(String documentText, NERModel modelEnum) {//String modelPath) {
        Map<String, Integer> neMap = [:]

        String[] tokens = tokenizer.tokenize(documentText)
        InputStream inputStreamNameFinder = getClass().getResourceAsStream(modelEnum.path)
        TokenNameFinderModel model = new TokenNameFinderModel(inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens))

        for (Span sp : spans) {
            List neList = tokens[sp.getStart()..sp.getEnd() - 1].findAll {
                it.charAt(0).isLetter() && it.length() > 1
            }

            List neCase = neList.collect {

                if (isAllUpper(it)) {
                    it
                } else {
                    it.substring(0, 1).toUpperCase() + it.substring(1).toLowerCase()
                }
            }

            if (neCase.size() < 5 && !neCase.isEmpty()) {

                String ne = neCase.join(' ')

                assert ne.charAt(0).isLetter()
                assert ne.size() > 1

                final int n0 = neMap.get(ne) ?: 0
                neMap.put(ne, n0 + 1)
            }
        }

        println "model $modelEnum  neMap:  $neMap "

        Map<String, Integer> neMapSmall = neMap.findAll { k, v ->
            v > 1
        }
        return neMapSmall.asImmutable()
    }

    List<String> tokenizeWithNE(String s) {

        Map<String, Integer> nerMap = Eval.me(nerMapFile.text) as Map<String, Integer>
        List<String> nerWordList = nerMap.keySet() as List<String>

        println "nerMap $nerMap"
        println "nerWordList $nerWordList"
        String[] documentTokens = tokenizer.tokenize(s)

        List<String> filteredTokensLowerCase = documentTokens.findResults { tok ->
            tok.charAt(0).isLetter() && tok.size() > 1 ? tok.toLowerCase() : null
        } as List<String>

        String documentAsCommaSeparatedString = ',' + filteredTokensLowerCase.join(',')

        for (String ner : nerWordList) {
            String nerWithCommaLowerCase = ner.replace(' ', ',').toLowerCase()

            //check full match by locating comma at start and end
            documentAsCommaSeparatedString = documentAsCommaSeparatedString.replaceAll(',' + nerWithCommaLowerCase + ',', ',' + ner + ',')
        }

        //remove extra comma from start and end
        documentAsCommaSeparatedString = documentAsCommaSeparatedString.endsWith(',') ? documentAsCommaSeparatedString.substring(0, documentAsCommaSeparatedString.length() - 1) : documentAsCommaSeparatedString
        documentAsCommaSeparatedString = documentAsCommaSeparatedString.substring(1)

        List<String> wordsNoStop = documentAsCommaSeparatedString.tokenize(',').findAll { w ->
            !StopSet.stopSet.contains(w.toLowerCase())
        }
        return wordsNoStop.asImmutable()
    }

    boolean isAllUpper(String str) {
        boolean isUpper = true
        for (char c : str.toCharArray()) {
            if (!c.isUpperCase() && c.isLetter()) {
                isUpper = false
            }
        }
        return isUpper
    }
}

