package org.diagramsequencegenerator.core.codeParsers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.diagramsequencegenerator.core.apis.interfaces.DiagramAPI;
import org.diagramsequencegenerator.core.apis.webSequenceDiagrams.WebSequenceDiagrams;
import org.diagramsequencegenerator.core.codeParsers.visitors.MethodVisitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

public class MethodParser {

	private boolean DEBUG = false;
	
	IMethod _method = null;
	public MethodParser(IMethod iMethod)
	{
		_method = iMethod;
	}
	
	String project = null;
	List<IMethod> methods = new LinkedList<IMethod>(); 
	DiagramAPI diagramApi = null;
	IMethod returnerMethod = null;
	
	private void buildHierachyList(IMethod method)
	{
		if (DEBUG) System.out.println("#"+method.getElementName());
		
		HashSet<IMethod> i2 = getCallsOf(method);
		for (IMethod met : i2)
		{
			if (DEBUG) System.out.println("#itemProject: " + met.getAncestor(IJavaElement.JAVA_PROJECT).getElementName());
			String itemProject = met.getAncestor(IJavaElement.JAVA_PROJECT).getElementName();
			if (itemProject.equals(project))
			{
				diagramApi.addMethodCall(method.getParent().getElementName(), met.getParent().getElementName(), met.getElementName());
				methods.add(met);
				buildHierachyList(met);
			}
		}
		
		try {
			if (returnerMethod != null)
			{
				if (DEBUG) System.out.println("#"+method.getElementName()+" returntype: "+ method.getReturnType());
				diagramApi.addMethodReturn(returnerMethod.getParent().getElementName(), method.getParent().getElementName(), returnerMethod.getReturnType());
				returnerMethod = null;
			}
			
			if (!method.getReturnType().equals("V"))
				returnerMethod = method;
				
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	public void parse() throws JavaModelException
	{
		project = _method.getAncestor(IJavaElement.JAVA_PROJECT).getElementName();
		
		diagramApi = new WebSequenceDiagrams("Example");
		
//		buildHierachyList(_method);

		diagramApi.setTitle(project + "::" + _method.getParent().getElementName()+"."+_method.getElementName());
		ICompilationUnit methodCompilationUnit = _method.getCompilationUnit();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(methodCompilationUnit); // set source
		parser.setResolveBindings(true);
		
		ASTNode node = parser.createAST(null /* IProgressMonitor */);
		MethodVisitor visitor = new MethodVisitor(_method, diagramApi);
		visitor.setDebug(DEBUG);
		node.accept(visitor);
	}

	
	public HashSet<IMethod> getCallersOf(IMethod m) {

		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = { m };

		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		HashSet<IMethod> callers = new HashSet<IMethod>();
		for (MethodWrapper mw : methodWrappers) {
			MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
			HashSet<IMethod> temp = getIMethods(mw2);
			callers.addAll(temp);
		}

		return callers;
	}

	public HashSet<IMethod> getCallsOf(IMethod m) {

		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = { m };

		MethodWrapper[] methodWrappers = callHierarchy.getCalleeRoots(members);
		HashSet<IMethod> callers = new HashSet<IMethod>();
		for (MethodWrapper mw : methodWrappers) {
			MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
			HashSet<IMethod> temp = getIMethods(mw2);
			callers.addAll(temp);
		}

		return callers;
	}
	
	HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IMethod> c = new HashSet<IMethod>();
		for (MethodWrapper m : methodWrappers) {
			IMethod im = getIMethodFromMethodWrapper(m);
			if (im != null) {
				c.add(im);
			}
		}
		return c;
	}

	IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
		try {
			IMember im = m.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return (IMethod) m.getMember();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
