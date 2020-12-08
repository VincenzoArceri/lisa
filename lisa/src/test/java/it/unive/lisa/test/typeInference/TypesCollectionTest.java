package it.unive.lisa.test.typeInference;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.outputs.JsonReport;
import it.unive.lisa.outputs.compare.JsonReportComparer;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.acadia.ClassMonolithicHeap;
import it.unive.lisa.test.imp.acadia.Interval;
import it.unive.lisa.test.imp.acadia.Sign;

public class TypesCollectionTest {
	@Test
	public void testTypesCollection() throws IOException {
		System.out.println("Testing types collection...");
		LiSA lisa = new LiSA();

		Collection<CFG> cfgs = IMPFrontend.processFile("imp-testcases/sign/heap.imp");
		cfgs.forEach(lisa::addCFG);
//		lisa.setInferTypes(false);
//		lisa.setDumpTypeInference(false);
		lisa.addNonRelationalValueDomain(new Sign());
		//lisa.addHeapDomain(new ClassMonolithicHeap());
		lisa.setDumpAnalysis(true);
//		lisa.setJsonOutput(true);
		lisa.setWorkdir("test-outputs/sign");
		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println(e);
			fail("Analysis terminated with errors");
		}
		
//		File expFile = new File("imp-testcases/type-inference/report.json");
//		File actFile = new File("test-outputs/type-inference/report.json");
//		JsonReport expected = JsonReport.read(new FileReader(expFile));
//		JsonReport actual = JsonReport.read(new FileReader(actFile));
//
//		assertTrue("Results are different", JsonReportComparer.compare(expected, actual, expFile.getParentFile(), actFile.getParentFile()));
	}
}
