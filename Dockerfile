FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/rchain-scan.jar /rchain-scan/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/rchain-scan/app.jar"]
