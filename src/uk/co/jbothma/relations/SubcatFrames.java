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
import gate.corpora.SerialCorpusImpl;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SubcatFrames {
	private static final String dataStorePath = "/home/jdb/thesis/results/jrc_2006_syntdep1";
	private static final String outfilePath = "/home/jdb/thesis/results/SubcatFrames.txt";

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GateException 
	 */
	public static void main(String[] args) throws IOException, GateException {
		SerialDataStore dataStore;
		Iterator<Annotation> sentIter;
		AnnotationSet inputAS, termAS;
		String inputASName, sentASType;
		gate.Document doc;
		Annotation sentAnnot;
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
		SerialCorpusImpl persistCorp = (SerialCorpusImpl)Factory.createResource("gate.corpora.SerialCorpusImpl", corpFeatures);
		
		printMem();
		
		// get the corpus
		for (int docIdx = 0; docIdx < persistCorp.size(); docIdx++) {
//			printMem();
			doc = persistCorp.get(docIdx);
//			printMem();
			System.out.println("Doc " + (docIdx+1) + " / " + persistCorp.size() + " : " + doc.getName());
			
			inputASName = "Original markups";
			inputAS = doc.getAnnotations(inputASName);

			sentASType = "sentence";
			sentIter = inputAS.get(sentASType).iterator();
			
			termAS = inputAS.get("TermCandidate");
			
			while (sentIter.hasNext()) {
				sentAnnot = (Annotation) sentIter.next();
				AnnotationSet wordAS = inputAS.get("w");
				AnnotationSet containedWordsAS = Utils.getContainedAnnotations(wordAS, sentAnnot);
				List<Annotation> wordAnnList = Utils.inDocumentOrder(containedWordsAS);
				Annotation rootAnn = null;
				for (Annotation ann : wordAnnList) {
					FeatureMap features = ann.getFeatures();
					if (features.get("deprel").equals("ROOT") && features.get("dephead").equals("")) {
						rootAnn = ann;
					}
					if (!features.get("dephead").equals("")){
						try {
							int depHeadRef = Integer.parseInt((String)features.get("dephead"));
							String depRel = (String)features.get("deprel");
							Annotation depHead = wordAnnList.get(depHeadRef-1);
							addBranch(depHead, ann);
						} catch(NumberFormatException e) {}
					}
				}
				if (rootAnn != null) {
					if (rootAnn.getFeatures().get("pos").equals("VB")) {
						out.write(Utils.stringFor(doc, sentAnnot) + "\n");
						out.write(Utils.stringFor(doc, rootAnn) + "\n");
						Set<Annotation> branches = (Set<Annotation>)rootAnn.getFeatures().get("branches");
						String subjStr = null;
						String objStr = null;
						String vgStr = Utils.stringFor(doc, rootAnn);
						if (branches != null) {
							for (Annotation branch : branches) {
								String depRel = (String)branch.getFeatures().get("deprel");
								out.write("  <--" + depRel + "-- " + Utils.stringFor(doc, branch));
								if (depRel.equals("SS")) {
									subjStr = getContainingTermString(doc, termAS, branch);
								}
								if (depRel.equals("OO")) {
									objStr = getContainingTermString(doc, termAS, branch);
								}
								if (depRel.equals("VG")) {
									vgStr += " " + Utils.stringFor(doc, branch);
								}
								out.newLine();
							}
						}
						if (subjStr != null || objStr != null) {
							out.write("REL: ("+subjStr+")  " + vgStr + "  ("+objStr+")\n");
						}
					}
				}
			}
			
			persistCorp.unloadDocument(doc, false);
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

	private static void addBranch(Annotation trunk, Annotation branch) {
		Set<Annotation> branches = (Set<Annotation>) trunk.getFeatures().get("branches");
		if (branches == null) {
			branches = new HashSet<Annotation>();
			trunk.getFeatures().put("branches", branches);
		}
		branches.add(branch);
	}

	public static String getContainingTermString(Document doc, AnnotationSet termAS, Annotation branch) {
		AnnotationSet containingTermAS = Utils.getContainedAnnotations(termAS, branch);
		if (containingTermAS.size() > 0) {
			return Utils.stringFor(doc, containingTermAS);
		}
		return null;
	}
}
