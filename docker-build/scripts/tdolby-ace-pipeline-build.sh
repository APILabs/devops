#!/bin/bash

# Internal dev pipeline
export LICENSE=accept

echo Starting build
. /opt/ibm/ace-11/server/bin/mqsiprofile

# Build the Java
ant -f TeaJava/build.xml
# Build the BAR file including the shared library (using mqsipackagebar)
ant -f Tea/build.xml


export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job

echo "Pushing release 1.1.$BUILD_ID"

/usr/local/bin/hub release create -a Tea.bar -m "CI release" 1.1.$BUILD_ID
