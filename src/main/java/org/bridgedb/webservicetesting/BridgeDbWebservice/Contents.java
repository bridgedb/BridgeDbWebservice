package org.bridgedb.webservicetesting.BridgeDbWebservice;

import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.GdbProvider;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class Contents extends RestletResource {

	@Get
	public Representation get(Variant variant) {
		if (getGdbProvider().getOrganisms().size() == 0) {
			return new StringRepresentation("\n");
		}
		try {
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
		        JSONObject jsonObject = new JSONObject();
				for (Organism org : getGdbProvider().getOrganisms()) {
		        	jsonObject.put(org.shortName(), org.latinName());
				}

		        return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				for (Organism org : getGdbProvider().getOrganisms()) {
					result.append(org.shortName());
					result.append("\t");
					result.append(org.latinName());
					result.append("\n");
				}
				return new StringRepresentation(result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	private GdbProvider getGdbProvider() {
		return ((RestletService) getApplication()).getGdbProvider();
	}

}
