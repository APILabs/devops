tdolby-ace-ci-build:
==============================================================
#!/bin/bash

echo Starting build
. /opt/ibm/ace-11/server/bin/mqsiprofile

# Build the Java
ant -f TeaJava/build.xml
# Build the BAR file (using mqsipackagebar)
ant -f Tea/build.xml


export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job

/usr/local/bin/hub release create -a Tea.bar -m "CI release" 1.1.$BUILD_ID
==============================================================

tdolby-ace-ci-int-test:
==============================================================
#!/bin/bash

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
==============================================================


tdolby-ace-ci-preprod-bar-build:
==============================================================
#!/bin/bash

echo Starting test run
. /opt/ibm/ace-11/server/bin/mqsiprofile


export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job

# Download the application
/usr/local/bin/hub release download 1.1.$BUILD_ID
# Build a BAR for the pre-prod policies
ant -f PreProdPolicies/build.xml

# Copy them into the right location to be picked up by the docker buil
mkdir -p docker-build/ace-ci-integration-run/bars/
cp Tea.bar docker-build/ace-ci-integration-run/bars/
cp PreProdPolicies.bar docker-build/ace-ci-integration-run/bars/

# Debugging
find docker-build -ls
ls -l /usr/bin/docker
ls -lrt /usr/local/bin
ls -lrt PreProdPolicies

# log in to artifactory to push image
docker login -u ${secure_registry_user} -p ${secure_registry_password} ${registry_login_url}
#export REGISTRY="appconnect-docker-local.artifactory.swg-devops.com/iib"

export REGISTRY="uk.icr.io/ace-registry"

# set the image name in a variable
IMAGE=tea-app
VERSION=1.1.$BUILD_ID

cd docker-build/ace-ci-integration-run

# Yuck - needed because of the way ibmcloud plugins work
mkdir ~root/.bluemix
ln -s ~jenkins/.bluemix/plugins ~root/.bluemix/plugins
ibmcloud login -r eu-gb --apikey $IBMCLOUD_APIKEY
ibmcloud cr login


# Likely to fail if images don't exist
( ibmcloud cr images --quiet | xargs -n 1 ibmcloud cr image-rm ) || /bin/true


# build the image
docker build --tag ${REGISTRY}/${IMAGE}:${VERSION} . 
# Push to registry
docker push ${REGISTRY}/${IMAGE}:${VERSION}
==============================================================

tdolby-ace-ci-preprod-bar-deploy-and-test:
==============================================================
#!/bin/bash

# Yuck
mkdir ~root/.bluemix
ln -s ~jenkins/.bluemix/plugins ~root/.bluemix/plugins
ibmcloud login -r eu-de --apikey $IBMCLOUD_APIKEY
ibmcloud ks cluster-config aceCluster --export > /tmp/ks-config.txt 2>&1
cat /tmp/ks-config.txt
`cat /tmp/ks-config.txt`

kubectl delete service tea-app-service
kubectl delete deployment tea-app
kubectl run tea-app --image uk.icr.io/ace-registry/tea-app:1.1.$BUILD_ID --port=7800
kubectl expose deployment/tea-app --type=NodePort --port=7800 --target-port=7800 --name tea-app-service
ibmcloud ks workers --cluster aceCluster


kubectl get service tea-app-service
export PORT=`kubectl get service tea-app-service | tr ' ' '\n' | grep TCP | cut -c 6-10`
ibmcloud ks workers --cluster aceCluster
export HOST=`ibmcloud ks workers --cluster aceCluster | grep kube | cut -c50-68 | tr -d ' '`

echo URL is http://$HOST:$PORT/tea/v1/index

sleep 5

curl -i http://$HOST:$PORT/tea/v1/index/0 > /tmp/curl.out 2>/dev/null
cat /tmp/curl.out
echo

grep -q "200 OK" /tmp/curl.out
if [ "$?" != "0" ]; then
    echo "Failed to get tea index 0"
    exit 1
fi
==============================================================


