package opennlp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.util.Span
import processText.StopSet

class NER {

    enum NERModel {
        PERSON('/models/en-ner-person.bin'),
        ORGANIZATION('/models/en-ner-organization.bin'),
        LOCATION('/models/en-ner-location.bin')

        String path
        NERModel(String p){
            path = p
        }
    }

    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    File nerMapFile = new File('ner.txt')
    Map<String, Integer> neMap = [:]

    static void main(String[] args) {

        File f =
                //       new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\secrecy10\ev590doc10908.txt/)
                //     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\coffee10\0000402/)
                     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\test.txt/)
                //    new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\exp\ev592doc10962.txt/)
             //    new File(/boaData\text\exp\ev592doc10962.txt/)
           //     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\single\ev599doc11102.txt/)

        NERModel nm = NERModel.ORGANIZATION
        NER ner = new NER()
        ner.generateNER(f.text, nm.path)
        ner.tokenizeWithNE(f.text)

        //https://www.baeldung.com/apache-open-nlp
        //https://www.tutorialspoint.com/opennlp/opennlp_named_entity_recognition.htm
    }


    void generateNER(String documentText, String modelPath) {

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

            List neCase = neList.collect {

                if (isAllUpper(it)) {
                    it
                } else
                {
                    it.substring(0, 1).toUpperCase() + it.substring(1).toLowerCase()
                }
            }
         //   println "neCase $neCase"

            if (neCase.size() < 5) {

                String ne = neCase.join(' ')

                if (ne.charAt(0).isLetter()) {

                    final int n0 = neMap.get(ne) ?: 0
                    neMap.put(ne, n0 + 1)
                }
            }
        }

        println "neMap $neMap"

        Map<String, Integer> neMapSmall = neMap.findAll { k, v ->
            v > 2
        }
        println "neMapSmall size ${neMapSmall.size()}"
        println "neMapSmall $neMapSmall"
        String str = neMapSmall.inspect()

        nerMapFile.write(str)
    }

    List<String> tokenizeWithNE(String s) {

        Map<String, Integer> nerMap = Eval.me(nerMapFile.text)
        List<String> nerWordList = nerMap.keySet() as List<String>

        println "nerMap $nerMap"
        println "nerWordList $nerWordList"
        String[] words = tokenizer.tokenize(s)

        println "first 40 words ${words.take(40)}"

        String wordsAsString = words.join(',').toLowerCase()
      //  wordsAsString = wordsAsString.substring(0, wordsAsString.length()-1)
        println "wordsasString $wordsAsString"

        nerWordList.each { String ner ->
            def nerWithComma = ner.replace(' ', ',').toLowerCase()

            wordsAsString = wordsAsString.replaceAll(nerWithComma +',', ner + ',')
        }

        List<String> wordsNoStop = wordsAsString.tokenize(',').findAll { w ->
            w.size() > 2 && w.charAt(0).isLetter() && !StopSet.stopSet.contains(w.toLowerCase())
        }
        println "worsNoStop $wordsNoStop"
        return wordsNoStop
    }

    boolean isAllUpper(String str) {
        boolean isUpper = true
        for (char c : str) {
            if (!c.isUpperCase()) {
                isUpper = false
            }
            return isUpper
        }
    }
}

