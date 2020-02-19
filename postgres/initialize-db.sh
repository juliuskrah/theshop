#!/bin/bash

set -e
set -u

function create_user_and_database() {
	local database=$1
	echo "  Creating user and database '$database'"
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	    CREATE USER $database WITH PASSWORD '$database';
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $database;
		\c $database;
		CREATE SCHEMA IF NOT EXISTS tenants AUTHORIZATION $database;
		CREATE SCHEMA IF NOT EXISTS baskets AUTHORIZATION $database;
		CREATE SCHEMA IF NOT EXISTS catalogs AUTHORIZATION $database;
		CREATE SCHEMA IF NOT EXISTS locations AUTHORIZATION $database;
		CREATE SCHEMA IF NOT EXISTS marketing AUTHORIZATION $database;
		CREATE SCHEMA IF NOT EXISTS orders AUTHORIZATION $database;
		CREATE SCHEMA IF NOT EXISTS payments AUTHORIZATION $database;
        CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
	EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
	for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
		create_user_and_database $db
	done
	echo "Multiple databases created"
fi
