package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.Set;

import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class FreeSearch extends RestletResource {

	String searchStr;
	int limit = 0;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			searchStr = urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_QUERY));
			String limitStr = (String) getRequest().getAttributes().get(RestletService.PAR_TARGET_LIMIT);

			if (null != limitStr) {
				limit = new Integer(limitStr).intValue();
			}
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public Representation get(Variant variant) {
		try {
			IDMapper mapper = getIDMappers();
			Set<Xref> results = mapper.freeSearch(searchStr, limit);
			if (MediaType.APPLICATION_JSON.equals(variant.getMediaType())) {
		        JSONObject jsonObject = new JSONObject();
		        for (Xref x : results) {
		        	jsonObject.put(x.getId(), x.getDataSource().getFullName());
				}
		        return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				for (Xref x : results) {
					result.append(x.getId());
					result.append("\t");
					result.append(x.getDataSource().getFullName());
					result.append("\n");
				}

				return (new StringRepresentation(result.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
