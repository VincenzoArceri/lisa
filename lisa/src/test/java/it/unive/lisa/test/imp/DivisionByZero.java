package it.unive.lisa.test.imp;

import java.util.Collection;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Statement;
import it.unive.lisa.checks.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.test.imp.expressions.IMPDiv;

public class DivisionByZero implements SyntacticCheck {

	
	public static void main(String[] args) throws ParsingException {
		LiSA lisa = new LiSA();
		
		Collection<CFG> cfgs = IMPFrontend.processFile("phd-course/program.imp");
		
		for (CFG cfg : cfgs)
			lisa.addCFG(cfg);
		
		lisa.addSyntacticCheck(new DivisionByZero());
		lisa.setDumpCFGs(true);
		lisa.setJsonOutput(true);
		lisa.setDumpAnalysis(true);
		lisa.setWorkdir("reports");
		
		try {
			lisa.run();
		} catch (AnalysisException e) {
			System.err.println("Analysis error!");
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void beforeExecution(CheckTool tool) {	}

	@Override
	public void visitCFGDescriptor(CheckTool tool, CFGDescriptor descriptor) {}

	@Override
	public void visitStatement(CheckTool tool, Statement statement) {

		// x = 1 + x / y
		if (statement instanceof Assignment) { 
			Assignment asg = (Assignment) statement;
				visitExpression(tool, asg.getLeft());

		}
	}

	@Override
	public void visitExpression(CheckTool tool, Expression expression) {
		if (expression instanceof IMPDiv) {
			tool.warnOn(expression, "Hey! Maybe there is a division by zero. Need to check!");
		}		
	}

	@Override
	public void afterExecution(CheckTool tool) {	}

}
