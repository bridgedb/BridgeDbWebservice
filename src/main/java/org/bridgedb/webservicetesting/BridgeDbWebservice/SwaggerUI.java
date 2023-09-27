// Copyright 2023  Egon Willighagen <egonw@users.sf.net>
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
//
package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Properties;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * Service calls that serve the Swagger (OpenAPI) files. The files are stored in the resources/swagger
 * folder.
 */
public class SwaggerUI extends RestletResource{

	@Get
	public Representation get() {
		String foo = getReference().getLastSegment();
		try {
			if (foo == null) foo = "index.html";

			String content = new String(SwaggerUI.class.getResourceAsStream("/swagger/" + foo).readAllBytes());

			// customize the content for the version service
			if ("swagger.yaml".equals(foo)) {
				Properties props = new Properties();
				props.load(RestletServer.class.getClassLoader().getResourceAsStream("webservice.props"));
			    String version = props.getProperty("webservice.version");
			    content = content.replaceAll("%%BRIDGEDB-WEBSERVICE-VERSION%%", version);
			}
			// customize the server URL
			if ("swagger.yaml".equals(foo)) {
				String serverURL = ((RestletService)getApplication()).SERVER_URL;
				System.out.println("Server URL: " + serverURL);
				if (serverURL != null)
					content = content.replaceAll("%%BRIDGEDB-SERVER-URL%%", serverURL);
			}

			StringRepresentation sr = new StringRepresentation(content);
			// set the proper MIME types
			if (foo.endsWith(".html")) sr.setMediaType(MediaType.TEXT_HTML);
			if (foo.endsWith(".js")) sr.setMediaType(MediaType.APPLICATION_JAVASCRIPT);
			if (foo.endsWith(".css")) sr.setMediaType(MediaType.TEXT_CSS);
			return sr;
		} catch (Exception e) {
			return new StringRepresentation(e.getMessage());
		}
	}

}
