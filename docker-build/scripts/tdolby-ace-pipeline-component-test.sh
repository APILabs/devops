#!/bin/bash

# Internal dev pipeline
export LICENSE=accept

echo Starting test run
. /opt/ibm/ace-11/server/bin/mqsiprofile


# Build the Java unit tests
ant -f TeaFlowComponentTests/build.xml

export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job

/usr/local/bin/hub release download 1.1.$BUILD_ID

# Create the configuration to run
mqsicreateworkdir /tmp/work
# sed -i 's/#port: 7600/port: -1/g' /tmp/work/server.conf.yaml
# sed -i "s/#policyProject: 'DefaultPolicies'/policyProject: 'TestPolicies'/g" /tmp/work/server.conf.yaml
mqsisetdbparms -w /tmp/work -n jdbc::preProdCreds -u xhs95598 -p "hxz57vc2km9^vxd9"
cp -r PreProdPolicies /tmp/work/run

# Override the defaults to switch off the REST Admin TCPIP port and tell the
# server to use PreProdPolicies as the default policy project.
cp PreProdPolicies/preprod-policies-overrides.yaml /tmp/work/overrides/server.conf.yaml

echo "/tmp/work"
find /tmp/work -type f -print

export TEST_BAR_DIR="$PWD/"

export CLASSPATH=$PWD/docker-build/ace-ci-build/hamcrest-all-1.3.jar:$PWD/docker-build/ace-ci-build/junit-4.11.jar:$PWD/docker-build/ace-ci-build/mockito-all-1.10.19.jar:$PWD/TeaFlowComponentTests/dist/TeaFlowComponentTests.jar:$CLASSPATH

# Run the tests; return code should signal failure to Jenkins
IntegrationServer -w /tmp/work --admin-rest-api -1  --user-junit-test com.ibm.ace.ci.flowComponentTests.TestJDBCConnection
