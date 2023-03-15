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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class RestletServerTest {

	private static int port = 1074;
	private static RestletServer server;

    @BeforeAll
    public static void startServer() throws IOException {
        // set up a test Derby file
        File derbyFile = File.createTempFile("bdb", "bridge");
        derbyFile.deleteOnExit();
        InputStream stream = RestletServerTest.class.getClassLoader().getResourceAsStream("humancorona-2021-11-27.bridge");
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
        RestletServerTest.server = new RestletServer();
        RestletServerTest.server.run(port, configFile, false, false);
    }

    @AfterAll
    public static void stopServer() {
        RestletServerTest.server.stop();
    }

    @Test
    public void testProperties() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/properties");
        assertTrue(reply.contains("DATASOURCENAME"));
    }

    @Test
    public void testSources() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/sourceDataSources");
        assertTrue(reply.contains("Wikidata"));
    }

    @Test
    public void testTargets() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/targetDataSources");
        assertTrue(reply.contains("Wikidata"));
    }

    @Test
    public void testXrefExists() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/xrefExists/Wd/Q90038963");
        assertTrue(reply.equals("true"));
    	reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/xrefExists/Wd/Q0");
        assertTrue(reply.equals("false"));
    }

    @Test
    public void testXrefs() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/xrefs/Wd/Q90038963");
        assertTrue(reply.contains("Wikidata"));
        assertTrue(reply.contains("P0DTD1-PRO_0000449625"));
    }

    @Test
    public void testXrefs_DataSource() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/xrefs/Wd/Q90038963?dataSource=S");
        assertFalse(reply.contains("Wikidata"));
        assertTrue(reply.contains("P0DTD1-PRO_0000449625"));
    }

    @Test
    public void testNoBioregistry() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/xrefs/Wd/Q90038963");
        assertFalse(reply.contains("wikidata:Q90038963"));
    }

    @Test
    public void testXrefsBatch() throws Exception {
        String requestBody = "Q90038963\tWd\n"
        		+ "P0DTD1-PRO_0000449625\tS\n";
        String reply = TestHelper.postContent("http://127.0.0.1:" + port + "/Human/xrefsBatch", requestBody);
        assertTrue(reply.contains("S:P0DTD1-PRO_0000449625"));
        assertTrue(reply.contains("Wd:Q90038963"));
        assertFalse(reply.contains(":T"));
        assertFalse(reply.contains(":F"));
    }

    @Test
    public void testAttributes() throws Exception {
        String reply = TestHelper.getContent("http://127.0.0.1:" + port + "/Human/attributes/Wd/Q90038963");
        assertTrue(reply.contains("virus"));
        assertTrue(reply.contains("SARS-CoV-2"));
    }

    @Disabled("Known to not work right now")
    public void testAttributesViaMappings() throws Exception {
        String replyWikidata = TestHelper.getContent("http://127.0.0.1:" + port + "/Human/attributes/Wd/Q90038963");
        String replyUniprot = TestHelper.getContent("http://127.0.0.1:" + port + "/Human/attributes/S/P0DTD1-PRO_0000449625");
        assertSame(replyWikidata, replyUniprot);
    }

    @Test
    public void testIsMappingSupported() throws Exception {
        String reply = TestHelper.getContent("http://127.0.0.1:" + port + "/Human/isMappingSupported/Wd/S");
        assertTrue(reply.equals("true"));
        reply = TestHelper.getContent("http://127.0.0.1:" + port + "/Human/isMappingSupported/S/Wd");
        assertTrue(reply.equals("true"));
    }

    @Test
    public void testAtributeSet() throws Exception {
        String reply = TestHelper.getContent("http://127.0.0.1:" + port + "/Human/attributeSet");
        assertTrue(reply.contains("virus"));
    }

    @Test
    public void testFavicon() throws Exception {
        FileNotFoundException thrown = assertThrows(
            FileNotFoundException.class,
                () -> TestHelper.getContent("http://127.0.0.1:" + port + "/favicon.ico"),
                "Expected a 404 but did not get it"
            );
        assertTrue(thrown.getMessage().contains("favicon.ico"));
    }

    @Test
    public void testConfig() throws Exception {
        String reply = TestHelper.getContent("http://127.0.0.1:" + port + "/config");
        assertTrue(reply.contains("java.version"));
        assertTrue(reply.contains("bridgedb.version"));
        assertTrue(reply.contains("webservice.version"));
    }

    @Test
    public void testContents() throws Exception {
        TestHelper.getContent("http://127.0.0.1:" + port + "/contents");
        // this normally returns a list of species, but not for this test file
    }

    @Test
    public void testDatasources() throws Exception {
        String reply = TestHelper.getContent("http://127.0.0.1:" + port + "/datasources");
        assertTrue(reply.contains("KNApSAcK"));
    }

    @Test
    public void testIsFreeSearchSupported() throws Exception {
        String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/Human/isFreeSearchSupported");
        assertEquals("true", reply);
    }

    @Test
    public void testAttributeSearch() throws Exception {
    	TestHelper.getContent("http://127.0.0.1:" + port + "/Human/attributeSearch/virus");
    	// does not return anything right now, bc the test file does not have synonyms
    }

    @Test
    public void testNoMatch() throws Exception {
    	String reply = TestHelper.getContent("http://127.0.0.1:" + port + "/Human");
    	assertTrue(reply.contains("check the spelling and syntax"));
    }

    @Test
    public void testSearch() throws Exception {
    	TestHelper.getContent("http://127.0.0.1:" + port + "/Human/search/virus");
    	// does not return anything right now, bc the test file does not have synonyms
    }

}
