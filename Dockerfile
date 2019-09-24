FROM openjdk:8-jdk-alpine

#properties
#ENV CERT_NAME amsdomain.crt
#ENV CERT_ALIAS_NAME amscas
ENV LANG en_US.UTF-8
ENV DS_PWD /opt/acp-be

RUN mkdir -p $DS_PWD/audience_data && \
    mkdir -p /tmp/acp-be
#RUN mkdir -p $DS_PWD/crt
#RUN mkdir -p /tmp/acp-be

WORKDIR $DS_PWD/

# copy packages directory
COPY ./target/app.jar $DS_PWD/app.jar
COPY ./init/tmp/CampaignTemplate.xlsx $DS_PWD/
#COPY ./amsdomain.crt /opt/acp-be/crt/

# CMD keytool -import -alias $CERT_ALIAS_NAME -file /opt/acp-be/crt/$CERT_NAME -noprompt -trustcacerts -keystore \
#    $JAVA_HOME/lib/security/cacerts -storepass changeit && java -jar app.jar

CMD java -jar app.jar