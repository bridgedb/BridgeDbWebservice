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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SwaggerUITest {

	private static int port = 1074;
	private static RestletServer server;

    @BeforeAll
    public static void startServer() throws IOException {
        // set up the GDB config file
        File configFile = File.createTempFile("gdb", "config");
        configFile.deleteOnExit();
        FileOutputStream outputStream = new FileOutputStream(configFile);
        BufferedOutputStream bufferStream = new BufferedOutputStream(outputStream);
        bufferStream.write("".getBytes());
        bufferStream.close();
        outputStream.close();

        // set up the REST service
        SwaggerUITest.server = new RestletServer();
        SwaggerUITest.server.run(port, configFile, false, false);
    }

    @AfterAll
    public static void stopServer() {
        SwaggerUITest.server.stop();
    }

    @Test
    public void testIndexHTML() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/");
        assertTrue(reply.contains("<head"));
    }
    
    @Test
    public void testSwaggerYAML() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/swagger.yaml");
        assertTrue(reply.contains("openapi: 3"));
		Properties props = new Properties();
		props.load(RestletServer.class.getClassLoader().getResourceAsStream("webservice.props"));
	    String version = props.getProperty("webservice.version");
        assertTrue(reply.contains("version: " + version));
    }
    
    @Test
    public void testCSS() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/index.css");
        assertTrue(reply.contains("html {"));
    }
    
    @Test
    public void testJavaScript() throws Exception {
    	String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/swagger-initializer.js");
        assertTrue(reply.contains("window.onload = function() {"));
    }
    
}
