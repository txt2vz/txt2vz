package opennlp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.util.Span
import processText.StopSet

class OpenNLP_b {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    File nerMapFile = new File('ner.txt')

    static void main(String[] args) {

        File f =
                //       new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\secrecy10\ev590doc10908.txt/)
                //     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\coffee10\0000402/)
                new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\test.txt/)
        //    new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\exp\ev592doc10962.txt/)
        // new File(/boaData\text\exp\ev592doc10962.txt/)

        def nlp = new OpenNLP_b()
        nlp.generateNER(f.text)
        nlp.tokenizeWithNE(f.text)

        //https://www.baeldung.com/apache-open-nlp
        //https://www.tutorialspoint.com/opennlp/opennlp_named_entity_recognition.htm
    }


    void generateNER(String s) {


//        List<String> words = s.replaceAll("[^a-zA-Z ]", "").tokenize().findAll {
//
//            it.size() > 1 && it.charAt(0).isLetter()
//            //&& !(it.toLowerCase() in StopSet.stopSet)  //&& it.charAt(1).isLetter()
//        }
//        println "words $words"
//        println words.join(' ')

        String[] tokens = tokenizer.tokenize(s)

        InputStream inputStreamNameFinder = getClass()
        //     .getResourceAsStream("/models/en-ner-person.bin");
        //   .getResourceAsStream("/models/en-ner-location.bin");
                .getResourceAsStream("/models/en-ner-organization.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

      //  println "spans $spans"

        Map neMap = [:]

        spans.each { sp ->
            List neList = tokens[sp.getStart()..sp.getEnd() - 1]

            if (neList.size() in [2, 3, 4]) {

                String ne = neList.join(' ')
       //         println "ne $ne " + " size " + neList.size()
         //       println "neInspect " + neList.inspect()

                if (ne.charAt(0).isLetter()) {
                    final int n0 = neMap.get(ne) ?: 0
                    neMap.put(ne, n0 + 1)
                }
            }
        }

   //     println "spans $spans"
        println "neMap $neMap"

        def neMapSmall = neMap.findAll { k, v ->
            v > 1
        }
        println "neMapSmall size ${neMapSmall.size()}"
        println "neMapSmall $neMapSmall"
        def str = neMapSmall.inspect()

        nerMapFile.write(str)
    }

    List<String> tokenizeWithNE(String s) {

        Map<String, Integer> nerMap = Eval.me(nerMapFile.text)
        List<String> nerWordList = nerMap.keySet() as List<String>

        println "nerMap $nerMap"
        println "nerWordList $nerWordList"
        String[] words = tokenizer.tokenize(s)

        println "first 40 words ${words.take(40)}"

        String wordsAsStringWithComma = words.join(',').toLowerCase()
       // println "wordswithComma $wordsAsStringWithComma"

        nerWordList.each { String ner ->

            def nerWithComma = ner.replace(' ', ',').toLowerCase()
            wordsAsStringWithComma = wordsAsStringWithComma.replaceAll(nerWithComma, ner)
        }

        List<String> wordsNoStop = wordsAsStringWithComma.tokenize(',').minus(StopSet.stopSet).findAll { w ->
            w.size() > 2 && w.charAt(0).isLetter()
        }
        println "worsNoStop $wordsNoStop"
        return wordsNoStop

    }
}

