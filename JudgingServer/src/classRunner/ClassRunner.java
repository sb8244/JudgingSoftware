package classRunner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import packet.results.*;
import server.Server;

/**
 * Given the name and location of a class, compile and execute the class
 * @author Stephen Bussey
 *
 */
public class ClassRunner 
{
	private String className, location;
	private Class<?> mainClass;
	private String compilationErrors = "";
	private String outputString = "", expectedOutput = "", inputString = "";

	/**
	 * Create a new ClassRunner
	 * @param name The name of the target
	 * @param location The location of the target
	 */
	public ClassRunner(String name, String location)
	{
		className = name;
		this.location = location;
	}
	
	/**
	 * 
	 * @return The location of the target
	 */
	public String getLocation()
	{
		return this.location;
	}

	/**
	 * 
	 * @return The output that was produced by running the code on the input data
	 */
	public String getOutput()
	{
		return outputString;
	}
	
	/**
	 * Compares the output and expected output on 3 levels:
	 * 	Match - Remove all newlines and compare
	 * 	OutputFormatError - Remove all spaces, punctuation 
	 * 	Wrong Answer - Both of these failed
	 * @return
	 */
	public Result compareOutput()
	{
		outputString = outputString.replaceAll("[\\r\\n\\f]", "");
		expectedOutput = expectedOutput.replaceAll("[\\r\\n\\f]", "");
		if(Server.DEBUG)
			System.err.println(outputString.replaceAll("[\\r\\n.,?:%$#\\t]", ""));
		if(outputString.equals(expectedOutput))
			return new CorrectResult("Correct");
		else if(outputString.replaceAll("[\r\n.,?:%$#\t]", "").equals(expectedOutput.replaceAll("[\r\n.,?:%$#\t]", "")))
			return new OutputFormatResult("Output Format Error!");
		else
			return new WrongAnswerResult("Wrong Answer! Expected: " + expectedOutput + "\tActual: " + outputString);
	}
	
	/**
	 * Load the output file into a string
	 * @param f The expected output file
	 * @throws FileNotFoundException
	 */
	public void loadExceptedOutputFile(File f) throws FileNotFoundException
	{
		expectedOutput = "";
		Scanner input = new Scanner(f);
		while(input.hasNextLine())
			expectedOutput += input.nextLine() + "\n";
	}
	
	/**
	 * Load the input file into a string
	 * @param f The expected output file
	 * @throws FileNotFoundException
	 */	
	public void loadInputFile(File f) throws FileNotFoundException
	{
		inputString = "";
		Scanner input = new Scanner(f);
		while(input.hasNextLine())
			inputString += input.nextLine() + "\n";
	}
	
	
	/**
	 * Runs the main Method
	 * Output is stored in outputString
	 * @throws FileNotFoundException 
	 */
	public Result runMainMethod(File in, File out) throws FileNotFoundException
	{
		if(mainClass != null)
		{
			Method mainMethod = getMainMethod();
			if(mainMethod != null)
			{
				try {
					this.loadInputFile(in);
					PrintStream originalStandardOutput = System.out;
					InputStream originalStandardInput = System.in;
					outputString = "";
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ByteArrayInputStream bais = new ByteArrayInputStream(inputString.getBytes());
					System.setIn(bais);
					System.setOut(new PrintStream(baos));
					
					mainMethod.invoke(null, (Object)(new String[1]));
					outputString = baos.toString();
					
					System.setOut(originalStandardOutput);
					System.setIn(originalStandardInput);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					return new RunResult("Use of disabled functions");
				} catch (InvocationTargetException e) {
					StackTraceElement ste = e.getCause().getStackTrace()[0];
					return new RunResult("Runtime error in " + ste.getMethodName() + ":" + ste.getLineNumber());
				}
			}
			else
			{
				return new RunResult("No main method found");
			}
		}
		this.loadExceptedOutputFile(out);
		return compareOutput();
	}

	
	
	/**
	 * Looks for the main method of a class:
	 * public static void ClassName.main(java.lang.String[])
	 * @return This Method if it exists
	 */
	public Method getMainMethod()
	{
		if(mainClass != null)
		{
			Method[] methods = mainClass.getMethods();
			for(int i = 0; i < methods.length; i++)
			{
				if(methods[i].toGenericString().equals("public static void " + className + ".main(java.lang.String[])"))
					return methods[i];
			}
		}
		return null;
	}

	/**
	 * Uses the JavaCompiler to compile the source java file into a Class object: stored in mainClass
	 * @param location The location of the class to load
	 * @return A Result of whether this failed or was successful
	 */
	public Result loadClass(String location)
	{
		/*
		 * Bug in ToolProvider: Default Standard VM must be a jdk and not a jre! Will return null if it is a JRE
		 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6477844
		 */
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler == null)
		{
			System.out.println("Compiler is null.  java.exe MUST be a jdk version.\nRunning from: " 
					+System.getProperties().getProperty("java.home"));
		}
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		List<File> sourceFileList = new ArrayList<File>();
		sourceFileList.add(new File(location + ".java"));
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);

		//baos is for capturing the compile diagnostic
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompilationTask task = compiler.getTask(null, fileManager, new MyDiagnosticListener(new PrintStream(baos)),
				null , null, compilationUnits);

		boolean result = task.call();
		if(result)
		{
			try {
				MyClassLoader mcl = new MyClassLoader();
				//A-okay
				mainClass = mcl.findClass(className, location);
			} catch (Exception e) {
				e.printStackTrace();
			}

			File f = new File("Test.class");
			f.delete();
		}
		else
		{
			//get our compilation errors and throw the exception
			compilationErrors = baos.toString();
			return new CompilationResult(compilationErrors);
		}
		try
		{
			fileManager.close();
		}catch(Exception e){
			//gracefully do nothing
		}
		
		return null;
	}
}
