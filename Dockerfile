# Build web ui distro
FROM node:12.22.10 as node

COPY thea-ui/package.json /tmp/
WORKDIR /tmp/
RUN npm install --verbose

COPY thea-ui /tmp/
RUN npm run ng build -- --aot --output-hashing all --extract-css true --build-optimizer --vendor-chunk --prod --configuration=production

# Build API microservice
FROM gradle:7.4.0-jdk11 as build

COPY . /home/gradle
COPY --from=node /tmp/dist/ /home/gradle/thea-ui/dist/

WORKDIR /home/gradle/
RUN gradle clean build -x :thea-ui:buildAngular

# Create runtime image
FROM adoptopenjdk/openjdk11:jre-11.0.14.1_1-alpine

RUN adduser -D ctg
RUN mkdir /opt/ctg
RUN chown -R ctg:ctg /opt/ctg

USER ctg
COPY --chown=ctg --from=build /home/gradle/thea-api/build/libs/thea-api.jar /opt/ctg/thea-api.jar

EXPOSE 8080
ENTRYPOINT java -jar /opt/ctg/thea-api.jar

