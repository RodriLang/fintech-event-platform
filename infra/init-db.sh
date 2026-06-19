#!/bin/bash
set -e

echo "Iniciando la creación de múltiples bases de datos..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE payment_db;
    CREATE DATABASE fraud_db;

    GRANT ALL PRIVILEGES ON DATABASE payment_db TO "$POSTGRES_USER";
    GRANT ALL PRIVILEGES ON DATABASE fraud_db TO "$POSTGRES_USER";
EOSQL

echo "Bases de datos creadas exitosamente."