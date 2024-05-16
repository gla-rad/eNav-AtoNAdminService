# Build like this:
#     docker build -t <version> -f Dockerfile ..
# e.g.
#     docker build -t glarad/enav-aton-service:latest -f Dockerfile ..
#
# Run like this:
#     sudo docker run -t -i --rm -p 8766:8766 -v /path/to/config-directory/on/machine:/conf <image-id>
#
# You can also push to docker-hub like:
#     docker push glarad/enav-aton-service:tagname
#
# A customized conf file (application.properties) can be made available in the folder mounted to /conf.
# When using in non-local environment it is recommended to generate new trust and keystores and place them in
# the conf-folder and point to them in application.properties.
#

# Use the official OpenJDK 21 image as the base image
FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.source="https://github.com/gla-rad/eNav-AtoNService"

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/aton-service-0.0.5-SNAPSHOT.jar .

# Create a modules path just in case
ENV LOADER_PATH /modules
RUN mkdir $LOADER_PATH

# Set the environment variable to activate the "prod" profile
ENV SPRING_PROFILES_ACTIVE=prod

# Define a variable for the configuration directory
ENV CONFIG_DIR=/conf

EXPOSE 8766

CMD ["java", "-jar", "aton-service-0.0.5-SNAPSHOT.jar", "--spring.config.location=optional:classpath:/,optional:file:${CONFIG_DIR}/"]
