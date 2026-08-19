from backend.database.connection import get_connection


def test_azure_sql_connection():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("SELECT 1")

        result = cursor.fetchone()

        assert result[0] == 1

    finally:
        connection.close()