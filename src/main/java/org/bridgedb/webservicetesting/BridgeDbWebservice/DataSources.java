package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bridgedb.bio.DataSourceTxt;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DataSources extends ServerResource{

	@Get
	public Representation get(Variant variant) {
		if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
			String datasourcesTxt = "";
			String datasourcesHeaders = "";
			InputStream headers = DataSourceTxt.class.getClassLoader().getResourceAsStream("org/bridgedb/bio/datasources_headers.tsv");	
			InputStream is = DataSourceTxt.class.getClassLoader().getResourceAsStream("org/bridgedb/bio/datasources.tsv");
			BufferedReader reader = new BufferedReader (new InputStreamReader (is));
			String line;
	   		try {
				while ((line = reader.readLine()) != null) {
					datasourcesTxt = datasourcesTxt + line + "\n";
				    		String[] fields = line.split ("\\t");
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
			BufferedReader readerHeader = new BufferedReader (new InputStreamReader (headers));
			String lineHeader;
	   		try {
				while ((lineHeader = readerHeader.readLine()) != null) {
						String[] headerFields = lineHeader.split("\\t");
						datasourcesHeaders = datasourcesHeaders + headerFields[1] + "\n";
					}
			} catch (IOException e) {
				e.printStackTrace();
			}
	   		String[] headerArray = datasourcesHeaders.split("\\n");
	   		String[] datasourcesArray = datasourcesTxt.split("\\n");	
	   		JSONObject jsonObject = new JSONObject();
	   		
	   		for (int i=0;i<datasourcesArray.length; i++) {
	   			JSONObject datasourceInfo = new JSONObject();
	   			
	   			String[] thisDataSource = datasourcesArray[i].split("\\t");
	   			for (int j=1;j<thisDataSource.length; j++) {
	   				datasourceInfo.put(headerArray[j+1],thisDataSource[j]);
	   			}
	   			jsonObject.put(thisDataSource[0],datasourceInfo);
	   		}
	   		return new StringRepresentation(jsonObject.toString());
		}
		else {
			return new StringRepresentation(DataSourceTxt.datasourcesTxt);
		}
	}

}
