# Build web ui distro
FROM node:10.16.0-jessie as node

COPY thea-ui/package.json /tmp/
WORKDIR /tmp/thea-ui
RUN npm install --verbose

COPY thea-ui /tmp/
RUN npm run ng build -- --aot --output-hashing all --extract-css true --build-optimizer --vendor-chunk --prod --configuration=production

# Build API microservice
FROM gradle:5.4.1-jre8 as build

COPY . /home/gradle
COPY --from=node /tmp/dist/ /home/gradle/thea-ui/dist/

WORKDIR /home/gradle/
RUN gradle clean build -x :thea-ui:buildAngular

# Create runtime image
FROM adoptopenjdk/openjdk11:jre-11.0.4_11-alpine

RUN adduser -D ctg
RUN mkdir /opt/ctg
RUN chown -R ctg:ctg /opt/ctg

USER ctg
COPY --chown=ctg --from=build /home/gradle/thea-api/build/libs/thea-api.jar /opt/ctg/thea-api.jar

EXPOSE 8080
ENTRYPOINT java -jar /opt/ctg/thea-api.jar

