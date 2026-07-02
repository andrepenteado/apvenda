#!/bin/sh

echo "Iniciando frontend $MODULE_NAME"

envsubst < /usr/share/nginx/html/$MODULE_NAME/assets/config/init.template.js > /usr/share/nginx/html/$MODULE_NAME/assets/config/init.js

sed -i "s|init.js|init.js?v=$(date +%s)|g" /usr/share/nginx/html/$MODULE_NAME/index.html

exec nginx -g 'daemon off;'
