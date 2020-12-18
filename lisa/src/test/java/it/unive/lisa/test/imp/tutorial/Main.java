package it.unive.lisa.test.imp.tutorial;

import java.util.Collection;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.nonrelational.ValueEnvironment;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.ParsingException;

public class Main {

	public static void main(String[] args) throws ParsingException {
		LiSA lisa = new LiSA();
		
		Collection<CFG> cfgs = IMPFrontend.processFile("phd-course/program.imp");
		
		for (CFG cfg : cfgs)
			lisa.addCFG(cfg);
		
		lisa.addNonRelationalValueDomain(new Sign());
		lisa.setWorkdir("reports/sign");
		lisa.setDumpAnalysis(true);
		
		try {
			lisa.run();
		} catch (AnalysisException e) {
			e.printStackTrace();
		}
	}
}
