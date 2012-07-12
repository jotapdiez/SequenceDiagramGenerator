package org.diagramsequencegenerator.views.items;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.swt.graphics.Image;

public class MethodTreeitem
{
	boolean isRoot = false;
	boolean _isDefault = false;
	
	private Image image = null;
	
	private IMethod _imethod = null;
	
	String _name = "";
	
	List<MethodTreeitem> _childList = null;
	MethodTreeitem _parent = null;
	
	public MethodTreeitem(IMethod imethod) {
		_imethod = imethod;
		_name = imethod.getElementName();
		setIsRoot(false);
		_isDefault = false;
	}

	/**
	 * Constructor usado para agregar elementos base (isDefault=true)
	 * @param name
	 */
	public MethodTreeitem(String name, boolean isRoot) {
		_name = name;
		setIsRoot(isRoot);
		_isDefault = true;
	}
	
	
//	public MethodTreeitem(MethodTreeitem parent) {
//		_parent = parent; 
//	}
//	
//	public MethodTreeitem(MethodTreeitem parent, String name) {
//		_parent = parent; 
//		_name = name;
//		_parent.addChild(this);
//		
//		if (parent.isRoot)
//			isDefault = true;
//	}
	
//	public MethodTreeitem(MethodTreeitem parent, IMethod imethod) {
//		_parent = parent;
//		_parent.addChild(this);
//		_name = imethod.getElementName();
//		setIsRoot(false);
//		isDefault = false;
//	}
	
	public void addChild(MethodTreeitem child)
	{
		getChildsList().add(child);
	}

	public void setIsRoot(boolean value)
	{
		isRoot = value;
	}

	private List<MethodTreeitem>  getChildsList()
	{
		if (_childList == null)
			_childList = new LinkedList<MethodTreeitem>();
		return _childList;
	}
	public Object[] getChildrens() {
		
		return getChildsList().toArray();
	}

	public boolean hasChilds() {
		return getChildsList().toArray().length > 0;
	}

	public boolean isRoot() {
		return isRoot;
	}
	
	public MethodTreeitem getParent() {
		return _parent;
	}

	public String getName() {
		return _name;
	}

	public boolean isDefault() {
		return _isDefault;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public MethodTreeitem getTreeItem(String string) {
		Iterator<MethodTreeitem> it = getChildsList().iterator();
		while (it.hasNext())
		{
			MethodTreeitem item = it.next();
			if (item.getName().equals(string))
				return item;
		}
		return null;		
	}

	public IMethod getIMethod() {
		return _imethod;
	}
	
}
