#!/bin/bash

# Internal dev pipeline
export LICENSE=accept

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
