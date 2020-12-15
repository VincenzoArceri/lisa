package it.unive.lisa.test.acadia;

import static org.junit.Assert.fail;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.ParsingException;
import it.unive.lisa.test.imp.acadia.DivisionByZeroCheck;
import it.unive.lisa.test.imp.acadia.Interval;
import it.unive.lisa.test.imp.acadia.Parity;
import it.unive.lisa.test.imp.acadia.ParityXSign;
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
		
		for (CFG cfg : cfgs)
			lisa.addCFG(cfg);
		
		lisa.addSyntacticCheck(new DivisionByZeroCheck());
		lisa.setJsonOutput(true);
		lisa.setDumpCFGs(true);
		lisa.setWorkdir("phd-course/div-by-zero");
		try {
			lisa.run();
		} catch (AnalysisException e1) {
			System.err.println(e1);
			fail("Analysis termiated with errors");
		}
	}
}