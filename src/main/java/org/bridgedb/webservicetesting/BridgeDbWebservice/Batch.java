package org.bridgedb.webservicetesting.BridgeDbWebservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.json.simple.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class Batch extends RestletResource {

	private DataSource sourceDs;
	private DataSource targetDs;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			System.out.println("Batch Xrefs.doInit start");
			String dsName = urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_SYSTEM));
			this.sourceDs = parseDataSource(dsName);
			String targetDsName = (String) getRequest().getAttributes().get(RestletService.PAR_TARGET_SYSTEM);
			if (targetDsName != null) {
				this.targetDs = parseDataSource(targetDsName);
			}
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Post("json")
	public Representation post(Representation entity, Variant variant) {
    	if (!supportedOrganism(urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM)))) {
			String error = UNSUPPORTED_ORGANISM_TEMPLATE.replaceAll("%%ORGANISM%%", (String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM));
			StringRepresentation sr = new StringRepresentation(error);
			sr.setMediaType(MediaType.TEXT_HTML);
			return sr;
    	}
		Representation result = new StringRepresentation("");
		if (sourceDs != null) {
			result = oneDataSource(entity, variant);
		} else {
			result = multiDataSource(entity, variant);
		}
		return result;
	}

	public Representation multiDataSource(Representation entity, Variant variant) {
    	if (!supportedOrganism(urlDecode((String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM)))) {
			String error = UNSUPPORTED_ORGANISM_TEMPLATE.replaceAll("%%ORGANISM%%", (String) getRequest().getAttributes().get(RestletService.PAR_ORGANISM));
			StringRepresentation sr = new StringRepresentation(error);
			sr.setMediaType(MediaType.TEXT_HTML);
			return sr;
    	}
		System.out.println("Batch Multi Xrefs.getXrefs() start");
		try {
			// The result set
			String postBody = entity.getText();
			String[] splitXrefs = postBody.split("\n");
			IDMapper mapper = getIDMappers();
			
			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
				JSONObject jsonObject = new JSONObject();
				for (String line : splitXrefs) {
					JSONObject mappedXrefsJson = new JSONObject();
					ArrayList resultSet = new ArrayList<>();
					String[] lineSplit = line.split("\t");
					String id = lineSplit[0].trim();

					DataSource ds = parseDataSource(lineSplit[1]);
					Xref source = new Xref(id, ds);

					Set<Xref> xrefs;
					
					if (targetDs == null)
						xrefs = mapper.mapID(source);
					else
						xrefs = mapper.mapID(source, targetDs);
					if (xrefs.isEmpty()) {
						mappedXrefsJson.put("result set", "N/A");
						mappedXrefsJson.put("datasource", ds.getFullName());
						jsonObject.put(id.trim(), mappedXrefsJson);
					} else {
						Iterator<Xref> iter = xrefs.iterator();
						while (iter.hasNext()) {
							resultSet.add(iter.next().getBioregistryIdentifier());
						}
						mappedXrefsJson.put("result set", resultSet);
						mappedXrefsJson.put("datasource", ds.getFullName());
						jsonObject.put(id.trim(), mappedXrefsJson);
					}
				}
				return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				for (String line : splitXrefs) {
					String[] lineSplit = line.split("\t");
					String id = lineSplit[0].trim();
					DataSource ds = parseDataSource(lineSplit[1]);
					Xref source = new Xref(id, ds);
					Set<Xref> xrefs;

					if (targetDs == null)
						xrefs = mapper.mapID(source);
					else
						xrefs = mapper.mapID(source, targetDs);
					if (xrefs.isEmpty()) {
						result.append(id.trim());
						result.append("\t");
						result.append(ds.getFullName());
						result.append("\t");
						result.append("N/A");
						result.append("\n");
					} else {
						result.append(id.trim());
						result.append("\t");
						result.append(ds.getFullName());
						result.append("\t");
						Iterator<Xref> iter = xrefs.iterator();
						// we already tested that the set is not empty
						Xref xref = iter.next();
						result.append(xref.getDataSource().getSystemCode()).append(":")
						      .append(xref.getId().trim());
						while (iter.hasNext()) {
							xref = iter.next();
							result.append(",").append(xref.getDataSource().getSystemCode())
							      .append(":").append(xref.getId().trim());
						}
						result.append("\n");
					}
				}
				return new StringRepresentation(result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	public Representation oneDataSource(Representation entity, Variant variant) {
		System.out.println("Batch Xrefs.getXrefs() start");
		try {
			// The result set
			String[] splitXrefs = entity.getText().split("\n");
			IDMapper mapper = getIDMappers();

			if (MediaType.APPLICATION_JSON.isCompatible(variant.getMediaType())) {
				JSONObject jsonObject = new JSONObject();
				ArrayList resultSet = new ArrayList<>();
				for (String id : splitXrefs) {
					JSONObject mappedXrefsJson = new JSONObject();
					// for this id, create this xref object
					Xref xref = new Xref(id.trim(), sourceDs);
					// map this xref to result set xrefs
					Set<Xref> xrefs;
					if (targetDs == null)
						xrefs = mapper.mapID(xref);
					else
						xrefs = mapper.mapID(xref, targetDs);
					// if no xrefs were returned from the mapping:
					if (xrefs.isEmpty()) {
						mappedXrefsJson.put("result set", "N/A");
						mappedXrefsJson.put("datasource", sourceDs.getFullName());
						jsonObject.put(id.trim(), mappedXrefsJson);
					} else {
						Iterator<Xref> iter = xrefs.iterator();
						while (iter.hasNext()) {
							resultSet.add(iter.next());
						}
						mappedXrefsJson.put("result set", resultSet);
						mappedXrefsJson.put("datasource", sourceDs.getFullName());
						jsonObject.put(id.trim(), mappedXrefsJson);
					}
				}
				return new StringRepresentation(jsonObject.toString());
			} else {
				StringBuilder result = new StringBuilder();
				for (String id : splitXrefs) {
					Xref xref = new Xref(id.trim(), sourceDs);
					Set<Xref> xrefs;
					if (targetDs == null)
						xrefs = mapper.mapID(xref);
					else
						xrefs = mapper.mapID(xref, targetDs);
					if (xrefs.isEmpty()) {
						result.append(id.trim());
						result.append("\t");
						result.append(sourceDs.getFullName());
						result.append("\t");
						result.append("N/A");
						result.append("\n");
					} else {
						result.append(id.trim());
						result.append("\t");
						result.append(sourceDs.getFullName());
						result.append("\t");
						Iterator<Xref> iter = xrefs.iterator();
						result.append(iter.next());
						while (iter.hasNext()) {
							result.append("," + iter.next());
						}
						result.append("\n");
					}
				}
				return new StringRepresentation(result.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	protected DataSource parseDataSource(String dsName) {
		if (dsName == null)
			return null;
		DataSource ds = null;
		// Try parsing by full name
		if (DataSource.getFullNames().contains(dsName)) {
			ds = DataSource.getExistingByFullName(dsName);
		} else { // If not possible, use system code
			ds = DataSource.getExistingBySystemCode(dsName);
		}
		return ds;
	}

}
