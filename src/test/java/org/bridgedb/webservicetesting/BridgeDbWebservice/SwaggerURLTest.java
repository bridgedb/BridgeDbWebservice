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

public class SwaggerURLTest {

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
        SwaggerURLTest.server = new RestletServer();
        SwaggerURLTest.server.run(port, configFile, false, false, "https://example.org/");
    }

    @AfterAll
    public static void stopServer() {
        SwaggerURLTest.server.stop();
    }

    @Test
    public void testServerURL() throws Exception {
        String reply =  TestHelper.getContent("http://127.0.0.1:" + port + "/swagger.yaml");
        assertTrue(reply.contains("url: https://example.org"));
    }

}
