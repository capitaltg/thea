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

# Create runtime image
FROM capitaltg/slimj

RUN useradd ctg
RUN mkdir /opt/ctg
RUN chown -R ctg /opt/ctg

USER ctg
COPY --from=build /home/gradle/thea-api/build/distributions/thea-api.tar /tmp/thea.tar
RUN tar xf /tmp/thea.tar -C /opt/ctg/
RUN rm /tmp/thea.tar

EXPOSE 7070
ENTRYPOINT /opt/ctg/thea-api/bin/thea-api

