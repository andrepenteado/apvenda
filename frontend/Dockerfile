FROM nginx:alpine

ARG ENV_NAME
ARG MODULE_NAME

ENV ENV_NAME=${ENV_NAME}
ENV MODULE_NAME=${MODULE_NAME}

# Timezone
ENV TZ=America/Sao_Paulo
RUN ln -snf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone

COPY ./frontend/dist/${ENV_NAME}/browser/  /usr/share/nginx/html/${MODULE_NAME}/
COPY ./frontend/nginx.conf /etc/nginx/conf.d/default.conf

RUN sed -i "s|{{MODULE_NAME}}|${MODULE_NAME}|g" /etc/nginx/conf.d/default.conf

COPY ./frontend/docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

EXPOSE 80
