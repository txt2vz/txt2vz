package opennlp

import groovyjarjarantlr.StringUtils
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.util.Span
import processText.StopSet

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
        NERModel.each { model ->
            //NERModel nm = NERModel.ORGANIZATION

            neAll << generateNERforModel(fileText, model.path)
            println "model $model"
            println "neAll $neAll"
        }
        nerMapFile.write(neAll.inspect())
    }


    Map<String, Integer> generateNERforModel(String documentText, String modelPath) {
        Map<String, Integer> neMap = [:]

        String[] tokens = tokenizer.tokenize(documentText)

        InputStream inputStreamNameFinder = getClass().getResourceAsStream(modelPath)
        //  .getResourceAsStream("/models/en-ner-person.bin");
        //   .getResourceAsStream("/models/en-ner-location.bin");
        //      .getResourceAsStream("/models/en-ner-organization.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens))

        spans.each { sp ->
            List neList = tokens[sp.getStart()..sp.getEnd() - 1]

            List neList1 = neList.findAll {
                it.charAt(0).isLetter() && it.length() > 1
            }

            List neCase = neList1.collect {

                if (isAllUpper(it)) {
                    it
                } else {
                    it.substring(0, 1).toUpperCase() + it.substring(1).toLowerCase()
                }
            }


            println "neCase $neCase"

            if (neCase.size() < 5 && !neCase.isEmpty()) {

                String ne = neCase.join(' ')

                assert ne.charAt(0).isLetter()
                assert ne.size() > 1
                //  if (ne.charAt(0).isLetter() && ne.size() > 1) {

                final int n0 = neMap.get(ne) ?: 0
                neMap.put(ne, n0 + 1)
                //}
            }
        }

        println " neMap  $neMap "

        Map<String, Integer> neMapSmall = neMap.findAll { k, v ->
            v > 2
        }
        println "neMapSmall size ${neMapSmall.size()}"
        println "neMapSmall $neMapSmall"
        String str = neMapSmall.inspect()

        //   nerMapFile.write(str)
        return neMap
    }

    List<String> tokenizeWithNE(String s) {

        Map<String, Integer> nerMap = Eval.me(nerMapFile.text)
        List<String> nerWordList = nerMap.keySet() as List<String>

        println "nerMap $nerMap"
        println "nerWordList $nerWordList"
        String[] documentTokens = tokenizer.tokenize(s)

        def doc2 = documentTokens.findResults {tok->
            tok.charAt(0).isLetter() && tok.size() > 1 ? tok.toLowerCase() : null
        }

        println "first 40 documentTokens ${doc2.take(40)}"

        String documentAsCommaSeparatedString = ',' + doc2.join(',').toLowerCase()
        //  documentAsCommaSeparatedString = documentAsCommaSeparatedString.substring(0, documentAsCommaSeparatedString.length()-1)
        println "docuemtnAsCommaSeparatedString: $documentAsCommaSeparatedString"

        nerWordList.each { String ner ->
            def nerWithComma = ner.replace(' ', ',').toLowerCase()

            documentAsCommaSeparatedString = documentAsCommaSeparatedString.replaceAll(',' + nerWithComma + ',', ',' + ner + ',')
        }

        documentAsCommaSeparatedString = documentAsCommaSeparatedString.endsWith(',') ? documentAsCommaSeparatedString.substring(0, documentAsCommaSeparatedString.length() - 1) : documentAsCommaSeparatedString
        documentAsCommaSeparatedString =  documentAsCommaSeparatedString.substring(1)

        List<String> wordsNoStop = documentAsCommaSeparatedString.tokenize(',').findAll { w ->
            w.size() > 2 && w.charAt(0).isLetter() && !StopSet.stopSet.contains(w.toLowerCase())
        }
        println "worsNoStop $wordsNoStop"
        return wordsNoStop
    }

    boolean isAllUpper(String str) {
        boolean isUpper = true
        for (char c : str) {
            if (!c.isUpperCase() && c.isLetter()) {
                isUpper = false
            }
        }
        return isUpper
    }
}

