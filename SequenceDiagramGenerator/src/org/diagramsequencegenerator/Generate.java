package org.diagramsequencegenerator;

import org.diagramsequencegenerator.core.codeParsers.JavaSourceParser;
import org.diagramsequencegenerator.views.MethodsSelector;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class Generate extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);

		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit) firstElement;
//			IResource res = cu.getResource();
//			boolean newDirectory = true;
//			directory = getPersistentProperty(res, path);
//
//			if (directory != null && directory.length() > 0) {
//				newDirectory = !(MessageDialog.openQuestion(
//						HandlerUtil.getActiveShell(event), "Question",
//						"Use the previous output directory?"));
//			}
//			analyze(cu);
			
			JavaSourceParser jsp = new JavaSourceParser(cu);
			
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.diagramsequencegenerator.views.MethodsSelector");
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			for (IViewReference item : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences())
			{
				IWorkbenchPart i = item.getPart(true);
				
				if (i instanceof MethodsSelector)
				{
					MethodsSelector view = (MethodsSelector) i;
//					view.setInput(new MethodTreeitem("/"));

					view.setInput(jsp.getPublicMethods(null));
					view.setFocus();
				}
			}
//			view.setInput(jsp.getPublicMethods(null));
//			if (newDirectory) {
//				DirectoryDialog fileDialog = new DirectoryDialog(
//						HandlerUtil.getActiveShell(event));
//				directory = fileDialog.open();
//
//			}
//			if (directory != null && directory.length() > 0) {
//				analyze(cu);
//				setPersistentProperty(res, path, directory);
//				write(directory, cu);
//			}

		} else {
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"Information", "Please select a Java source file");
		}
		return null;
	}

//	protected String getPersistentProperty(IResource res, QualifiedName qn) {
//		try {
//			return res.getPersistentProperty(qn);
//		} catch (CoreException e) {
//			return "";
//		}
//	}
//
//
//	protected void setPersistentProperty(IResource res, QualifiedName qn,
//			String value) {
//		try {
//			res.setPersistentProperty(qn, value);
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
//	}
}
