FROM amazoncorretto:25-alpine

ARG MODULE_NAME

WORKDIR /app

# Timezone
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone

COPY backend/target/${MODULE_NAME}.jar /app/backend.jar

ENTRYPOINT [ "java", "-jar", "/app/backend.jar" ]

EXPOSE 8080
