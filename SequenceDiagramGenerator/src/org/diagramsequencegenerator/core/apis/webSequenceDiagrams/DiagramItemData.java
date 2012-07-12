package org.diagramsequencegenerator.core.apis.webSequenceDiagrams;

public class DiagramItemData
{
	public String	diag;
	public String	fileName;
	public String	style = "rose";
	
	public DiagramItemData()
	{
		
	}
	
	public DiagramItemData(String diag, String fileName)
	{
		this.diag = diag;
		this.fileName = fileName;
	}
}