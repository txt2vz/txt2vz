package lucene

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.MultiFields
import org.apache.lucene.index.PostingsEnum
import org.apache.lucene.index.Terms
import org.apache.lucene.index.TermsEnum
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.BytesRef


//http://stackoverflow.com/questions/35809035/how-to-get-positions-from-a-document-term-vector-in-lucene

StandardAnalyzer analyzer = new StandardAnalyzer();

FieldType ft = new FieldType();
ft.setIndexOptions( IndexOptions.DOCS_AND_FREQS_AND_POSITIONS );
ft.setStoreTermVectors( true );
ft.setStoreTermVectorPositions( true );
ft.setStored(true)
ft.setTokenized( true );

Directory indexDir = new RAMDirectory();
IndexWriterConfig config = new IndexWriterConfig( analyzer);
IndexWriter w = new IndexWriter(indexDir, config);

addDoc(w, "one fish two fish", ft);
addDoc(w, "red fish blue fish", ft);
addDoc(w, "black fish blue fish", ft);

w.close();

String querystr =  "blue";
Query q = new QueryParser( "contents", analyzer).parse(querystr);

int hitsPerPage = 10;
IndexReader reader = DirectoryReader.open(indexDir);
IndexSearcher searcher = new IndexSearcher(reader);
TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
searcher.search(q, collector);
ScoreDoc[] hits = collector.topDocs().scoreDocs;

println "Found " + hits.length + " hits."

Bits liveDocs = MultiFields.getLiveDocs(reader);
//for (int i=0; i<reader.maxDoc(); i++) {
def st = ["contents"] as Set

hits.each{
	
	int docNumber = it.doc;
	Document d = searcher.doc(docNumber);
	println "d " + d.get("contents")

//}

//reader.maxDoc().times{docNumber ->

	if (liveDocs == null || liveDocs.get(docNumber)) {

		println "docNumer $docNumber  **********************************"
		doc = reader.document(docNumber);

		//https://lucene.apache.org/core/6_2_0/core/index.html?org/apache/lucene/index/CheckIndex.Status.TermVectorStatus.html
		Terms tv = reader.getTermVector( docNumber, "contents" );
		TermsEnum terms = tv.iterator();
		PostingsEnum p = null;

		BytesRef br = terms.next();

		while(br != null ) {
			println ""
			println "Term:  ${br.utf8ToString()}"
			p = terms.postings( p, PostingsEnum.POSITIONS );

			while( p.nextDoc() != PostingsEnum.NO_MORE_DOCS ) {

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

def private addDoc(IndexWriter w, String text, FieldType ft) throws IOException {

	Document doc = new Document();
	Field f = new Field("contents", text, ft)
	doc.add(f);
	w.addDocument(doc);	
}