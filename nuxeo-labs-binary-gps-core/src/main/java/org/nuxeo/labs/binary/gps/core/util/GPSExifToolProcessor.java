package org.nuxeo.labs.binary.gps.core.util;

import org.nuxeo.binary.metadata.internals.ExifToolProcessor;
import org.nuxeo.ecm.core.api.Blob;

import java.util.Map;

public class GPSExifToolProcessor extends ExifToolProcessor {

  private static final String EXIFTOOL_READ_GPS = "exiftool-get-gps-data";

  public Map<String, Object> readGPSMetadata(Blob blob) {
    return readMetadata(EXIFTOOL_READ_GPS, blob, null, true);
  }

}
