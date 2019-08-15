# About nuxeo-labs-binary-gps

<a href='https://qa.nuxeo.org/jenkins/job/Sandbox/job/sandbox_nuxeo-labs-binary-gps-master/'><img src='https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-labs-binary-gps-master'></a>

This plug-in uses [exiftool](https://www.sno.phy.queensu.ca/~phil/exiftool/) with the option `-n` to extract GPS metadata in decimal format. This makes it easier to deal with positive and negative direction, and easier to integrate with services like Google Maps.

# Usage

The plugin includes a custom operation, `Document.GetGPSData`, that accepts params that correspond to the EXIF tags `GPSLongitute`, `GPSLatitude`, and `GPSPosition` (which is a [composite tag](https://sno.phy.queensu.ca/~phil/exiftool/TagNames/Composite.html)).

The values of the parameters are XPATH fields of the input document that will get these values. For example, say input document has the `gps` (custom) schema with 3 fields, `gps:DecimalLatitude`, `gps:DecimalLongitude` and `gps:DecimalPosition`. Calling theoperaiton:

```
. . .
input = Document.GetGPSData(
  input, {
    'latitudeField': 'gps:DecimalLatitude',
    'longitudeField': 'gps:DecimalLongitude',
    'positionField': 'gps:DecimalPosition'
  }
)
```

# Requirements

Building requires the following software:

* git
* maven
* exiftool

# Build

    git clone https://github.com/nuxeo-sandbox/nuxeo-labs-binary-gps
    cd nuxeo-labs-binary-gps
    mvn clean install

# Support

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.


# Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)


# About Nuxeo
[Nuxeo](www.nuxeo.com), developer of the leading Content Services Platform, is reinventing enterprise content management (ECM) and digital asset management (DAM). Nuxeo is fundamentally changing how people work with data and content to realize new value from digital information. Its cloud-native platform has been deployed by large enterprises, mid-sized businesses and government agencies worldwide. Customers like Verizon, Electronic Arts, ABN Amro, and the Department of Defense have used Nuxeo's technology to transform the way they do business. Founded in 2008, the company is based in New York with offices across the United States, Europe, and Asia.

Learn more at www.nuxeo.com.