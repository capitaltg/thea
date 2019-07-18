# Build web ui distro
FROM node:10.16.0-jessie as node
COPY thea-ui /tmp/
WORKDIR /tmp/thea-ui

RUN npm install --verbose
RUN npm run ng build -- --aot --output-hashing all --extract-css true --build-optimizer --vendor-chunk --prod --configuration=production

# Build API microservice
FROM gradle:5.4.1-jre8 as build

COPY . /home/gradle
COPY --from=node /tmp/dist/ /home/gradle/thea-ui/dist/

WORKDIR /home/gradle/
RUN gradle clean disttar -x :thea-ui:buildAngular

WORKDIR /
RUN tar xf /home/gradle/thea-api/build/distributions/thea-api.tar

# Create runtime image
FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.3_7

RUN adduser -D ctg
RUN mkdir /opt/ctg
RUN chown -R ctg /opt/ctg

USER ctg
COPY --chown=ctg --from=build /thea-api /opt/ctg/thea-api

EXPOSE 7070
ENTRYPOINT /opt/ctg/thea-api/bin/thea-api

