package classRunner;
import java.io.PrintStream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

/**
 * DiagnosticListener that will provide output for CompilationTasks
 * @author Stephen Bussey
 *
 */
public class MyDiagnosticListener implements DiagnosticListener<JavaFileObject>
{
	PrintStream out;
	public MyDiagnosticListener(PrintStream out)
	{
		this.out = out;
	}
	public void report(Diagnostic<? extends JavaFileObject> diag)
	{
		out.println(diag.toString());
	}

}
