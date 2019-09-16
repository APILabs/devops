FROM appconnect-docker-local.artifactory.swg-devops.com/iib/ace-minimal:11.0.0.5-ubuntu

#
# This image is run by Jenkins in a Docker swarm (using ssh) and is used to run all of
# the Jenkins builds that implement the CI pipeline for this project.
#
# Starting from ace-minimal (which has the ACE install plus the aceuser created), this
# image contains the Jenkins pre-reqs and also the IBM cloud tools; not all stages are
# using the whole set of tools, but installing them all at once makes management easy.
#

LABEL "maintainer"="tdolby@uk.ibm.com"
USER root

# We're in an internal pipeline
ENV LICENSE=accept

# Prevent errors about having no terminal when using apt-get
ENV DEBIAN_FRONTEND noninteractive

RUN dpkg --add-architecture i386 \
  && apt-get update && apt-get install -y --no-install-recommends \
    apt-utils \
    apt-transport-https \
    bc \
    curl \
    ftp \
    maven \
    ant \
    git \
    gnupg-agent \
    iputils-ping \
    ksh \
    libxtst6 \
    lsb-release \
    locales \
    make \
    net-tools \
    nmap \
    openssh-server \
    patch \
    rsyslog \
    software-properties-common \
    sudo \
    telnet \
    tzdata \
    unzip \
    zip \
  && rm -rf /var/lib/apt/lists/*

ENV TZ=Europe/London

# Configure system
## add jenkins user and config
RUN groupadd -g 1010 jenkins \
  && adduser --disabled-password --gecos "" jenkins --uid 1010 --gid 1010 \
  && ( echo jenkins ; echo jenkins ) | passwd jenkins \
  && echo "jenkins ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers \
  && mkdir /var/run/sshd \
## allow users to log in
  && rm -fv /etc/nologin \
## fix to make sshd work on ubuntu (by default it doesn't)
  && sed -i 's/UsePAM .*/UsePAM no/g' /etc/ssh/sshd_config \
## set root password and allow root to ssh in (required for jenkins slave)
  && ( echo jenkins ; echo jenkins ) | passwd root \
  && sed -i 's/PermitRootLogin .*/PermitRootLogin yes/g' /etc/ssh/sshd_config \
  && sed -i 's/#PermitRootLogin .*/PermitRootLogin yes/g' /etc/ssh/sshd_config \
## set the timezone in the container
  && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
  && echo "ACEv11:" > /etc/debian_chroot \
## create syslog
  && touch /var/log/syslog \
  && chown syslog:adm /var/log/syslog \
## add in the mqbrkrs group
  && groupadd -f mqbrkrs \
  && usermod -a -G mqbrkrs jenkins \
## make registry path and chmod so non-root can set
  && mkdir -p /var/mqsi \
  && chmod 777 -R /var/mqsi \
## get the timezone and locales set correctly
  && locale-gen en_US.UTF-8 \
  && rm /etc/localtime \
  && ln -snf /usr/share/zoneinfo/Europe/London /etc/localtime \
  && dpkg-reconfigure -f noninteractive tzdata

WORKDIR /usr/local/db2-client
COPY db2jcc4.jar /usr/local/db2-client/
RUN chmod 664 /usr/local/db2-client/*

WORKDIR /

# Install the github commandline to enable release management.
RUN mkdir /tmp/hub && cd /tmp/hub \
    && curl -L https://github.com/github/hub/releases/download/v2.11.1/hub-linux-amd64-2.11.1.tgz | tar -xzf - \
    && /tmp/hub/hub-linux-amd64-2.11.1/install \
    && cd / && rm -rf /tmp/hub

# Download and install IBM cloud tools; must be run as Jenkins
# for plugins to be found during Jenkins builds. This step also
# installs docker and so we need to make sure the jenkins user
# is a member of the docker group.
RUN curl -sL https://ibm.biz/idt-installer -o /tmp/idt-installer.sh \
    && chmod 775 /tmp/idt-installer.sh \
    && groupadd -f docker \
    && usermod -a -G docker jenkins \
    && su - jenkins -c "/tmp/idt-installer.sh"

# Standard SSH port
EXPOSE 22

# Default command to run
ENTRYPOINT []
CMD ["/usr/sbin/sshd", "-D"]
