FROM alpine:3.9
RUN apk --no-cache add curl
RUN curl -LJO "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.0.1/opentelemetry-javaagent-all.jar" -o opentelemetry-javaagent-all.jar

FROM alpine:3.9
RUN mkdir /usr/agent
COPY --from=0 opentelemetry-javaagent-all.jar /usr/agent