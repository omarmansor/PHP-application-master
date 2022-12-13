From devopsedu/webapp
RUN whoami
RUN pwd
COPY ./code/. /usr/share/nginx/html
