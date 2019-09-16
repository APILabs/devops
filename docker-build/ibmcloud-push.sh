#!/bin/bash

ibmcloud login -r eu-gb --apikey vDqP7lGQsEyphj5bUeOj9NkcwmM90oLPtNrOH9cws_Hm
ibmcloud ks cluster-config aceCluster

ibmcloud ks cluster-config aceCluster --export > /tmp/ks-config.txt 2>&1
cat /tmp/ks-config.txt
`cat /tmp/ks-config.txt`

docker login appconnect-docker-local.artifactory.swg-devops.com
docker pull appconnect-docker-local.artifactory.swg-devops.com/iib/tea-app:latest

ibmcloud cr login
# Likely to fail if images don't exist
( ibmcloud cr images --quiet | xargs -n 1 ibmcloud cr image-rm ) || /bin/true

docker tag appconnect-docker-local.artifactory.swg-devops.com/iib/tea-app:latest uk.icr.io/ace-registry/tea-app:latest
docker push uk.icr.io/ace-registry/tea-app:latest
