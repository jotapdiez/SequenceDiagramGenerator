package org.diagramsequencegenerator.views.providers;

import java.net.URL;

import org.diagramsequencegenerator.views.items.MethodTreeitem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TreeViewerContentProvider implements ITreeContentProvider {
	
	MethodTreeitem root = new MethodTreeitem("/", true);
	
	public TreeViewerContentProvider(Viewer tree) {
//		parentTree = (TreeViewer) tree;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		MethodTreeitem item = (MethodTreeitem) parentElement;
		return item.getChildrens();
	}

	@Override
	public Object[] getElements(Object parentElement) {
		MethodTreeitem item = (MethodTreeitem) parentElement;
		if (item.isRoot())
			return item.getChildrens();
		else
			return item.getParent().getChildrens();
	}

	@Override
	public Object getParent(Object parentElement) {
		MethodTreeitem item = (MethodTreeitem) parentElement;
		return item.getParent();
	}

	@Override
	public boolean hasChildren(Object parentElement) {
		MethodTreeitem item = (MethodTreeitem) parentElement;
		return item.hasChilds();
	}

	@Override
	public void dispose() {
		root = null;
	}

	public MethodTreeitem getDefaultRoot()
	{
		URL url = null;
		ImageDescriptor image = null;
		
		// TODO: Arreglar. Que al padre se le pase la instancia del hijo y no al revez
		root = new MethodTreeitem("/", true);
		{
			MethodTreeitem publics    = new MethodTreeitem("public", false);
			url = getClass().getResource("/icons/jdt/methpub_obj.gif");
			image = ImageDescriptor.createFromURL(url);
			publics.setImage(image.createImage(false));
			root.addChild(publics);
		}
		{
			MethodTreeitem privates   = new MethodTreeitem("private", false);
			url = getClass().getResource("/icons/jdt/methpri_obj.gif");
			image = ImageDescriptor.createFromURL(url);
			privates.setImage(image.createImage(false));
			root.addChild(privates);
		}

		{
			MethodTreeitem protecteds = new MethodTreeitem("protected", false);
			url = getClass().getResource("/icons/jdt/methpro_obj.gif");
			image = ImageDescriptor.createFromURL(url);
			protecteds.setImage(image.createImage(false));
			root.addChild(protecteds);
		}

		{
			MethodTreeitem defaults = new MethodTreeitem("default", false);
			url = getClass().getResource("/icons/jdt/methdef_obj.gif");
			image = ImageDescriptor.createFromURL(url);
			defaults.setImage(image.createImage(false));
			root.addChild(defaults);
		}
		
		return root;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}
}