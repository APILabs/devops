#!/bin/bash

export GITHUB_HOST=github.ibm.com
export HUB_PROTOCOL=https
# GITHUB_TOKEN set as credentials in job

/usr/local/bin/hub release download 1.1.$BUILD_ID

# We now have Tea.bar (from GHE) and PreProdPolicies in a directory from the git repo
mkdir -p temp

( cd temp && unzip ../Tea.bar)
echo '<credentials name="DynamicCreds"><credential credentialName="jdbc::preProdCreds"><userId>xhs95598</userId><password>hxz57vc2km9^vxd9</password></credential></credentials>' > temp/test.credentialxml
mv PreProdPolicies temp/DefaultPolicies # Rename to match the server's expectation
( cd temp && zip -r ../_tea.bar *)

chmod 664 _tea.bar
# TEST_SSH_KEY_FILE  set as credentials in job
( tar -cf - _tea.bar ) | ssh -i $TEST_SSH_KEY_FILE -oStrictHostKeyChecking=no newuser@easterncape.hursley.ibm.com "cd /gsa/hurgsa/home/t/d/tdolby/public_html/bar && tar -xf -"
