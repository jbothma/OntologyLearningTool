package uk.co.jbothma.olt;

import gate.util.Out;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.digester3.AbstractMethodRule;
import org.apache.commons.digester3.AbstractRulesImpl;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Saldo {
	private static String XMLPath = "/home/jdb/uni/uppsala/2011-2012/thesis/data/saldo/saldo.xml";
	private XPathFactory xpathFactory;  
	private XPath xpath;
	private InputSource source;
	private FileReader fileReader;
	private HashMap<String, String> wordLemma;
	private IndexReader reader;
	private IndexSearcher searcher;
	
	public Saldo() throws XPathExpressionException, IOException, SAXException {
		Logger.getRootLogger().removeAllAppenders();
		
		Digester digester = new Digester();

		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
		IndexWriter writer = new IndexWriter(index, config);
		
		Lexicon.writer = writer;

		digester.addObjectCreate("Lexicon", Lexicon.class);
		digester.addObjectCreate("Lexicon/LexicalEntry", LexicalEntry.class);
		digester.addCallMethod("Lexicon/LexicalEntry/gf", "setGf", 0);
		digester.addCallMethod("Lexicon/LexicalEntry/lem", "setLem", 0);
		digester.addSetNext("Lexicon/LexicalEntry", "addEntry");

		Out.println("About to parse");

		digester.parse("file://" + XMLPath);
		
		writer.close();
		
		Out.println("About Done parsing");
		
		reader = IndexReader.open(index);
		searcher = new IndexSearcher(reader);
        
	}
	
	public String getLemma(String word) throws XPathExpressionException, IOException {
		Query query = new TermQuery(new Term("gf", word));
        
        ScoreDoc[] hits = searcher.search(query, 100).scoreDocs;
        //System.out.println("NUMBER OF MATCHING CONTACTS: " + hits.length);
        for (int i = 0; i < hits.length; i++) {
        	Out.prln("  lemma " + searcher.doc(hits[i].doc).get("lem"));
        }
		return "";
	}
}