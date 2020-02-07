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

        //  assertThat(tokens).contains(
        //         "Baeldung", "is", "a", "Spring", "Resource", ".");
        //https://www.baeldung.com/apache-open-nlp
    }

    void person(){

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer
                .tokenize("John Lennon is 26 years old. His best friend's "
                        + "name is Leonard. He has a sister named Penny Smith. George Harrison is a beatle");

        InputStream inputStreamNameFinder = getClass()
                .getResourceAsStream("/models/en-ner-person.bin");
        TokenNameFinderModel model = new TokenNameFinderModel(
                inputStreamNameFinder);
        NameFinderME nameFinderME = new NameFinderME(model);
        List<Span> spans = Arrays.asList(nameFinderME.find(tokens));

        println "spans $spans"

    }

}

