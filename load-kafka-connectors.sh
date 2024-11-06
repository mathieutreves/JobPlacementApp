#!/bin/bash

# Wait for Kafka Connect to start up
sleep 30

# Create CRM Postgres source connector
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "crm-postgres-source",
  "config" : {
  "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
  "database.user": "myuser",
  "database.dbname": "mydatabase",
  "database.hostname": "crm-postgres",
  "database.password": "secret",
  "database.port": "5432",
  "table.include.list": "public.message",
  "tasks.max": "1",
  "database.history.kafka.bootstrap.servers": "kafka:29092",
  "database.history.kafka.topic": "schema-changes.kafka",
  "database.server.name": "postgres",
  "plugin.name": "pgoutput",
  "key.converter.schemas.enable": "false",
  "topic.prefix": "kafka_postgres_",
  "value.converter.schemas.enable": "false",
  "value.converter": "org.apache.kafka.connect.json.JsonConverter",
  "key.converter": "org.apache.kafka.connect.storage.StringConverter",
  "snapshot.mode": "initial"
  }
}' http://kafka-connect:8083/connectors

# Create DS Postgres source connector
curl -X POST -H "Content-Type: application/json" -d '{
  "name": "ds-postgres-source",
  "config" : {
  "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
  "database.user": "myuser",
  "database.dbname": "mydatabase",
  "database.hostname": "ds-postgres",
  "database.password": "secret",
  "database.port": "5432",
  "tasks.max": "1",
  "database.history.kafka.bootstrap.servers": "kafka:29092",
  "database.history.kafka.topic": "schema-changes.kafka",
  "database.server.name": "postgres",
  "plugin.name": "pgoutput",
  "key.converter.schemas.enable": "false",
  "topic.prefix": "kafka_postgres_",
  "value.converter.schemas.enable": "false",
  "value.converter": "org.apache.kafka.connect.json.JsonConverter",
  "key.converter": "org.apache.kafka.connect.storage.StringConverter",
  "snapshot.mode": "initial"
  }
}' http://kafka-connect:8083/connectors