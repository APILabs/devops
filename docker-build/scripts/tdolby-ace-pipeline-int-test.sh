#!/bin/bash

# Internal dev pipeline
export LICENSE=accept

echo Starting test run
. /opt/ibm/ace-11/server/bin/mqsiprofile


export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job

/usr/local/bin/hub release download 1.1.$BUILD_ID

# Create the configuration to run
mqsicreateworkdir /tmp/work
# sed -i 's/#port: 7600/port: -1/g' /tmp/work/server.conf.yaml
# sed -i "s/#policyProject: 'DefaultPolicies'/policyProject: 'TestPolicies'/g" /tmp/work/server.conf.yaml
mqsisetdbparms -w /tmp/work -n jdbc::testCreds -u $TEST_DB_USER -p $TEST_DB_PASSWORD

mqsibar -c -w /tmp/work -a Tea.bar

cp -r TestPolicies /tmp/work/run

# Override the defaults to switch off the REST Admin TCPIP port and tell the
# server to use TestPolicies as the default policy project.
cp TestPolicies/test-policies-overrides.yaml /tmp/work/overrides/server.conf.yaml

# Now we have everything ready, start the server and run the tests.
ls -la
cd TeaTest
ls -la
chmod 775 *sh
./start-server-and-run-tests.sh ./tea-test.sh
