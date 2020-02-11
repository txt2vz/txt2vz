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

        def nlp = new OpenNLP_b()
     //   nlp.person()
           nlp.testTwoWordTokens()

        //https://www.baeldung.com/apache-open-nlp
        //https://www.tutorialspoint.com/opennlp/opennlp_named_entity_recognition.htm
    }

    void person() {


        File f =
         //       new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\secrecy10\ev590doc10908.txt/)
           //     new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\coffee10\0000402/)
     //  new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\test.txt/)
        new File(/C:\Users\aceslh\lngit\txt2vz\boaData\text\exp\ev592doc10962.txt/)

        List<String> words = f.text.replaceAll("[^a-zA-Z ]", "").tokenize().findAll {

            it.size() > 1 && it.charAt(0).isLetter() //&& !(it.toLowerCase() in StopSet.stopSet)  //&& it.charAt(1).isLetter()
        }
        println "words $words"
        println words.join(' ')

        String[] tokens = tokenizer.tokenize(f.text)

      //  println "tokens $tokens"
//                .tokenize("John Lennon is 26 years old. His best friend's "
//                        + "name is Leonard. He has a sister named Penny Smith. George Harrison United Nations is OPEC a Mary Anne Hobbes beatle");

        InputStream inputStreamNameFinder = getClass()
     //     .getResourceAsStream("/models/en-ner-person.bin");
     //   .getResourceAsStream("/models/en-ner-location.bin");
                .getResourceAsStream("/models/en-ner-organization.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

        println "spans $spans"

        //Span f = spans.first()
        //println "f $f " + tokens[f.getStart()]
        Map neMap = [:]

        spans.each { sp ->
            // println " sp $sp  " + tokens[sp.getStart()]
            List neList = tokens[sp.getStart()..sp.getEnd() - 1]
            println "FFogzn $neList "

            if (neList.size() in [2,3,4] ) {

               // println "neList $neList " + neList.join(' ') + " size " + neList.size()
                String ne = neList.join(' ')
                println "ne $ne " +  " size " + neList.size()
                println "neInspect " + neList.inspect()

                if (ne.charAt(0).isLetter()) {
                    final int n0 = neMap.get(ne) ?: 0
                    neMap.put(ne, n0 + 1)
                }
            }
        }

        println "spans $spans"
        println "neMap $neMap"

       def neMapSmall=  neMap.findAll{k, v ->
            v>1
        }
        println "neMapSmall size ${neMapSmall.size()}"
        println "neMapSmall $neMapSmall"
        def str = neMapSmall.inspect()

        nerMapFile.write(str)

    }

    void testTwoWordTokens() {
        String s = 'the only way united nations michael opec jordan president obama ringo starr White Russians'
//        List<String> words = s.replaceAll(/\W/, ' ').toLowerCase().tokenize().findAll {
//            it.size() > 1 && it.charAt(0).isLetter() //&& it.charAt(1).isLetter()
//        }
       // File nerMap  = new File('ner.txt')
        Map<String, Integer> nerMap = Eval.me(nerMapFile.text)
        List<String> nerWordList = nerMap.keySet() as List <String>

        println "nerMap $nerMap"
        println "nerWordList $nerWordList"
        String[] words = tokenizer.tokenize(s)
        println "words $words"

        def wordsWithComma = words.join(',')
        println " wordsWithComma:  $wordsWithComma"

     //   List<String> l = ['united,nations', 'ringo,starr']
//def l2 = l.collect{String pair ->
      //  l.each { String pair ->
        nerWordList.each { String ner ->

            def nerWithComma = ner.replace(' ', ',')
            println "ner $ner nerWithComma $nerWithComma"
            wordsWithComma = wordsWithComma.replaceAll(nerWithComma, ner)
          //  return wordsWithComma
        }

//        println "l $l"
        println "wordsWithComma $wordsWithComma"

       // def wordsReduced = wordsWithComma.replaceAll()

    //    List <String> l7 = wordsWithComma.replaceAll(/\W/, ' ').tokenize(',')
        List <String> l7 = wordsWithComma.tokenize(',')
        println "l7 $l7"

        def wordsNoStop = l7.minus(StopSet.stopSet)
        println "worsNoStop $wordsNoStop"

    }
}

