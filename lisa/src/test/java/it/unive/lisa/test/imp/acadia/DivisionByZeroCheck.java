package it.unive.lisa.test.imp.acadia;

import it.unive.lisa.cfg.CFGDescriptor;
import it.unive.lisa.cfg.statement.Assignment;
import it.unive.lisa.cfg.statement.Call;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.Return;
import it.unive.lisa.cfg.statement.Statement;
import it.unive.lisa.checks.CheckTool;
import it.unive.lisa.checks.syntactic.SyntacticCheck;
import it.unive.lisa.test.imp.expressions.IMPDiv;
import it.unive.lisa.test.imp.expressions.IMPFloatLiteral;
import it.unive.lisa.test.imp.expressions.IMPIntLiteral;

public class DivisionByZeroCheck implements SyntacticCheck {

	@Override
	public void beforeExecution(CheckTool tool) {
	}

	@Override
	public void visitCFGDescriptor(CheckTool tool, CFGDescriptor descriptor) {
	}

	@Override
	public void visitStatement(CheckTool tool, Statement statement) {
		if (statement instanceof Assignment) {
			Assignment asg = (Assignment) statement;
			visitExpression(tool, asg.getRight());
		}

		if (statement instanceof Return) {
			Return ret = (Return) statement;
			visitExpression(tool, ret.getExpression());
		}
	}

	@Override
	public void visitExpression(CheckTool tool, Expression expression) {
		if (expression instanceof IMPDiv) {
			IMPDiv division = (IMPDiv) expression;
			Expression right = division.getParameters()[1];

			if ((right instanceof IMPIntLiteral && (int) ((IMPIntLiteral) right).getValue() == 0)
					|| (right instanceof IMPFloatLiteral && (float) ((IMPIntLiteral) right).getValue() == 0.0))
				tool.warnOn(division, "[definite] Found division by zero!");
			else
				tool.warnOn(expression, "[possible] Maybe division by zero!");
		}

		if (expression instanceof Call) {
			Call call = (Call) expression;
			for (Expression exp : call.getParameters())
				visitExpression(tool, exp);
		}
	}

	@Override
	public void afterExecution(CheckTool tool) {
	}

}
