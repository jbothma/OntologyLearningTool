package uk.co.jbothma.olt;

import gate.util.Out;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;


public class LexicalEntry {
	private String gf;
	private String lem;
	
	public LexicalEntry() {
		//Out.prln("new lexical entry");
	}

	public String getLem() {
		return lem;
	}

	public void setLem(String lem) {
		this.lem = lem;
		//Out.prln("lem " + lem);
	}

	public String getGf() {
		return gf;
	}

	public void setGf(String gf) {
		this.gf = gf;
		//Out.prln("gf " + gf);
	}
}
