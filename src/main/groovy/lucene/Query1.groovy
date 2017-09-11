package lucene

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.MultiFields
import org.apache.lucene.index.PostingsEnum
import org.apache.lucene.index.Terms
import org.apache.lucene.index.TermsEnum
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.BytesRef

import java.nio.file.Path
import java.nio.file.Paths

class Query1 {

    static main (args){
       def q1 =  new Query1()
        q1.q()
    }

    void q () {
        println "h** ***************************************************************************"

        StandardAnalyzer analyzer = new StandardAnalyzer();
       // String querystr = "*:*";
        Query q =  new MatchAllDocsQuery()
                //new QueryParser("contents", analyzer).parse(querystr);

        int hitsPerPage = 10;

        Path indexPath = Paths.get('indexes/QueensLandFloods')
        Directory directory = FSDirectory.open(indexPath)

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        println "Found " + hits.length + " hits."

        Bits liveDocs = MultiFields.getLiveDocs(reader);
//for (int i=0; i<reader.maxDoc(); i++) {
//def st = ["contents"] as Set

        hits.each {

            int docNumber = it.doc;
            Document d = searcher.doc(docNumber);
            println ""
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
            println "document contents docNumber $docNumber " + d.get("contents")
//}

//reader.maxDoc().times{docNumber ->

            if (liveDocs == null || liveDocs.get(docNumber)) {

                println "docNumer $docNumber  **********************************"
                def doc = reader.document(docNumber);

                //https://lucene.apache.org/core/6_2_0/core/index.html?org/apache/lucene/index/CheckIndex.Status.TermVectorStatus.html
                Terms tv = reader.getTermVector(docNumber, "contents");
                //   if (tv.is(org.apache.lucene.index.TermsEnum)) {
                TermsEnum terms = tv.iterator();
                PostingsEnum p = null;

                BytesRef br = terms.next();

                while (br != null) {
                    println ""
                    println "Term:  ${br.utf8ToString()}"
                    p = terms.postings(p, PostingsEnum.POSITIONS);

                    while (p.nextDoc() != PostingsEnum.NO_MORE_DOCS) {

                        int freq = p.freq();
                                println "freq: $freq"

                        freq.times {
                            int pos = p.nextPosition();
                            println "Occurence $it :  position $pos"
                        }
                    }
                    br = terms.next();
                }
            }
        }
        reader.close();
    }

}