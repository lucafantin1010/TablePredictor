# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the WAR file
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Tomcat
FROM tomcat:10.1-jdk21

# Remove default Tomcat applications to keep it clean
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the generated WAR file from the build stage
# Naming it ROOT.war makes it available at the root URL (/)
COPY --from=build /app/target/TablePredictor-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]