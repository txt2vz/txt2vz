package NER

import groovy.transform.CompileStatic
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

        Map<String, Integer> neMap = Eval.me(nerMapFile.text) as Map<String, Integer>
        List<String> neList = neMap.sort {a, b -> b.value <=> a.value}.keySet() as List<String>

        println "neMap $neMap"
        println "neList $neList"

          List<String> filteredTokensLowerCase = tokenizer.tokenize(s).findResults { tok ->
              tok.charAt(0).isLetterOrDigit() ? tok.toLowerCase() : null
          } as List<String>

        String documentAsCommaSeparatedString = ',' + filteredTokensLowerCase.join(',')

        for (String ne : neList) {
            String neWithCommasLowerCase = ',' + ne.replace(' ', ',').toLowerCase() + ','
            String neWithCommas = ',' + ne + ','

            documentAsCommaSeparatedString = documentAsCommaSeparatedString.replaceAll( neWithCommasLowerCase , neWithCommas)
        }

        //remove extra comma from start and end
        documentAsCommaSeparatedString = documentAsCommaSeparatedString.endsWith(',') ? documentAsCommaSeparatedString.substring(0, documentAsCommaSeparatedString.length() - 1) : documentAsCommaSeparatedString
        documentAsCommaSeparatedString = documentAsCommaSeparatedString.substring(1)

        List<String> documentAsTokenListNoStopWords = documentAsCommaSeparatedString.tokenize(',').findAll { w ->
            !StopSet.stopSet.contains(w.toLowerCase()) && w.size() > 1
        }

        return documentAsTokenListNoStopWords.asImmutable()
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

