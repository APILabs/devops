FROM appconnect-docker-local.artifactory.swg-devops.com/iib/ace-minimal:11.0.0.5-ubuntu
#
# This image is used to run integration solutions in a cloud. Although this file is in
# a directory called ace-ci-integration-run, the build images are normally tagged with
# the name of the application itself (such as tea-app) and an app version number.
#
# Starting from ace-minimal (which has the ACE install plus the aceuser created), this
# image contains the deployed BAR files for a given application. Credentials are hard-
# coded at the moment, and should be moved to a secrets-management solution.
#

LABEL "maintainer"="tdolby@uk.ibm.com"

USER root


# Copy the DB2 client jar
WORKDIR /usr/local/db2-client
COPY db2jcc4.jar /usr/local/db2-client/
RUN chmod 664 /usr/local/db2-client/*


WORKDIR /
COPY deploy-bars.sh /tmp/
COPY bars /tmp/
RUN chown aceuser /tmp/*

USER aceuser

# We're in an internal pipeline
ENV LICENSE=accept

# Switch off the admin REST API for the server run, as we won't be deploying anything after start
RUN sed -i 's/#port: 7600/port: -1/g' /home/aceuser/ace-server/server.conf.yaml
RUN sed -i "s/#policyProject: 'DefaultPolicies'/policyProject: 'PreProdPolicies'/g" /home/aceuser/ace-server/server.conf.yaml

RUN /tmp/deploy-bars.sh /tmp

RUN bash -c "mqsisetdbparms -w /home/aceuser/ace-server -n jdbc::preProdCreds -u xhs95598 -p hxz57vc2km9^vxd9"

# Set entrypoint to run the server
ENTRYPOINT ["bash", "-c", "IntegrationServer -w /home/aceuser/ace-server"]
