package it.unive.lisa.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.heap.ClassBasedHeap;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.ParsingException;
import it.unive.lisa.test.imp.acadia.ConstantPropagation;
import it.unive.lisa.test.imp.acadia.DivisionByZeroCheck;
import it.unive.lisa.test.imp.acadia.Interval;
import it.unive.lisa.test.imp.acadia.Parity;
import it.unive.lisa.test.imp.acadia.Pentagon;
import it.unive.lisa.test.imp.acadia.Sign;
import it.unive.lisa.test.imp.acadia.UpperBoundsEnvironment;

public class Main {
	
	public static void main(String[] args) throws IOException, ParsingException{
		LiSA lisa = new LiSA();
		Collection<CFG> cfgs = IMPFrontend.processFile("phd-course/program.imp");
		
		for (CFG cfg : cfgs)
			lisa.addCFG(cfg);
	
		lisa.addValueDomain(new UpperBoundsEnvironment());
		lisa.setInferTypes(false);
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir("phd-course-reports/div-by-zero");
		lisa.setJsonOutput(true);

		try {
			lisa.run();
		} catch (AnalysisException e) {
			e.printStackTrace();
			fail("Something bad happened during the analysis :(");
		}
	}
}
