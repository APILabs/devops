#!/bin/bash

# Yuck
mkdir ~root/.bluemix
ln -s ~jenkins/.bluemix/plugins ~root/.bluemix/plugins
ibmcloud config --check-version=false
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
# Give the container time to start
sleep 20

curl -i http://$HOST:$PORT/tea/v1/index/0 > /tmp/curl.out 2>/dev/null
cat /tmp/curl.out
echo

grep -q "200 OK" /tmp/curl.out
if [ "$?" != "0" ]; then
    echo "Failed to get tea index 0"
    exit 1
fi


