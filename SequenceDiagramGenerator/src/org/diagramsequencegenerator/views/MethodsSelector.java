package org.diagramsequencegenerator.views;


import java.util.Iterator;
import java.util.List;

import org.diagramsequencegenerator.views.actions.ParseAndGenerate;
import org.diagramsequencegenerator.views.items.MethodTreeitem;
import org.diagramsequencegenerator.views.providers.TreeViewLabelProvider;
import org.diagramsequencegenerator.views.providers.TreeViewerContentProvider;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class MethodsSelector extends ViewPart implements ISelectionChangedListener
{
	public static final String ID = "org.diagramsequencegenerator.views.MethodsSelector";

	private TreeViewer provider = null;
	
	private ParseAndGenerate parseAndGenerateAction = null;
	
	private IMethod _selected = null;
	
	@Override
	public void createPartControl(Composite parent)
	{
		MethodTreeitem defaultObject = null;
		
			provider = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
			TreeViewerContentProvider treeProvider = new TreeViewerContentProvider(provider);
			
			provider.setContentProvider(treeProvider);
			provider.setLabelProvider(new TreeViewLabelProvider());
			
//			((TreeViewer) provider).expandAll();
			defaultObject = treeProvider.getDefaultRoot();
		
		provider.addSelectionChangedListener(this);
		
		makeActions();
		
		getSite().setSelectionProvider(provider);
		setInput(defaultObject);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setInput(MethodTreeitem treeItem)
	{
		provider.setInput(treeItem);
		provider.expandAll();
	}

	public void setInput(List<IMethod> methods)
	{
		TreeViewerContentProvider prov = (TreeViewerContentProvider) provider.getContentProvider();
		MethodTreeitem newRoot = prov.getDefaultRoot();
		
		Iterator<IMethod> it = methods.iterator();
		
		MethodTreeitem parent = null;
		while (it.hasNext())
		{
			IMethod method = it.next();
			
			int flags;
			try
			{
				flags = method.getFlags();
				if (Flags.isPublic(flags))
				{
					if (parent == null || !parent.getName().equals("public"))
						parent = newRoot.getTreeItem("public");
				}else if (Flags.isPrivate(flags))
				{
					if (parent == null || !parent.getName().equals("private"))
						parent = newRoot.getTreeItem("private");
				}else if (Flags.isProtected(flags))
				{
					if (parent == null || !parent.getName().equals("protected"))
						parent = newRoot.getTreeItem("protected");
				}else
				{
					if (parent == null || !parent.getName().equals("default"))
						parent = newRoot.getTreeItem("default");
				}
				
				parent.addChild(new MethodTreeitem(method));
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
		
		setInput(newRoot);
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(parseAndGenerateAction);
	}
	
	private void makeActions()
	{
		parseAndGenerateAction = new ParseAndGenerate(this);
		parseAndGenerateAction.setEnabled(false);
		
		contributeToActionBars();
	}
	
	public IMethod getSelected()
	{
		return _selected;
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// if the selection is empty clear the label
	       if(event.getSelection().isEmpty()) {
		       parseAndGenerateAction.setEnabled(false);
	           return;
	       }
	       
	       TreeSelection selected = (TreeSelection) event.getSelection();
//	       System.out.println("Seleccionado: "+selected.getFirstElement());
	       
	       MethodTreeitem itemSelected = (MethodTreeitem) selected.getFirstElement();
	       if (itemSelected.getIMethod() == null)
	       {
//		       System.out.println("Seleccionado un padre? (getIMethod() == null)");
		       parseAndGenerateAction.setEnabled(false);
	    	   return;
	       }
	       
	       System.out.println("Seleccionado: "+itemSelected.getName());
	       
	       _selected = itemSelected.getIMethod();
	       
	       parseAndGenerateAction.setEnabled(true);
	       
//	       if(event.getSelection() instanceof IStructuredSelection) {
//	           IStructuredSelection selection = (IStructuredSelection)event.getSelection();
//	           StringBuffer toShow = new StringBuffer();
//	           for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
//	               Object domain = (Model) iterator.next();
//	               String value = labelProvider.getText(domain);
//	               toShow.append(value);
//	               toShow.append(", ");
//	           }
//	           // remove the trailing comma space pair
//	           if(toShow.length() > 0) {
//	               toShow.setLength(toShow.length() - 2);
//	           }
//	           text.setText(toShow.toString());
//	       }
//		
	}
}
