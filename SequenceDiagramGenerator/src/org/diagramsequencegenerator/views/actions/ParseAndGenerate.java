package org.diagramsequencegenerator.views.actions;

import java.net.URL;

import org.diagramsequencegenerator.core.codeParsers.MethodParser;
import org.diagramsequencegenerator.views.MethodsSelector;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class ParseAndGenerate extends Action {

	private MethodsSelector viewPart;
	
	public ParseAndGenerate(MethodsSelector theViewPart) {
		super("Generate Sequence Diagram", Action.AS_PUSH_BUTTON);
		
		URL url = getClass().getResource("/icons/sequence-diagram-16x16.png");
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		
		setImageDescriptor(image);
		
		viewPart = theViewPart;
	}
	
	public void run() {
		MethodParser mp = new MethodParser(viewPart.getSelected());
		try {
			mp.parse();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
