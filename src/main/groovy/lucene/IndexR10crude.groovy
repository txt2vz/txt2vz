package lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.search.similarities.ClassicSimilarity
import org.apache.lucene.search.similarities.Similarity
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.store.RAMDirectory

import java.nio.file.Path
import java.nio.file.Paths

//import org.apache.lucene.queryparser.classic.QueryParser
class IndexR10crude {

    // Create Lucene index in this directory
//     Path indexPath = Paths.get('Indexes/R10CrudeL')
  //  Path docsPath = Paths.get('Datasets/R10Crude')

    Path indexPath = Paths.get('Indexes/R3cornCrudeShip')
    Path docsPath = Paths.get('C:\\Users\\aceslh\\Dataset\\reut3')

  //  Path indexPath = Paths.get('Indexes/20NG')
   // Path docsPath = Paths.get('C:\\Users\\aceslh\\Dataset\\20bydate')
    
    Directory directory = FSDirectory.open(indexPath)
    RAMDirectory ramDir = new RAMDirectory();
    Analyzer analyzer = //new EnglishAnalyzer();  //with stemming
            new StandardAnalyzer();
    def catsFreq = [:]

    static main(args) {
        def i = new IndexR10crude()
        i.buildIndex()
    }

    Directory buildIndex() {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        Similarity tfidf = new ClassicSimilarity()
        iwc.setSimilarity(tfidf)

        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);

     //   IndexWriter writer = new IndexWriter(directory, iwc);
        IndexWriter writer = new IndexWriter(ramDir, iwc);

        Date start = new Date();
        println("Indexing to directory $indexPath ...");

        //	println "docsPath $docsPath parent" + docsPath.getParent()

        FieldType ft = new FieldType();
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStored(true)
        ft.setTokenized(true);

        def x = 0
        int max = 500

        docsPath.toFile().eachFileRecurse { file ->
         //   if (x < max) {
            if (!file.isDirectory()){
                def doc = new Document()
                doc.add(new Field("contents", file.text, ft))
                writer.addDocument(doc);
            }
            x++
        }
        println "Total docs in index: ${writer.maxDoc()}"
        writer.close()
        println "done"

        return ramDir

      }
}
