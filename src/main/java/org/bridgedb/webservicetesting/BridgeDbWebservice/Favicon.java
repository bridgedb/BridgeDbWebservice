package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class Favicon extends RestletResource{

	@Get
	public Representation get() {
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		StringRepresentation sr = new StringRepresentation("Not found");
		sr.setMediaType(MediaType.TEXT_PLAIN);
		return sr;
	}

}
