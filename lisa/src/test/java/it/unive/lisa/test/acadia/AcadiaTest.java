package it.unive.lisa.test.acadia;

import static org.junit.Assert.fail;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.ParsingException;
import it.unive.lisa.test.imp.acadia.Interval;
import it.unive.lisa.test.imp.acadia.Parity;
import it.unive.lisa.test.imp.acadia.Sign;
import it.unive.lisa.test.imp.acadia.UpperBoundsEnvironment;

import java.io.IOException;
import java.util.Collection;
import org.junit.Test;

public class AcadiaTest {
	
	@Test
	public void testSign() throws IOException, ParsingException {
		LiSA lisa = new LiSA();

		Collection<CFG> cfgs = IMPFrontend.processFile("imp-testcases/sign/program.imp");
		cfgs.forEach(lisa::addCFG);
//		lisa.setInferTypes(false);
//		lisa.setDumpTypeInference(false);
		lisa.addNonRelationalValueDomain(new Interval());
//		lisa.addValueDomain(new UpperBoundsEnvironment());
		lisa.setDumpAnalysis(true);
		lisa.setJsonOutput(false);
		lisa.setWorkdir("test-outputs/sign");
		
		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
	}
}