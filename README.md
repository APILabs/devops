# ace-ci-workspace

Sample project to demonstrate a basic pipeline with ACE v11, Jenkins, and Docker.

This project depends on some Docker images being present in a registry (currently the IIB folder in Artifactory):
 - ace-minimal (see tdolby/ot4i-ace-docker in the experimental directory for more info)
 - ace-ci-build (see docker-build in this repo)
 - ace-ci-integration-run (see docker-build in this repo)

The project itself is a trivial database access application that uses only one table and creates that table during flow operation if needed.

Possibly interesting curl commands:

curl http://host:7800/tea/v1/index/1


curl -i -X POST -d '{"name":"Redbush"}' http://172.17.0.3:7800/tea/v1/index

etc.

This project is picked up by some Jenkins jobs in a pipeline here:

http://9.20.64.158:8080/view/Experimental%20ACE%20CI%20pipeline/


 - tdolby-ace-ci-build runs the initial build
 - tdolby-ace-ci-int-test runs integration tests against a test database
 - tdolby-ace-ci-preprod-bar-build builds the docker image containing the application and policies
 - tdolby-ace-ci-preprod-bar-deploy-and-test deploys to the cloud and runs pre-prod tests against DB2 on Cloud

Sample piepline component overview: 

![sample pipeline](https://github.ibm.com/TDOLBY/ace-ci-workspace/blob/master/sample-ci-pipeline-graphic-small.gif)
 
Download EGit-4.11 (earlier failed to connect to GHE and later had loads of NPEs) from here:

https://wiki.eclipse.org/EGit/FAQ#Where_can_I_find_older_releases_of_EGit.3F

and clone this repo into the toolkit workspace. (might also need Git for Windows; I have 2.21 installed but am not certain this is needed).
