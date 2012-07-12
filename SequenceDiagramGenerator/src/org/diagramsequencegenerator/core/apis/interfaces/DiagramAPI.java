package org.diagramsequencegenerator.core.apis.interfaces;

public interface DiagramAPI {

	public void addMethodCall(String caller, String called, String methodName);
	public void addMethodReturn(String returner, String returned, String dataType);
	
	public void addInstanceObject(String instancer, String instanced, String params);
	
	public void destroyObject();
	public void setTitle(String string);
	
}
