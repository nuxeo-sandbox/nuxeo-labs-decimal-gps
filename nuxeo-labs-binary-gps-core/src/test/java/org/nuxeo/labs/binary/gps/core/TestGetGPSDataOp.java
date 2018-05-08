package org.nuxeo.labs.binary.gps.core;

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

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
    "nuxeo-labs-binary-gps-core",
})
@Deploy({
    "org.nuxeo.ecm.platform.picture.core",
    "org.nuxeo.ecm.platform.tag"
})
@Deploy({
    "nuxeo-labs-binary-gps-core:OSGI-INF/test-disabled-listener-contrib.xml",
})
public class TestGetGPSDataOp {

  public static final String GPS_SCHEMA_NAME = "gps";
  public static final String GPS_LONGITUDE_FIELD = GPS_SCHEMA_NAME + ":decimalLongitude";
  public static final String GPS_LATITUDE_FIELD = GPS_SCHEMA_NAME + ":decimalLatitude";
  public static final String GPS_POSITION_FIELD = GPS_SCHEMA_NAME + ":decimalPosition";

  @Inject
  protected CoreSession session;

  @Inject
  protected AutomationService automationService;

  @Test
  public void shouldCallGetGPSData() throws OperationException {
    OperationContext ctx = new OperationContext(session);

    DocumentModel picture = session.createDocumentModel(session.getRootDocument().getPathAsString(), "picture", "Picture");
    File file = new File(getClass().getResource("/files/plate.jpg").getPath());
    Blob blob = new FileBlob(file);
    picture.setPropertyValue("file:content", (Serializable) blob);
    picture = session.createDocument(picture);

    Map<String, Object> params = new HashMap<>();
    params.put("positionField", "dc:description");


    ctx.setInput(picture);
    ctx.setCoreSession(session);

    DocumentModel doc = (DocumentModel) automationService.run(ctx, GetGPSData.ID, params);
    assertEquals("/picture", doc.getPathAsString());
  }

}