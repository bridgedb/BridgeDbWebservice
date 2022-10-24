package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.bio.DataSourceTxt;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestJson {

	public static void main(String[] args) throws Exception {
		
		String datasourcesTxt = "";
		String datasourcesHeaders = "";
		InputStream headers = DataSourceTxt.class.getClassLoader().getResourceAsStream("org/bridgedb/bio/datasources_headers.tsv");	
		InputStream is = DataSourceTxt.class.getClassLoader().getResourceAsStream("org/bridgedb/bio/datasources.tsv");
		BufferedReader reader = new BufferedReader (new InputStreamReader (is));
		String line;
   		while ((line = reader.readLine()) != null) {
			datasourcesTxt = datasourcesTxt + line + "\n";
            		String[] fields = line.split ("\\t");
        	}
		BufferedReader readerHeader = new BufferedReader (new InputStreamReader (headers));
		String lineHeader;
   		while ((lineHeader = readerHeader.readLine()) != null) {
				String[] headerFields = lineHeader.split("\\t");
				datasourcesHeaders = datasourcesHeaders + headerFields[1] + "\n";
        	}
   		String[] headerArray = datasourcesHeaders.split("\\n");
   		String[] datasourcesArray = datasourcesTxt.split("\\n");
   		
   		System.out.println(headerArray.length);
   		System.out.println(datasourcesArray.length);
   		
   		JSONObject jsonObject = new JSONObject();
   		
   		for (int i=0;i<datasourcesArray.length; i++) {
   			JSONObject datasourceInfo = new JSONObject();
   			JSONArray datasourceInformation = new JSONArray();
   			Map<String, Object> map = new HashMap<String, Object>();
   			
   			System.out.println(datasourcesArray[i]);
   			String[] thisDataSource = datasourcesArray[i].split("\\t");
   			for (int j=1;j<thisDataSource.length; j++) {
   				datasourceInfo.put(headerArray[j+1],thisDataSource[j]);
   				map.put(headerArray[j+1], thisDataSource[j]);
   			}
   			datasourceInformation.add(datasourceInfo);
   			
   			System.out.println(thisDataSource[0]);
   			System.out.println("data source info:");
   	   		System.out.println(datasourceInfo.toString());
   			System.out.println("data source information:");
   	   		System.out.println(datasourceInformation.toJSONString());
   			jsonObject.put(thisDataSource[0], datasourceInformation);

   		}
   		
   		System.out.println("json object:");
   		System.out.println(jsonObject.toJSONString());
	}
}
