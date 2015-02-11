FROM java:openjdk-8-jre

# Add the uberjar.
ADD target/sprint-poker-*-standalone.jar /root/

# Set the working directory and exposed ports.
WORKDIR /root
EXPOSE 3000

# Set environment variables for the app.
ENV PORT 3000
ENV LEIN_NO_DEV true

# Start the app.
CMD java -jar /root/sprint-poker-*-standalone.jar
