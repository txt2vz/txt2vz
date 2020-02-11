package opennlp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import opennlp.tools.util.Span

class OpenNLP {

    //public void givenEnglishModel_whenTokenize_thenTokensAreDetected()
    //       throws Exception {

    static void main (String[] args){

        InputStream inputStream = getClass()
                .getResourceAsStream("/models/en-token.bin");
        TokenizerModel model = new TokenizerModel(inputStream);
        TokenizerME tokenizer = new TokenizerME(model);
        String[] tokens = tokenizer.tokenize("Baeldung is a Spring Resource.");

        println "$tokens"

        def nlp = new OpenNLP()
        nlp.person()
        nlp.testTwoWordTokens()

        //  assertThat(tokens).contains(
        //         "Baeldung", "is", "a", "Spring", "Resource", ".");
        //https://www.baeldung.com/apache-open-nlp
        //https://www.tutorialspoint.com/opennlp/opennlp_named_entity_recognition.htm
    }

    void person(){

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer
                .tokenize("John Lennon is 26 years old. His best friend's "
                        + "name is Leonard. He has a sister named Penny Smith. George Harrison united nations is OPEC a Mary Anne Hobbes beatle");

        InputStream inputStreamNameFinder = getClass()
       //         .getResourceAsStream("/models/en-ner-person.bin");
        .getResourceAsStream("/models/en-ner-organization.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

        println "spans $spans"

        Span f = spans.first()
        println "f $f " + tokens[f.getStart()]

        spans.each{sp->
            println " sp $sp  " + tokens[sp.getStart()]
            def ff = tokens[sp.getStart()..sp.getEnd()-1]
            println "ff $ff"
            println ff.join(' ')

        }

    }

    void testTwoWordTokens(){
        String s = 'the only way united nations michael opec jordan president obama ringo starr'
        List<String> words = s.replaceAll(/\W/, ' ').toLowerCase().tokenize().findAll {
            it.size() > 1 && it.charAt(0).isLetter() //&& it.charAt(1).isLetter()
        }

        println "words $words"

        def words2 = words.join(',')
        println " words2 $words2"

        List <String> l = ['united,nations', 'ringo,starr']
//def l2 = l.collect{String pair ->
        l.each{String pair ->
            def p2 = pair.replace(',', ' ')
            words2= words2.replaceAll(pair, p2)
            return words2
        }

        println "l $l"
        println "words2 $words2"

        def l7 = words2.split(',')
        println "l7 $l7"

    }
}

