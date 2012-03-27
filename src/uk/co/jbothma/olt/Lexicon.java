package uk.co.jbothma.olt;

import gate.util.Out;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

public class Lexicon {

	public static IndexWriter writer;
	
	public Lexicon() {
		
	}

	public static void addEntry(LexicalEntry entry) throws CorruptIndexException, IOException {
		//Out.prln("add lexical entry");
		Document contactDocument  = new Document();
		contactDocument.add(new Field("gf", entry.getGf(), Field.Store.YES, Field.Index.ANALYZED));
		contactDocument.add(new Field("lem", entry.getLem(), Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(contactDocument);
		//System.out.println("docs " + writer.numDocs());
	}

}
