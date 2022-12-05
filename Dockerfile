From nginx:stable

WORKDIR /website

COPY . .

EXPOSE 80
