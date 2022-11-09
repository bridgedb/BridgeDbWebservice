package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Map;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class AttributeSearch extends RestletResource {
	String searchStr;
	String attribute;
	int limit = 0;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			this.searchStr = getAttribute(RestletService.PAR_QUERY);
			this.attribute = getAttribute(RestletService.PAR_TARGET_ATTR_NAME);
			String limitStr = getAttribute(RestletService.PAR_TARGET_LIMIT);

			if (null != limitStr) {
				this.limit = new Integer(limitStr).intValue();
			}
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get("json")
	public Representation get(Variant variant) {
		try {
			IDMapperStack stack = getIDMappers();
			if (attribute == null) attribute = "Symbol"; // use symbol by default.
			Map<Xref, String> results = stack.freeAttributeSearch(searchStr, attribute, limit);
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
				JSONObject jsonObject = new JSONObject();
				JSONObject attributeSearchResult = new JSONObject();
				int i=1;
				for (Xref x : results.keySet()) {
					attributeSearchResult.put("id: ", x.getId());
					attributeSearchResult.put("full name: ", x.getDataSource().getFullName());
					attributeSearchResult.put("xref: ", results.get(x));
					i++;
					jsonObject.put(i,attributeSearchResult);
				}
				
				return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				for (Xref x : results.keySet()) {
					result.append(x.getId());
					result.append("\t");
					result.append(x.getDataSource().getFullName());
					result.append("\t");
					result.append(results.get(x));
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
