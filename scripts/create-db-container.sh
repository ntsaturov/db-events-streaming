#!/usr/bin/env bash

docker run --name postgres-test \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=postgres \
  -e POSTGRES_USER=postgres \
  -p 5432:5432 \
  -v $(realpath ./db-data):/var/lib/postgresql/data \
  -v $(realpath ./db/test_table.sql):/docker-entrypoint-initdb.d/init.sql \
  -d \
  postgres
