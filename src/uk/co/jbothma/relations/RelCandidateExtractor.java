package uk.co.jbothma.relations;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RelCandidateExtractor {
	private static final String dataStorePath = "/home/jdb/thesis/results/jrc2006_korp_big";
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
		Annotation relationAnnot;
		FileWriter fstream = new FileWriter(outfilePath);
		BufferedWriter out = new BufferedWriter(fstream, 20);
		
		printMem();
		Gate.init();
		printMem();
		
		// get the datastore
		dataStore = (SerialDataStore) Factory.openDataStore(
				"gate.persist.SerialDataStore", "file:///" + dataStorePath);
		dataStore.open();
		
		printMem();
		
		FeatureMap corpFeatures = Factory.newFeatureMap();
		corpFeatures.put(DataStore.LR_ID_FEATURE_NAME, dataStore.getLrIds("gate.corpora.SerialCorpusImpl").get(0));
		corpFeatures.put(DataStore.DATASTORE_FEATURE_NAME, dataStore);
		Corpus persistCorp = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", corpFeatures);
		
		printMem();
		
		// get the corpus
		for (int docIdx = 0; docIdx < persistCorp.size(); docIdx++) {
			printMem();
			doc = persistCorp.get(docIdx);
			printMem();
			System.out.println("Doc " + (docIdx+1) + " / " + persistCorp.size() + " : " + doc.getName());
			
			inputASName = "Original markups";
			inputAS = doc.getAnnotations(inputASName);
			
			relationASType = "SubclassOf";
			relationIter = inputAS.get(relationASType).iterator();
			subclassIter = inputAS.get("Domain").iterator();
			
			while (relationIter.hasNext()) {
				relationAnnot = (Annotation) relationIter.next();
				String superclass = superclass(inputAS, relationAnnot);
				Set<String> subclasses = subclasses(inputAS, relationAnnot);
				for (String subclass : subclasses) {
					System.out.println(superclass + " <--- " + subclass);
					out.write(superclass + " <--- " + subclass + "\n");
				}
			}
			
			persistCorp.unloadDocument(doc);
			Factory.deleteResource(doc);
		}
		out.close();
	}
	
	private static void printMem() {
		Runtime runtime = Runtime.getRuntime();
		int mb = 1024*1024;
		System.out.println("Used Memory:"
	            + (runtime.totalMemory() - runtime.freeMemory()) / mb
	            + " / " + runtime.maxMemory() / mb);
	}

	private static String superclass(AnnotationSet inputAS, Annotation relation) {
		AnnotationSet superclassAS = inputAS.get("Range");
		AnnotationSet containedSuperclassAS = Utils.getContainedAnnotations(superclassAS, relation);
		return Utils.stringFor(inputAS.getDocument(), containedSuperclassAS);
	}

	private static Set<String> subclasses(AnnotationSet inputAS, Annotation relation) {
		AnnotationSet subclassAS = inputAS.get("Domain");
		AnnotationSet termAS = inputAS.get("TermCandidate");
		AnnotationSet containedSubclassPartAS = Utils.getContainedAnnotations(subclassAS, relation);
		AnnotationSet subclassTermsAS = Utils.getContainedAnnotations(termAS, containedSubclassPartAS);
		Set<String> subclassStrings = new HashSet<String>();
		Iterator<Annotation> iter = subclassTermsAS.iterator();
		while (iter.hasNext()) {
			subclassStrings.add(Utils.stringFor(inputAS.getDocument(), iter.next()));
		}
		return subclassStrings;
	}
}
