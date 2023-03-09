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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RestletServerTest {

	private static int port = 1074;
	private static RestletServer server;

    @BeforeClass
    public static void startServer() {
        File configFile = new File("./gdb.config");

        RestletServerTest.server = new RestletServer();
        RestletServerTest.server.run(port, configFile, false, false);
    }

    @AfterClass
    public static void stopServer() {
        RestletServerTest.server.stop();
    }

    @Test
    public void testProperties() throws Exception {
        URL queryURL = new URL("http://127.0.0.1:" + port + "/Human/properties");
        URLConnection connection = queryURL.openConnection();
        InputStream input = connection.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        input.transferTo(buffer);
        String reply = buffer.toString();
        Assert.assertTrue(reply.contains("DATASOURCENAME"));
    }

}
