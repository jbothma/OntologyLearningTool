package uk.co.jbothma.relations;

import gate.Annotation;
import gate.AnnotationSet;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Utils;
import gate.creole.ANNIEConstants;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class RelCandidateExtractor {
	private static final String dataStorePath = "/home/jdb/thesis/results/jrc2006_korp_mid";
	private static final String outfilePath = "/home/jdb/thesis/results/Relations.txt";

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GateException 
	 */
	public static void main(String[] args) throws IOException, GateException {
		SerialDataStore dataStore;
		FeatureMap docFeatures;
		Iterator<Annotation> relationIter, subclassIter;
		AnnotationSet inputAS;
		String inputASName, relationASType;
		gate.Document doc;
		String npWordsPos, pos, word, phrase;
		Annotation relationAnnot;
		AnnotationSet tokAnnots;
		List<Annotation> tokAnnotList;
		FileWriter fstream = new FileWriter(outfilePath);
		BufferedWriter out = new BufferedWriter(fstream);

		Gate.init();
		
		// get the datastore
		dataStore = (SerialDataStore) Factory.openDataStore(
				"gate.persist.SerialDataStore", "file:///" + dataStorePath);
		dataStore.open();
		
		int totalDocs = dataStore.getLrIds("gate.corpora.DocumentImpl").size();
		int docIdx = 0;
		
		// get the corpus
		for (Object docID : dataStore.getLrIds("gate.corpora.DocumentImpl")) {
			docFeatures = Factory.newFeatureMap();
			docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
			docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
			
			//tell the factory to load the Serial Corpus with the specified ID from the specified  datastore
			doc = (gate.Document)
					Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
			

			docIdx++;
			System.out.println("Doc " + docIdx + " / " + totalDocs + " : " + doc.getName());
			
			inputASName = "Original markups";
			inputAS = doc.getAnnotations(inputASName);
			
			relationASType = "SubclassOf";
			relationIter = inputAS.get(relationASType).iterator();
			subclassIter = inputAS.get("Domain").iterator();
			
			while (relationIter.hasNext()) {
				phrase = "";
				relationAnnot = (Annotation) relationIter.next();
				String superclass = superclass(inputAS, relationAnnot);
				System.out.println(superclass + " såsom");
				//out.write(phrase + "\n");
			}
		}
		
		out.close();
	}
	
	private static String superclass(AnnotationSet inputAS, Annotation relation) {
		AnnotationSet superclassAS = inputAS.get("Range");
		AnnotationSet containedSuperclassAS = Utils.getContainedAnnotations(superclassAS, relation);
		return Utils.stringFor(inputAS.getDocument(), containedSuperclassAS);
	}
}
