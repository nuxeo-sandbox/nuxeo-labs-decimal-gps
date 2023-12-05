package org.nuxeo.labs.decimal.gps.core.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.schema.types.TypeException;
import org.nuxeo.labs.decimal.gps.core.util.GPSExifToolProcessor;

import java.util.Map;

/**
 *
 */
@Operation(id = GetGPSData.ID, category = Constants.CAT_DOCUMENT, label = "Get GPS Data", description = "Get GPS data using exiftool with the -n switch.")
public class GetGPSData {

  public static final String ID = "Document.GetGPSData";

  private static final Log log = LogFactory.getLog(GetGPSData.class);

  // These are the tag keys returned by exiftool
  private static final String GPS_KEY_POSITION = "GPSPosition";
  private static final String GPS_KEY_LATITUDE = "GPSLatitude";
  private static final String GPS_KEY_LONGITUDE = "GPSLongitude";

  @Context
  protected CoreSession session;

  @Param(name = "longitudeField", required = false)
  protected String longitudeField;

  @Param(name = "latitudeField", required = false)
  protected String latitudeField;

  @Param(name = "positionField", required = false)
  protected String positionField;

  @Param(name = "xpath", required = false, values = { "file:content" })
  protected String xpath = "file:content";

  @OperationMethod
  public DocumentModel run(DocumentModel input) throws TypeException, OperationException {
    // Make sure the document is a Picture
    if (!input.hasFacet("Picture")) {
      throw new TypeException("Picture document required");
    }

    // Make sure at least 1 param is passed
    if (longitudeField == null && latitudeField == null && positionField == null) {
      throw new OperationException("Need at least one parameter");
    }
    Property fileProp = input.getProperty(xpath);
    Blob blob = (Blob) fileProp.getValue();
    if (blob == null) {
      throw new OperationException("Need a picture blob at xpath " + xpath);
    }

    // Extract GPS metadata
    GPSExifToolProcessor myExifToolProcessor = new GPSExifToolProcessor();
    Map<String, Object> result = myExifToolProcessor.readGPSMetadata(blob);

    // Map GPS metadata to schema fields
    if (result.containsKey(GPS_KEY_POSITION) && (positionField != null)) {
      input.setPropertyValue(positionField, (String) result.get(GPS_KEY_POSITION));
    }
    if (result.containsKey(GPS_KEY_LATITUDE)&& (latitudeField != null)) {
      input.setPropertyValue(latitudeField, result.get(GPS_KEY_LATITUDE).toString());
    }
    if (result.containsKey(GPS_KEY_LONGITUDE)&& (longitudeField != null)) {
      input.setPropertyValue(longitudeField, result.get(GPS_KEY_LONGITUDE).toString());
    }

    return input;
  }

}
