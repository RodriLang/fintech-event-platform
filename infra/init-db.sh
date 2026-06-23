#!/bin/bash
set -e

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
    echo "Iniciando la creación dinámica de múltiples bases de datos..."

    # Reemplazamos las comas por espacios para poder iterar en el bucle for
    for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
        echo "Creando base de datos: '$db'"

        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_USER" <<-EOSQL
            CREATE DATABASE $db;
            GRANT ALL PRIVILEGES ON DATABASE $db TO "$POSTGRES_USER";
EOSQL
    done

    echo "Todas las bases de datos fueron creadas exitosamente."
fi