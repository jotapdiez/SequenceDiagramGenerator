package org.diagramsequencegenerator.core.codeParsers.visitors;

import org.diagramsequencegenerator.core.apis.interfaces.DiagramAPI;
import org.diagramsequencegenerator.core.codeParsers.utils.Binding2JavaModel;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;

public class MethodVisitor extends ASTVisitor {

    static class ParentData
    {
    	protected String _classMethodCallerName = null;
    	protected String _methodCallerName = null;
    	
    	protected String _classReturnToMethodCallerName = null;
    	protected String _methodReturnToCallerName = null;
    	
    	protected int mMethodEndPosition;
    	protected int mMethodStartPosition;
    	
    	protected IMethod imethod = null;
    	
    	protected ParentData(IMethod method)
    	{
    		imethod = method;
    		_classMethodCallerName = method.getParent().getElementName();
    		_methodCallerName = method.getElementName();
    		
            try {
                ISourceRange sourceRange = method.getSourceRange();
                this.mMethodStartPosition = sourceRange.getOffset();
                this.mMethodEndPosition = mMethodStartPosition + sourceRange.getLength();
            } catch (JavaModelException jme) {
//            	jme.printStackTrace();
            }
    	}
    	
    	protected ParentData(String className, String methodName, int startPos, int endPos)
    	{
    		_classMethodCallerName = className;
    		_methodCallerName = methodName;
    		
    		mMethodEndPosition = endPos;
    		mMethodStartPosition = startPos;
    	}

    	public void setReturnTo(IMethod method)
    	{
    		_classReturnToMethodCallerName = method.getParent().getElementName();
    		_methodReturnToCallerName = method.getElementName();
    	}
    	
    	public boolean isInsideMethod(ASTNode node)
    	{
            int nodeStartPosition = node.getStartPosition();
            int nodeEndPosition = nodeStartPosition + node.getLength();

            if (nodeStartPosition < mMethodStartPosition) {
                return false;
            }

            if (nodeEndPosition > mMethodEndPosition) {
                return false;
            }

            return true;
        }
    	
    	public boolean isSameProject(IMethod node)
    	{
    		String project = imethod.getJavaProject().getElementName();
    		String nodeProject = node.getJavaProject().getElementName();
    		return project.equals(nodeProject);
    	}
    }
    
	private boolean DEBUG = false;
	private DiagramAPI _api = null;
	
    private ParentData _data = null;
    
	public MethodVisitor(IMethod callerMethod, DiagramAPI api) {
		this( new ParentData(callerMethod), api);
	}
	
	public MethodVisitor(ParentData data, DiagramAPI api)
	{
		_data = data;
		_api = api;
	}

	public void setDebug(boolean debug)
	{
		DEBUG = debug;
	}
	
	public void setReturnTo(IMethod method)
	{
		_data.setReturnTo(method);	
	}
	
	private IMethod ASTNodeToIMethod(IMethodBinding calledMethodBinding, ASTNode node)
	{
        IType calledType = null;
        try {
	        ITypeBinding calledTypeBinding = calledMethodBinding.getDeclaringClass();
	        if (!calledTypeBinding.isAnonymous()) {
	            calledType = Binding2JavaModel.find(calledTypeBinding, _data.imethod.getJavaProject());
	        } else {
				calledType = Binding2JavaModel.find(calledTypeBinding.getInterfaces()[0], _data.imethod.getJavaProject());
	        }
	        if (calledType != null)
	        	return calledType.getMethod(calledMethodBinding.getName(), null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
        return null;
	}

	protected void followMethod(IMethodBinding calledMethodBinding, ASTNode node)
	{
		IMethod calledMethod1 = ASTNodeToIMethod(calledMethodBinding, node);
		followMethod(calledMethod1);
	}
	
	protected void followMethod(IMethod node)
	{
        ICompilationUnit methodCompilationUnit = node.getCompilationUnit();
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(methodCompilationUnit); // set source
        parser.setResolveBindings(true);
        
        ASTNode astNode = parser.createAST(null /* IProgressMonitor */);
        MethodVisitor innerVisitor = new MethodVisitor(node, _api);
        innerVisitor.setReturnTo(_data.imethod);
        innerVisitor.setDebug(DEBUG);
        astNode.accept(innerVisitor);
    }

	@Override
	public boolean visit(ReturnStatement node)
	{
//		if (DEBUG) System.out.println("#ReturnStatement: " + node);
		if (!_data.isInsideMethod(node))
		{
//			if (DEBUG) System.out.println("#ReturnStatement OUTSIDE METHOD (_data._methodCallerName: "+_data._methodCallerName+")");
			return false;
		}
		
		if (_data._classReturnToMethodCallerName == null) return false;
//		IMethodBinding methodBindind = node.resolveMethodBinding();
//		String className = methodBindind.getDeclaringClass().getName();
//		String methodName = methodBindind.getName();
//		
		_api.addMethodReturn(_data._classMethodCallerName, _data._classReturnToMethodCallerName, node.getExpression().toString());
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node)
	{
//		if (DEBUG) System.out.println("#MethodInvocation: " + node);
		
		if (!_data.isInsideMethod(node))
		{
//			if (DEBUG) System.out.println("#MethodInvocation OUTSIDE METHOD (_data._methodCallerName: "+_data._methodCallerName+")");
			return false;
		}
		
		IMethodBinding methodBindind = node.resolveMethodBinding();
		String className = methodBindind.getDeclaringClass().getName();
		String methodName = methodBindind.getName();
		_api.addMethodCall(_data._classMethodCallerName, className, methodName);
		
		IMethod invoquedMethod = ASTNodeToIMethod(methodBindind, node);
		if (invoquedMethod == null)
			return false;
		
		if (_data.isSameProject(invoquedMethod))
			followMethod(invoquedMethod);
		
		return true;
	}

//	@Override
//	public void preVisit(ASTNode node) {
//		if (DEBUG) System.out.println("#preVisit (_data._methodCallerName: "+_data._methodCallerName+") node: " + node);
//		super.preVisit(node);
//	}

//	@SuppressWarnings("rawtypes")
//	@Override
//	public boolean visit(VariableDeclarationExpression node)
//	{
//		for (Iterator iter = node.fragments().iterator(); iter.hasNext();) {
//			VariableDeclarationFragment fragment = (VariableDeclarationFragment) iter.next();
//			System.out.println("LocalVariableDetector::visit(VariableDeclarationExpression node):ClassName: "+fragment.getClass().getCanonicalName());
//		}
//		return true;
//	}
	
	
//	@Override
//	public boolean visit(SimpleName node)
//	{
//		System.out.println("SimpleName: " + node);
//		return true;
//	}

//	@Override
//	public boolean visit(Block node)
//	{
//		System.out.println("#Block: " + node);
//		return true;
//	}

    //	public MethodVisitor(String classMethodCaller, String nameMethodCaller, DiagramAPI api) {
//		_api = api;
//		_classMethodCaller = classMethodCaller;
//		_methodCaller = nameMethodCaller;
//	}
//	

    //    private boolean isNodeWithinMethod(ASTNode node) {
//        int nodeStartPosition = node.getStartPosition();
//        int nodeEndPosition = nodeStartPosition + node.getLength();
//
//        if (nodeStartPosition < mMethodStartPosition) {
//            return false;
//        }
//
//        if (nodeEndPosition > mMethodEndPosition) {
//            return false;
//        }
//
//        return true;
//    }


    //	@Override
//	public boolean visit(ExpressionStatement node)
//	{
//		if (DEBUG) System.out.println("#ExpressionStatement: " + node);
//		return true;
//	}

//	@Override
//	public boolean visit(ClassInstanceCreation node)
//	{
//		if (DEBUG) System.out.println("#ClassInstanceCreation: " + node);
//		return true;
//	}

//	@Override
//	public boolean visit(VariableDeclarationStatement node)
//	{
//		if (DEBUG) System.out.println("#VariableDeclarationStatement: " + node);
//		return true;
//	}
//	
//	@Override
//	public void endVisit(VariableDeclarationExpression node) {
//		if (DEBUG) System.out.println("#VariableDeclarationExpression: " + node);
//	}
//	
//	@Override
//	public void endVisit(VariableDeclarationFragment node) {
//		if (DEBUG) System.out.println("#VariableDeclarationFragment: " + node);
//	}
	
}
