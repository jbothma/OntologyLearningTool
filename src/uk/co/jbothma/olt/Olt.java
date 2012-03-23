package uk.co.jbothma.olt;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.pdfbox.pdfviewer.MapEntry;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageResource;
import gate.persist.SerialDataStore;
import gate.util.Err;
import gate.util.GateException;
import gate.util.Out;

/**
 * @author JD Bothma (jbothma@gmail.com)
 * 
 */
public class Olt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String dataStorePath = "/home/jdb/thesis/results/GATE_hunpos_201203222255";

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

			// create&open a new Serial Data Store
			// pass the datastore class and path as parameteres
			SerialDataStore dataStore = (SerialDataStore) Factory
					.openDataStore("gate.persist.SerialDataStore", "file:///"
							+ dataStorePath);
			dataStore.open();
			Out.prln("serial datastore opened...");

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
				Map<Object, Integer> termFreqHM = new HashMap();
				
				for (Annotation ann : tokenAnnSet) {
					FeatureMap tokenFeatures = ann.getFeatures();
					String tokenCategory = (String) tokenFeatures.get("category");
					//Out.prln(tokenCategory.substring(0, 1));
					// is it a Parole noun?
					if (tokenCategory.substring(0, 1).equals("N")) {
						String tokenString = (String) tokenFeatures.get("string");
						
						// increment string's count
						if (termFreqHM.containsKey(tokenString)) {
							termFreqHM.put(tokenString, termFreqHM.get(tokenString) + 1);
							//Out.prln("Increment " + tokenString);
						} else {
							termFreqHM.put(tokenString, 1);
						}
					}
				}
				
				MapLTEValueComparator comparator = new MapLTEValueComparator(termFreqHM);
				TreeMap<Object, Integer> valueSortedTermFreq = new TreeMap<Object, Integer>(comparator);
				valueSortedTermFreq.putAll(termFreqHM);
				// Output term frequencies
				for (Entry<Object, Integer> termEntry : valueSortedTermFreq.entrySet()) {
					Out.prln("    " + termEntry.getValue() + "  " + termEntry.getKey());
				}
			}

			// close data store
			dataStore.close();
			dataStore = null;
			Out.prln("serial datastore closed.");
		} catch (GateException gex) {
			Err.prln("cannot open " + dataStorePath);
			gex.printStackTrace();
			return;
		}
	}

}
