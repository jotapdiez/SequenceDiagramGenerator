package org.diagramsequencegenerator.core.codeParsers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class JavaSourceParser
{
	Map<Integer, Map<String, List<IMethod>>> _methods = null;
	ICompilationUnit _compilationUnit  = null;
	
	public JavaSourceParser()
	{
		
	}
	
	public JavaSourceParser(ICompilationUnit cu)
	{
		_methods = new HashMap<Integer, Map<String, List<IMethod>>>();
		_compilationUnit = cu;
		
		preParse();
	}
	
	private void preParse()
	{
		try {
			IType[] allTypes = _compilationUnit.getAllTypes();
			
			for (IType item : allTypes)
			{
				if (_methods.get(item.getFlags()) == null)
					_methods.put(item.getFlags(), new HashMap<String, List<IMethod>>());
					
				for (IMethod mItem : item.getMethods())
				{
					String methodName = mItem.getElementName();
					Map<String, List<IMethod>> methodEqType = _methods.get(item.getFlags());
					if ( methodEqType.get(methodName) == null)
						methodEqType.put(methodName, new LinkedList<IMethod>());
					methodEqType.get(methodName).add(mItem);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	public List<IMethod> getMethods() {
		List<IMethod> allMethods = new LinkedList<IMethod>();
		
		allMethods.addAll(getPublicMethods(null));
		allMethods.addAll(getPrivateMethods(null));
		allMethods.addAll(getProtectedMethods(null));
		
		allMethods.addAll(getDefaultMethods(null));
		
		return allMethods;
	}
	
	public List<IMethod> getPublicMethods(String name)
	{
		Map<String, List<IMethod>> publicMethod = _methods.get(Flags.AccPublic);
		return joinMethodsLists(publicMethod.values());
	}

	public List<IMethod> getPrivateMethods(String name)
	{
		Map<String, List<IMethod>> publicMethod = _methods.get(Flags.AccPrivate);
		return joinMethodsLists(publicMethod.values());
	}
	
	public List<IMethod> getDefaultMethods(String name)
	{
		Map<String, List<IMethod>> publicMethod = _methods.get(Flags.AccDefault);
		return joinMethodsLists(publicMethod.values());
	}
	
	public List<IMethod> getProtectedMethods(String name)
	{
		Map<String, List<IMethod>> publicMethod = _methods.get(Flags.AccProtected);
		return joinMethodsLists(publicMethod.values());
	}
	
	public void parseMethod(IMethod method)
	{
		//TODO: Usar clase MethodParser
	}
	
	/*
	 * UTILS
	 */

	private List<IMethod> joinMethodsLists(Collection<List<IMethod>> lists)
	{
		List<IMethod> result = new LinkedList<IMethod>();
		for (List<IMethod> item : lists)
			result.addAll(item);
		return result;
	}
}
