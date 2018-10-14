FROM clojure:lein

ADD ./ /project
WORKDIR /project
RUN lein uberjar

FROM java:8-alpine

COPY --from=0 /project/target/uberjar/rchain-scan.jar .
EXPOSE 3000

CMD ["java", "-jar", "./rchain-scan.jar"]
