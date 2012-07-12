package org.diagramsequencegenerator.core.apis.webSequenceDiagrams;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.diagramsequencegenerator.core.apis.interfaces.DiagramAPI;

public class WebSequenceDiagrams implements DiagramAPI
{
	public void writeSequenceDiagramImage(DiagramItemData diag) {
		try {
			// Build parameter string
			String data = "style=" + diag.style + "&message=" + URLEncoder.encode(diag.diag, "UTF-8");

			// Send the request
			URL url = new URL("http://www.websequencediagrams.com");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			// write parameters
			writer.write(data);
			writer.flush();

			// Get the response
			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}
			writer.close();
			reader.close();

			String json = answer.toString();
			int start = json.indexOf("?img=");
			int end = json.indexOf("\"", start);

			url = new URL("http://www.websequencediagrams.com/" + json.substring(start, end));

			OutputStream out = new BufferedOutputStream(new FileOutputStream(diag.fileName));
			InputStream in = url.openConnection().getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
//			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
//				numWritten += numRead;
			}

			in.close();
			out.close();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String lastActivated = null;
	private String _title = null;
	
	public WebSequenceDiagrams(String title) {
		_title = title;
	}

	@Override
	public void setTitle(String string) {
		System.out.println("title "+string);
	}
	
	@Override
	public void addMethodCall(String caller, String called, String methodName) {
		if (lastActivated != null && !lastActivated.equals(caller))
		{
			System.out.println("activate " + caller);
		}
		System.out.println(caller+"->"+called+": " + methodName);
		lastActivated = caller;
	}
	
	@Override
	public void addMethodReturn(String returner, String returned, String dataType) {
		if (lastActivated != null && !lastActivated.equals(returner))
		{
			System.out.println("activate "+returner);
		}
		System.out.println(returner+"->"+returned+": " + dataType);
		lastActivated = returner;
	}

	@Override
	public void addInstanceObject(String instancer, String instanced, String params) {
		boolean useParams = params != null && !params.equals("");
		
		System.out.println(instancer+"->"+instanced+": " + params + (useParams ? " ("+params+")" : "") );
		lastActivated = instancer;
	}

	@Override
	public void destroyObject() {
		// TODO Auto-generated method stub
		
	}

}
