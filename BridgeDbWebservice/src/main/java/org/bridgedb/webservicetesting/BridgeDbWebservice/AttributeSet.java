package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Map;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class AttributeSet extends RestletResource {

	@Get("json")
	public Representation get(Variant variant) {
		try {
			IDMapperStack stack = getIDMappers();
			Set<String> attributes = stack.getAttributeSet();
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
				JSONObject jsonObject = new JSONObject();
				int i = attributes.size();
				for (String a : attributes) {
					System.out.println(a);
					jsonObject.put("attribute "+ Integer.toString(i) +" :", a);
					i--;
				}
				return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				for (String a : attributes) {
					result.append(a);
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
}
