#!/bin/bash

# Internal dev pipeline
export LICENSE=accept

echo Starting build
. /opt/ibm/ace-11/server/bin/mqsiprofile

# Build the Java unit tests
ant -f TeaFlowUnitTests/build.xml

export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job
/usr/local/bin/hub release download 1.1.$BUILD_ID

export TEST_BAR_DIR="$PWD/"

export CLASSPATH=$PWD/docker-build/ace-ci-build/hamcrest-all-1.3.jar:$PWD/docker-build/ace-ci-build/junit-4.11.jar:$PWD/docker-build/ace-ci-build/mockito-all-1.10.19.jar:$PWD/TeaFlowUnitTests/dist/TeaFlowUnitTests.jar:$PWD/TeaFlowUnitTests/xmlunit-matchers-2.6.2.jar:$PWD/TeaFlowUnitTests/xmlunit-core-2.6.2.jar:$CLASSPATH

# Run the tests; return code should signal failure to Jenkins
IntegrationServer -w /tmp/ut-work --admin-rest-api -1  --user-junit-test com.ibm.ace.ci.flowUnitTests.TestHeaderAddition
