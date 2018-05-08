package org.nuxeo.labs.binary.gps.core.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.binary.metadata.api.BinaryMetadataException;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CloseableFile;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.schema.types.TypeException;
import org.nuxeo.ecm.platform.commandline.executor.api.*;
import org.nuxeo.runtime.api.Framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 */
@Operation(id = GetGPSData.ID, category = Constants.CAT_DOCUMENT, label = "Get GPS Data", description = "Get GPS data using exiftool with the -n switch.")
public class GetGPSData {

  public static final String ID = "Document.GetGPSData";

  private static final Log log = LogFactory.getLog(GetGPSData.class);

  @Context
  protected CoreSession session;

  protected CommandLineExecutorService commandLineService;

  protected Pattern VALID_EXT = Pattern.compile("[a-zA-Z0-9]*");

  protected ObjectMapper jacksonMapper;


  @Param(name = "longitudeField", required = false)
  protected String longitudeField;

  @Param(name = "latitudeField", required = false)
  protected String latitudeField;

  @Param(name = "positionField", required = false)
  protected String positionField;

  @OperationMethod
  public DocumentModel run(DocumentModel input) throws TypeException, OperationException {
    // Make sure the document is a Picture
    if (!input.getType().equals("Picture"))
      throw new TypeException("Picture document required");

    // Make sure at least 1 param is passed
    if (longitudeField == null && latitudeField == null && positionField == null)
      throw new OperationException("Need at least one parameter");

    Property fileProp = input.getProperty("file:content");
    Blob blob = (Blob) fileProp.getValue();
    if (blob == null)
      throw new OperationException("Need a picture blob");

    // Extract GPS metadata
    Map<String, Object> result = readMetadata("exiftool-get-gps-data", blob);

    log.warn(result);

    // Map GPS metadata to schema fields

    return input;
  }

  protected Map<String, Object> readMetadata(String command, Blob blob) {

    commandLineService = Framework.getService(CommandLineExecutorService.class);
    jacksonMapper = new ObjectMapper();

    CommandAvailability ca = commandLineService.getCommandAvailability(command);
    if (!ca.isAvailable()) {
      throw new BinaryMetadataException("Command '" + command + "' is not available.");
    }
    if (blob == null) {
      throw new BinaryMetadataException("The following command " + ca + " cannot be executed with a null blob");
    }
    try {
      ExecResult er;
      try (CloseableFile source = getTemporaryFile(blob)) {
        CmdParameters params = commandLineService.getDefaultCmdParameters();
        params.addNamedParameter("inFilePath", source.getFile());

        er = commandLineService.execCommand(command, params);
      }
      return returnResultMap(er);
    } catch (CommandNotAvailable commandNotAvailable) {
      throw new RuntimeException("Command '" + command + "' is not available.", commandNotAvailable);
    } catch (IOException ioException) {
      throw new BinaryMetadataException(ioException);
    }
  }

  protected CloseableFile getTemporaryFile(Blob blob) throws IOException {
    String ext = FilenameUtils.getExtension(blob.getFilename());
    if (!VALID_EXT.matcher(ext).matches()) {
      ext = "tmp";
    }
    File tmp = Framework.createTempFile("nxblob-", '.' + ext);
    File file = blob.getFile();
    if (file == null) {
      // if we don't have an underlying File, use a temporary File
      try (InputStream in = blob.getStream()) {
        Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
    } else {
      // attempt to create a symbolic link, which would be cheaper than a copy
      tmp.delete();
      try {
        Files.createSymbolicLink(tmp.toPath(), file.toPath().toAbsolutePath());
      } catch (IOException | UnsupportedOperationException e) {
        // symbolic link not supported, do a copy instead
        Files.copy(file.toPath(), tmp.toPath());
      }
    }
    return new CloseableFile(tmp, true);
  }

  protected Map<String, Object> returnResultMap(ExecResult er) throws IOException {
    if (!er.isSuccessful()) {
      throw new BinaryMetadataException(
          "There was an error executing " + "the following command: " + er.getCommandLine(), er.getError());
    }
    StringBuilder sb = new StringBuilder();
    for (String line : er.getOutput()) {
      sb.append(line);
    }
    String jsonOutput = sb.toString();
    List<Map<String, Object>> resultList = jacksonMapper.readValue(jsonOutput,
        new TypeReference<List<HashMap<String, Object>>>() {
        });
    Map<String, Object> resultMap = resultList.get(0);

    return resultMap;
  }

}
