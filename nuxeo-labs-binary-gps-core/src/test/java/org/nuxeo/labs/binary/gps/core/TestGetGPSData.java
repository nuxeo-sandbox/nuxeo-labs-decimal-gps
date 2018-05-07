package org.nuxeo.labs.binary.gps.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.labs.binary.gps.core.operation.GetGPSData;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.labs.binary.gps.core.nuxeo-labs-binary-gps-core")
public class TestGetGPSData {


  public static final String GPS_SCHEMA_NAME = "gps";
  public static final String GPS_LONGITUDE_FIELD = GPS_SCHEMA_NAME+":decimalLongitude";
  public static final String GPS_LATITUDE_FIELD = GPS_SCHEMA_NAME+":decimalLatitude";
  public static final String GPS_POSITION_FIELD = GPS_SCHEMA_NAME+":decimalPosition";



  @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    // I wrote this just for a sanity check
    private void shouldThrowTypeException() throws OperationException {
      OperationContext ctx = new OperationContext(session);
      DocumentModel file = session.createDocumentModel("/", "File", "File");
      ctx.setInput(file);
      ctx.setCoreSession(session);

      DocumentModel doc = (DocumentModel) automationService.run(ctx, GetGPSData.ID);
    }

    @Test
    public void shouldCallGetGPSData() throws OperationException {
        OperationContext ctx = new OperationContext(session);

      DocumentModel picture = session.createDocumentModel("/", "Picture", "Picture");
      File file = new File(getClass().getResource("/files/plate.jpg").getPath());
      Blob blob = new FileBlob(file);
      picture.setPropertyValue("file:content", (Serializable) blob);
      picture = session.createDocument(picture);

      ctx.setInput(picture);
      ctx.setCoreSession(session);

        DocumentModel doc = (DocumentModel) automationService.run(ctx, GetGPSData.ID);
        assertEquals("/", doc.getPathAsString());
    }

    @Test
    public void shouldCallGetGPSDataWithParameters() throws OperationException {
        final String path = "/default-domain";
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        params.put("path", path);
        DocumentModel doc = (DocumentModel) automationService.run(ctx, GetGPSData.ID, params);
        assertEquals(path, doc.getPathAsString());
    }

    private void callGetGPSData() throws OperationException{

    }
}
