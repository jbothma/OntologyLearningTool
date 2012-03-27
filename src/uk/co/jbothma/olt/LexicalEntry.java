package uk.co.jbothma.olt;

import gate.util.Out;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;


public class LexicalEntry {
	// descriptions from https://svn.spraakdata.gu.se/sb-arkiv/pub/lexikon/description.xml
	private String id; // <saldo> element
	private String mf; // association
	private String pf; // sv:bestämmare en:determiner
	private String lem; // lemgram
	private String gf; // sv:grundformen en:lemma
	private String pos; // part of speech sv:ordklass
	private String p; // paradigm
	
	public LexicalEntry() {	}

	public String getLem() {
		return lem;
	}

	public void setLem(String lem) {
		this.lem = lem;
	}

	public String getGf() {
		return gf;
	}

	public void setGf(String gf) {
		this.gf = gf;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMf() {
		return mf;
	}

	public void setMf(String mf) {
		this.mf = mf;
	}

	public String getPf() {
		return pf;
	}

	public void setPf(String pf) {
		this.pf = pf;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}
}
