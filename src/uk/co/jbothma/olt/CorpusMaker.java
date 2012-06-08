package uk.co.jbothma.olt;

import gate.Corpus;
import gate.Factory;
import gate.Gate;
import gate.persist.SerialDataStore;
import gate.util.ExtensionFileFilter;
import gate.util.GateException;

import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CorpusMaker {

	/**
	 * @param args: datastoreDir populateFromDir fileExtention
	 * @throws UnsupportedOperationException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws GateException 
	 */
	public static void main(String[] args)
			throws UnsupportedOperationException, 
				MalformedURLException, IOException, GateException {
		String dsDir = "file://" + args[0];
		String populateDir = "file://" + args[1];
		String extensionFilter = args[2];
		SerialDataStore sds;
		Corpus corp, persistCorp;
		FileFilter filter;
		
		Gate.init();
		sds = (SerialDataStore)
				Factory.createDataStore("gate.persist.SerialDataStore", dsDir);
		corp = Factory.newCorpus("test corpus");
		filter = new ExtensionFileFilter("XML files", extensionFilter);
		corp.populate(new URL(populateDir), filter, "utf-8", true);
		persistCorp = null;
		persistCorp = (Corpus)sds.adopt(corp,null);
		sds.sync(persistCorp);
		sds.close();
	}

}
