package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.restlet.resource.Get;

public class NoMatch extends RestletResource{
	@Get
	public String getNoMatchResult() 
	{
		throw new IllegalArgumentException("Unrecognized query<p><font size='+1'><i>Double check the spelling and syntax. We are expecting something like: <a href='http://webservice.bridgedb.org/Human/xrefs/L/1234'>webservice.bridgedb.org/Human/xrefs/L/1234</a></i></font></p>");
	}

}
