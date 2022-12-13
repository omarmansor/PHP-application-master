From devopsedu/webapp
RUN whoami
RUN pwd
COPY ./code/. /var/www/html
# COPY ./code/. /usr/share/nginx/html
# COPY ./site.conf /etc/nginx/conf.d/default.conf