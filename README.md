# About nuxeo-labs-binary-gps

<a href='https://qa.nuxeo.org/jenkins/job/Sandbox/job/sandbox_nuxeo-labs-binary-gps-master/'><img src='https://qa.nuxeo.org/jenkins/buildStatus/icon?job=Sandbox/sandbox_nuxeo-labs-binary-gps-master'></a>

This plug-in uses [exiftool](https://www.sno.phy.queensu.ca/~phil/exiftool/) with the option `-n` to extract GPS metadata in decimal format. This makes it easier to deal with positive and negative direction, and easier to integrate with services like Google Maps.

# Usage

The plugin includes a custom operation, `Document.GetGPSData`, that accepts params that correspond to the EXIF tags `GPSLongitute`, `GPSLatitude`, and `GPSPosition` (which is a [composite tag](https://sno.phy.queensu.ca/~phil/exiftool/TagNames/Composite.html)).

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

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).  