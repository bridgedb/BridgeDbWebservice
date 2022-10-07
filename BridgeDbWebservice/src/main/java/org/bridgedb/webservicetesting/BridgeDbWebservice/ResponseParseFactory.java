package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.LinkedHashMap;

//import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

public class ResponseParseFactory {

 //static Logger logger = Logger.getLogger(ResponseParseFactory.class);

 @SuppressWarnings({ "rawtypes", "unchecked" })
 public String getFailureJsonString(String msg){
  String jsonString = "";
  LinkedHashMap list = new LinkedHashMap();
  list.put("response_status", "false");

  list.put("result", msg+"");
  jsonString = JSONValue.toJSONString(list);
  //logger.info(jsonString);
  return jsonString;
 }

 @SuppressWarnings({ "rawtypes", "unchecked" })
 public String getSuccessJsonString(String msg){
  String jsonString = "";
  LinkedHashMap list = new LinkedHashMap();
  list.put("response_status","true");

  list.put("result", msg);
  jsonString = JSONValue.toJSONString(list);
  //logger.info(jsonString);
  return jsonString;
 }
}
