#!/bin/bash

ibmcloud login -r eu-de --apikey vDqP7lGQsEyphj5bUeOj9NkcwmM90oLPtNrOH9cws_Hm
ibmcloud ks cluster-config aceCluster --export > /tmp/ks-config.txt 2>&1
cat /tmp/ks-config.txt
`cat /tmp/ks-config.txt`

kubectl delete service tea-app-service
kubectl delete deployment tea-app
kubectl run tea-app --image uk.icr.io/ace-registry/tea-app:latest --port=7800
kubectl expose deployment/tea-app --type=NodePort --port=7800 --target-port=7800 --name tea-app-service
ibmcloud ks workers --cluster aceCluster


kubectl get service tea-app-service
export PORT=`kubectl get service tea-app-service | tr ' ' '\n' | grep TCP | cut -c 6-10`
ibmcloud ks workers --cluster aceCluster
export HOST=`ibmcloud ks workers --cluster aceCluster | grep kube | cut -c50-68 | tr -d ' '`

echo URL is http://$HOST:$PORT/tea/v1/index
