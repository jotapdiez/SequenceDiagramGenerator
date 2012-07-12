package org.diagramsequencegenerator.core;

import org.diagramsequencegenerator.core.codeParsers.MethodParser;
import org.diagramsequencegenerator.views.items.MethodTreeitem;

public class SequenceDiagramsGenerator {

	
	public static void generateAndShow(MethodTreeitem treeItem)
	{
		if (treeItem.getIMethod() == null)
			return;
		
		MethodParser methodParser = new MethodParser(treeItem.getIMethod());
		
	}
}
