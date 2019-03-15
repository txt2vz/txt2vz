package lucene

import org.apache.lucene.document.FieldType
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexReader

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TotalHitCountCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory

enum IndexName {
    R10, NG20, OHS
}

class BuildIndex {

    static final String FIELD_CATEGORY_NAME = 'category',
                        FIELD_CONTENTS = 'contents',
                        FIELD_PATH = 'path',
                        FIELD_TEST_TRAIN = 'test_train',
                        FIELD_CATEGORY_NUMBER = 'categoryNumber';

    static main(args) {
        new BuildIndex()
    }

    BuildIndex() {

        IndexName iName = IndexName.R10

        final String ohsIndexPath = 'indexes/Ohsc06MuscC08RespC11Eye'
        final String ohsDocsPath =/C:\Users\aceslh\Dataset\Ohsc06MuscC08RespC11Eye/

        final String r10DocsPath = 'C:\\Users\\aceslh\\Dataset\\reut3'
           //     /C:\Users\Laurie\Dataset\R8/
        // /C:\Users\Laurie\Dataset\reuters-top10/

        final String NG20DocsPath =
                /C:\Users\Laurie\Dataset\20NG5WindowsMotorcyclesSpaceMedMideast/
        //        /C:\Users\Laurie\Dataset\20NG5WindowsmiscForsaleHockeySpaceChristian/
        //   /C:\Users\Laurie\Dataset\20NG3SpaceHockeyChristian/
        //   /C:\Users\Laurie\Dataset\20bydate/
        final String r10IndexPath = 'indexes/R3'//'indexes/R10'
        //     final String NG20IndexPath = 'indexes/NG20SpaceHockeyChristianV7'
        final String NG20IndexPath =
                //'indexes/20NG'
                //        'indexes/20NG5WindowsForsaleSpaceHockeyChristian'
                //        'indexes/20NG3SpaceHockeyChristian'
                'indexes/20NG5WindowsMotorcyclesSpaceMedMideast'

        String docsPath, indexPath

        if (iName == IndexName.R10) {
            docsPath = r10DocsPath
            indexPath = r10IndexPath
        } else if (iName == IndexName.NG20) {
            docsPath = NG20DocsPath
            indexPath = NG20IndexPath
        }
        else if (iName==IndexName.OHS){
            docsPath = ohsDocsPath
            indexPath = ohsIndexPath
        }

//Note: R10 - different directory structure
        Path path = Paths.get(indexPath)
        Directory directory = FSDirectory.open(path)
        Analyzer analyzer = //new EnglishAnalyzer();  //with stemming
                new StandardAnalyzer()
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer)

//store doc counts for each category
        def catsFreq = [:]

// Create a new index in the directory, removing any
// previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE)

        FieldType ft = new FieldType();
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStored(true)
        ft.setTokenized(true);


        IndexWriter writer = new IndexWriter(directory, iwc)
        //  IndexSearcher indexSearcher = new IndexSearcher(writer.getReader())
        Date start = new Date();
        println("Indexing to directory: $indexPath  from: $docsPath ...")

        def categoryNumber = -1
        new File(docsPath).eachDir {
            if (iName == IndexName.R10) categoryNumber++
            else categoryNumber = -1  //reset for 20NG for test and train directories

            it.eachFileRecurse { file ->
                if (iName != IndexName.R10 && file.isDirectory()) categoryNumber++
                if (!file.hidden && file.exists() && file.canRead() && !file.isDirectory()) // && categoryNumber <3)

                {
                    def doc = new Document()

                    Field catNumberField = new StringField(FIELD_CATEGORY_NUMBER, String.valueOf(categoryNumber), Field.Store.YES);
                    doc.add(catNumberField)

                    Field pathField = new StringField(FIELD_PATH, file.getPath(), Field.Store.YES);
                    doc.add(pathField);

                    String parent = file.getParent()
                    String grandParent = file.getParentFile().getParent()

                    def catName
                    //reuters dataset has different directory structure

                    if (iName == IndexName.R10)
                        catName = grandParent.substring(grandParent.lastIndexOf(File.separator) + 1, grandParent.length())
                    else
                        catName = parent.substring(parent.lastIndexOf(File.separator) + 1, parent.length())

                    Field catNameField = new StringField(FIELD_CATEGORY_NAME, catName, Field.Store.YES);
                    doc.add(catNameField)

                    String test_train
                    if (file.canonicalPath.contains("test")) test_train = "test" else test_train = "train"
                    //   println "cannonicla ptath is" + file.canonicalPath
                    //    println "test train $test_train"
                    //   println ""
                    Field ttField = new StringField(FIELD_TEST_TRAIN, test_train, Field.Store.YES)
                    doc.add(ttField)

                    doc.add(new Field(FIELD_CONTENTS, file.text, ft))

                    def n = catsFreq.get((catName)) ?: 0
                    catsFreq.put((catName), n + 1)
                    writer.addDocument(doc)
                }
            }
        }
        println "Total docs: " + writer.maxDoc()
        writer.close()
        IndexReader indexReader = DirectoryReader.open(directory)
        IndexSearcher indexSearcher = new IndexSearcher(indexReader)
        TotalHitCountCollector trainCollector = new TotalHitCountCollector();
        final TermQuery trainQ = new TermQuery(new Term(FIELD_TEST_TRAIN, "train"))

        TotalHitCountCollector testCollector = new TotalHitCountCollector();
        final TermQuery testQ = new TermQuery(new Term(FIELD_TEST_TRAIN, "test"))

        indexSearcher.search(trainQ, trainCollector);
        def trainTotal = trainCollector.getTotalHits();

        indexSearcher.search(testQ, testCollector);
        def testTotal = testCollector.getTotalHits();

        Date end = new Date();
        println(end.getTime() - start.getTime() + " total milliseconds");
        println "testTotal $testTotal trainTotal $trainTotal"
        println "catsFreq $catsFreq"

        println "End ***************************************************************"
    }
}