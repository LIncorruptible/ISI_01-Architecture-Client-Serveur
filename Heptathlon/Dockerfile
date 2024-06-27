FROM mysql/mysql-server:8.0

RUN apt-get update && apt-get install -y gettext-base

COPY ./initdb/entrypoint.sh /docker-entrypoint-initdb.d/entrypoint.sh