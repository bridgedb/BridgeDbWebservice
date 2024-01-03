// Copyright 2023 Egon Willighagen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.bridgedb.webservicetesting.BridgeDbWebservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RestletServerJSONTest {

	private static int port = 1074;
	private static RestletServer server;

    @BeforeAll
    public static void startServer() throws IOException {
        // set up a test Derby file
        File derbyFile = File.createTempFile("bdb", "bridge");
        derbyFile.deleteOnExit();
        InputStream stream = RestletServerJSONTest.class.getClassLoader().getResourceAsStream("humancorona-2021-11-27.bridge");
        FileOutputStream derbyStream = new FileOutputStream(derbyFile);
        stream.transferTo(derbyStream);
        derbyStream.close();
        stream.close();

        // set up the GDB config file
        File configFile = File.createTempFile("gdb", "config");
        configFile.deleteOnExit();
        FileOutputStream outputStream = new FileOutputStream(configFile);
        BufferedOutputStream bufferStream = new BufferedOutputStream(outputStream);
        String configFileContent = "*\t" +  derbyFile.getAbsolutePath();
        bufferStream.write(configFileContent.getBytes());
        bufferStream.close();
        outputStream.close();

        // set up the REST service
        RestletServerJSONTest.server = new RestletServer();
        RestletServerJSONTest.server.run(port, configFile, false, false);
    }

    @AfterAll
    public static void stopServer() {
    	RestletServerJSONTest.server.stop();
    }

    @Test
    public void testSources() throws Exception {
    	String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/sourceDataSources");
        assertTrue(reply.contains("Wikidata"));
        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        JSONArray sources = (JSONArray)root.get("supportedSourceDatasources");
        assertNotNull(sources);
        assertTrue(sources.toList().contains("Wikidata"));
    }

    @Test
    public void testTargets() throws Exception {
        String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/targetDataSources");
        assertTrue(reply.contains("Wikidata"));
        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        JSONArray sources = (JSONArray)root.get("supportedTargetDatasources");
        assertNotNull(sources);
        assertTrue(sources.toList().contains("Wikidata"));
        assertTrue(sources.toList().contains("Guide to Pharmacology Targets"));
    }

    @Test
    public void testSources_UnknownSpecies() throws Exception {
    	String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Catz/sourceDataSources");
        assertTrue(reply.contains("<html>"));
        assertTrue(reply.contains("Unknown organism"));
    }

    @Test
    public void testProperties() throws Exception {
        String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/properties");
        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        String source = (String)root.get("DATASOURCENAME");
        assertNotNull(source);
        assertTrue(source.equals("Wikidata"));
        String version = (String)root.get("SCHEMAVERSION");
        assertNotNull(version);
        assertTrue(version.equals("3"));
    }

    @Test
    public void testContents() throws Exception {
    	TestHelper.getJSONContent("http://127.0.0.1:" + port + "/contents");
        // this normally returns a list of species, but not for this test file
    }

    @Test
    public void testDatasources() throws Exception {
    	String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/datasources");
    	// test if the reply is JSON
        JSONTokener tokener = new JSONTokener(reply);
        new JSONObject(tokener);
        assertTrue(reply.contains("KNApSAcK"));
    }

    @Test
    public void testXrefsBatch() throws Exception {
        String requestBody = "Q90038963\tWd\n"
        		+ "P0DTD1-PRO_0000449625\tS\n";
        String reply =  TestHelper.postJSONContent("http://127.0.0.1:" + port + "/Human/xrefsBatch", requestBody);
        assertTrue(reply.contains("wikidata:Q90038963"));

        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        assertEquals("Uniprot-TrEMBL", ((JSONObject)root.get("P0DTD1-PRO_0000449625")).get("datasource"));
    }

    @Test
    public void testXrefsBatch_DataSource() throws Exception {
        String requestBody = "Q90038963\n";
        String reply =  TestHelper.postJSONContent("http://127.0.0.1:" + port + "/Human/xrefsBatch/Wd", requestBody);
        assertTrue(reply.contains("wikidata:Q90038963"));

        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        assertEquals("Wikidata", ((JSONObject)root.get("Q90038963")).get("datasource"));
        assertTrue(((JSONArray)((JSONObject)root.get("Q90038963")).get("result set")).toList().contains("uniprot:P0DTD1-PRO_0000449625"));
    }

    @Test
    public void testAttributes() throws Exception {
        String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/attributes/Wd/Q90038963");
        assertTrue(reply.contains("virus"));
        assertTrue(reply.contains("SARS-CoV-2"));

        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        assertEquals("SARS-CoV-2", root.get("virus"));
    }

    @Test
    public void testAtributeSet() throws Exception {
        String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/attributeSet");
        assertTrue(reply.contains("virus"));

        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        assertEquals("virus", ((JSONArray)root.getJSONArray("attributes")).get(0));
    }

    @Test
    public void testXrefs() throws Exception {
    	String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/xrefs/Wd/Q90038963");
        assertTrue(reply.contains("Wikidata"));
        assertTrue(reply.contains("P0DTD1-PRO_0000449625"));

        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        assertEquals("Wikidata", root.get("wikidata:Q90038963"));
        assertEquals("Uniprot-TrEMBL", root.get("uniprot:P0DTD1-PRO_0000449625"));
    }

    @Test
    public void testXrefs_DataSource() throws Exception {
    	String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/xrefs/Wd/Q90038963?dataSource=S");
        assertFalse(reply.contains("Wikidata"));
        assertTrue(reply.contains("P0DTD1-PRO_0000449625"));

        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
        assertEquals("Uniprot-TrEMBL", root.get("uniprot:P0DTD1-PRO_0000449625"));
        JSONException thrown = assertThrows(
        	JSONException.class,
                () -> root.get("wikidata:Q90038963")
            );
        assertTrue(thrown.getMessage().contains("wikidata:Q90038963"));
        assertTrue(thrown.getMessage().contains("not found"));
    }

    @Test
    public void testIsFreeSearchSupported() throws Exception {
        String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/isFreeSearchSupported");
        assertTrue(reply.contains("true"));

        // test if the JSON can be parsed
        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
    }

    @Test
    public void testConfig() throws Exception {
        String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/config");
        assertTrue(reply.contains("java.version"));
        assertTrue(reply.contains("bridgedb.version"));
        assertTrue(reply.contains("webservice.version"));

        // test if the JSON can be parsed
        JSONTokener tokener = new JSONTokener(reply);
        JSONObject root = new JSONObject(tokener);
    }

	@Test
	public void testAttributeSearch() throws Exception {
		String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/attributeSearch/virus");
		assertNotNull(reply);
		// does not return anything right now, bc the test file does not have synonyms
	}

    @Test
    public void testTargets_UnknownSpecies() throws Exception {
        String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Catz/targetDataSources");
        assertTrue(reply.contains("Unknown organism"));
    }

    @Test
    public void testXrefExists() throws Exception {
    	String reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/xrefExists/Wd/Q90038963");
        assertTrue(reply.contains("true"));
    	reply =  TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/xrefExists/Wd/Q0");
        assertTrue(reply.contains("false"));
    }

    @Test
    public void testIsMappingSupported_UnsupportedSpecies() throws Exception {
        String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Catz/isMappingSupported/Wd/S");
        assertTrue(reply.contains("Unknown organism"));
    }

    @Test
    public void testSearch() throws Exception {
        TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Human/search/virus");
        // does not return anything right now, bc the test file does not have synonyms
    }

    @Test
    public void testSearch_UnsupportedSpecies() throws Exception {
        String reply = TestHelper.getJSONContent("http://127.0.0.1:" + port + "/Catz/search/virus");
        assertTrue(reply.contains("Unknown organism"));
    }

}
