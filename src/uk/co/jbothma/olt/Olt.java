package uk.co.jbothma.olt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.BasicConfigurator;
import org.xml.sax.SAXException;

import uk.co.jbothma.saldo.Saldo;
import uk.co.jbothma.saldo.SaldoException;
import uk.co.jbothma.saldo.Word;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.persist.SerialDataStore;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;

/**
 * @author JD Bothma (jbothma@gmail.com)
 * 
 */
public class Olt {
	private static String dataStorePath = "/home/jdb/thesis/results/GATE_hunpos_201203222255";
	
	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public static void main(String[] args) {

		// log to console
		BasicConfigurator.configure();

		try {
			Gate.init();
		} catch (GateException gex) {
			Err.prln("cannot initialise GATE...");
			gex.printStackTrace();
			return;
		}

		try {
	    	System.out.println(new java.util.Date());
			// create&open a new Serial Data Store
			// pass the datastore class and path as parameteres
			SerialDataStore dataStore = (SerialDataStore) Factory
					.openDataStore("gate.persist.SerialDataStore", "file:///"
							+ dataStorePath);
			dataStore.open();
			Out.prln("serial datastore opened...");
			
			String saldoBinPath = "/home/jdb/uni/uppsala/2011-2012/thesis/sw_source/FM-SBLEX_svn/sblex/bin/saldo";
			String saldoDictPath = "/home/jdb/uni/uppsala/2011-2012/thesis/sw_source/FM-SBLEX_svn/dicts/saldo.dict";
			
			Saldo saldo = new Saldo(saldoBinPath, saldoDictPath);

			Out.prln("Documents:");
			for (Object lrId : dataStore.getLrIds("gate.corpora.DocumentImpl")) {

				Out.prln("  " + lrId);

				// params for getting an instance of the serialized LR
				FeatureMap parameters = Factory.newFeatureMap();
				// datastore it should use to load the document from
				parameters.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
				// persistent id of the document
				parameters.put(DataStore.LR_ID_FEATURE_NAME, lrId);

				Document doc = (Document) Factory
						.createResource("gate.corpora.DocumentImpl", parameters);

				AnnotationSet tokenAnnLayer = doc.getAnnotations("Token");
				AnnotationSet tokenAnnSet = tokenAnnLayer.get("Token");
				
				// Term Frequency Hash Map
				Map<Object, Integer> termFreqHM = new HashMap<Object, Integer>();
				
				for (Annotation ann : tokenAnnSet) {
					FeatureMap tokenFeatures = ann.getFeatures();
					String tokenCategory = (String) tokenFeatures.get("category");
					//Out.prln(tokenCategory.substring(0, 1));
					// is it a Parole noun?
					if (tokenCategory.startsWith("NC")) {
						String tokenString = (String) tokenFeatures.get("string");
						if (!tokenString.matches("[a-zA-ZöäåÖÄÅ]+"))
							continue;
						
						//Out.prln("lookup " + tokenString);
						String lemma = "";
						List<Word> saldoResults = saldo.getAnalysis(tokenString.toLowerCase());
						if ((lemma = chooseNounLemma(saldoResults, tokenCategory)).equals(""))
							lemma = "unknown";
						
						Out.prln("lemma " + tokenString + "  " + lemma + " " + saldoResults.size());
						
						// increment string's count
						if (termFreqHM.containsKey(tokenString)) {
							termFreqHM.put(tokenString, termFreqHM.get(tokenString) + 1);
							//Out.prln("Increment " + tokenString);
						} else {
							termFreqHM.put(tokenString, 1);
						}
						
//						if (tokenString.equals("saker"))
//							break;
					}
				}
				/*
				MapLTEValueComparator comparator = new MapLTEValueComparator(termFreqHM);
				TreeMap<Object, Integer> valueSortedTermFreq = new TreeMap<Object, Integer>(comparator);
				valueSortedTermFreq.putAll(termFreqHM);
				// Output term frequencies
				for (Entry<Object, Integer> termEntry : valueSortedTermFreq.entrySet()) {
					Out.prln("    " + termEntry.getValue() + "  " + termEntry.getKey());
				}*/
			}


	    	System.out.println(new java.util.Date());
			saldo.close();
			dataStore.close();
			dataStore = null;
			Out.prln("serial datastore closed.");
		} catch (GateException gex) {
			Err.prln("cannot open " + dataStorePath);
			gex.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SaldoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String chooseNounLemma(List<Word> saldoResults, String parolePos) {
		NounMorph wordMorph = new NounMorph(parolePos);
		for (Word result : saldoResults) {
			try {
				NounMorph resultMorph = new NounMorph(result.getMorph());
				if (wordMorph.equals(resultMorph))
					return result.getLemma();
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
		return "";
	}
	

}
