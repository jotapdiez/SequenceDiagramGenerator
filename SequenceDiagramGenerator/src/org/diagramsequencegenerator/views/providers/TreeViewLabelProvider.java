package org.diagramsequencegenerator.views.providers;

import java.net.URL;

import org.diagramsequencegenerator.views.items.MethodTreeitem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class TreeViewLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		MethodTreeitem item = (MethodTreeitem) element;
		return item.getName();
	}
	
//	public String getColumnText(Object obj, int index)
//	{
//		IMethod method = (IMethod) obj;
//		return method.getElementName();
//	}

//	public Image getColumnImage(Object obj, int index) {
//		return this.getImage(obj);
//	}
	
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof MethodTreeitem)
		{
			MethodTreeitem item = (MethodTreeitem) obj;
			if (item.isRoot())
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			
			if (item.getImage() != null)
				return item.getImage();
		}
		
		URL url = getClass().getResource("/icons/jdt/compare_method.gif");
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}
}
