package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.bridgedb.IDMapper;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class IsFreeSearchSupported extends RestletResource {

	@Get("json")
	public Representation get(Variant variant) {
		try {
			IDMapper mapper = getIDMappers();
			boolean isSupported = mapper.getCapabilities().isFreeSearchSupported();
			if(MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("isSupported", isSupported);
				return new StringRepresentation(jsonObject.toString());
			}
			else {
				return new StringRepresentation("" + isSupported);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
