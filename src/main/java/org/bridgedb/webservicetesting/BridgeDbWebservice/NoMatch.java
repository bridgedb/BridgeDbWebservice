package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class NoMatch extends RestletResource {

	@Get
	public Representation getNoMatchResult() {
		String error = "Unrecognized query<p><font size='+1'><i>Double check the spelling and syntax. We are expecting "
				+ "something like: <a href='http://webservice.bridgedb.org/Human/xrefs/L/1234'>webservice.bridgedb.org/Human/xrefs/L/1234</a></i></font></p>";
		StringRepresentation sr = new StringRepresentation(error);
		sr.setMediaType(MediaType.TEXT_HTML);
		return sr;
	}

}
