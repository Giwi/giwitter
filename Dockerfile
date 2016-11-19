FROM ubuntu:16.04
MAINTAINER Giwi Soft <giwi@free.fr>
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
RUN echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | tee /etc/apt/sources.list.d/mongodb-org-3.4.list
RUN apt-get update && apt-get install -y mongodb-org=2.6.9 mongodb-org-server=2.6.9 mongodb-org-shell=2.6.9 mongodb-org-mongos=2.6.9 mongodb-org-tools=2.6.9 openjdk-8-jre-headless
RUN mkdir -p /opt/giwitter
COPY docker/mongodb.sh /etc/init.d/.
RUN chmod 0755 /etc/init.d/mongodb.sh; mkdir -p /data/db; chmod 777 /data/db
COPY docker/entrypoint.sh /opt/.
RUN chmod +x /opt/entrypoint.sh
COPY application/Giwitter-fat.jar /opt/giwitter/.

RUN locale-gen fr_FR.UTF-8
ENV LANG fr_FR.UTF-8
ENV LANGUAGE fr_FR:fr
ENV LC_ALL fr_FR.UTF-8

EXPOSE 8080
EXPOSE 27017
VOLUME ["/data/db"]
CMD ["/opt/entrypoint.sh"]
