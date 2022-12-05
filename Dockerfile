From nginx:stable
ADD ./website/. /usr/share/nginx/html
EXPOSE 80
