from backend.database.connection import get_connection


def get_count(cursor, query):
    cursor.execute(query)
    return cursor.fetchone()[0]


def test_record_counts():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        suppliers = get_count(
            cursor,
            "SELECT COUNT(*) FROM dbo.suppliers"
        )

        performance = get_count(
            cursor,
            "SELECT COUNT(*) FROM dbo.performance"
        )

        risk = get_count(
            cursor,
            "SELECT COUNT(*) FROM dbo.risk"
        )

        contracts = get_count(
            cursor,
            "SELECT COUNT(*) FROM dbo.contracts"
        )

        assert suppliers == 500
        assert performance == 500
        assert risk == 500
        assert contracts == 500

    finally:
        connection.close()


def test_supplier_ids_are_unique():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT
                COUNT(*) - COUNT(DISTINCT vendor_id)
            FROM dbo.suppliers
        """)

        duplicate_count = cursor.fetchone()[0]

        assert duplicate_count == 0

    finally:
        connection.close()


def test_performance_vendor_relationships():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT COUNT(*)
            FROM dbo.performance p
            LEFT JOIN dbo.suppliers s
                ON p.vendor_id = s.vendor_id
            WHERE s.vendor_id IS NULL
        """)

        orphan_count = cursor.fetchone()[0]

        assert orphan_count == 0

    finally:
        connection.close()


def test_risk_vendor_relationships():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT COUNT(*)
            FROM dbo.risk r
            LEFT JOIN dbo.suppliers s
                ON r.vendor_id = s.vendor_id
            WHERE s.vendor_id IS NULL
        """)

        orphan_count = cursor.fetchone()[0]

        assert orphan_count == 0

    finally:
        connection.close()


def test_contract_vendor_relationships():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT COUNT(*)
            FROM dbo.contracts c
            LEFT JOIN dbo.suppliers s
                ON c.vendor_id = s.vendor_id
            WHERE s.vendor_id IS NULL
        """)

        orphan_count = cursor.fetchone()[0]

        assert orphan_count == 0

    finally:
        connection.close()


def test_performance_scores_are_valid():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT COUNT(*)
            FROM dbo.performance
            WHERE quality_score < 0
               OR quality_score > 100
               OR sla_score < 0
               OR sla_score > 100
               OR delivery_score < 0
               OR delivery_score > 100
        """)

        invalid_count = cursor.fetchone()[0]

        assert invalid_count == 0

    finally:
        connection.close()


def test_delivery_values_are_valid():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT COUNT(*)
            FROM dbo.performance
            WHERE orders < 0
               OR on_time_deliveries < 0
               OR late_deliveries < 0
               OR on_time_deliveries + late_deliveries > orders
        """)

        invalid_count = cursor.fetchone()[0]

        assert invalid_count == 0

    finally:
        connection.close()


def test_risk_values_are_valid():
    connection = get_connection()

    try:
        cursor = connection.cursor()

        cursor.execute("""
            SELECT COUNT(*)
            FROM dbo.risk
            WHERE invoice_count < 0
               OR invoice_discrepancies < 0
               OR payment_delays < 0
               OR compliance_issues < 0
        """)

        invalid_count = cursor.fetchone()[0]

        assert invalid_count == 0

    finally:
        connection.close()