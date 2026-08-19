from pathlib import Path
import json
import math

import pandas as pd

from backend.database.connection import get_connection


# ============================================================
# Configuration
# ============================================================

DATASET_DIR = Path("datasets")


SUPPLIER_FILE = DATASET_DIR / "supplier_profiles.json"
PERFORMANCE_FILE = DATASET_DIR / "performance_records.csv"
RISK_FILE = DATASET_DIR / "risk_signals.csv"
CONTRACT_FILE = DATASET_DIR / "contracts.csv"


# ============================================================
# Helper functions
# ============================================================

def yes_no_to_bit(value):
    """
    Convert dataset Yes/No values to SQL Server BIT values.
    
    Yes -> 1
    No  -> 0
    Empty/None -> 0
    """
    if value is None:
        return 0

    value = str(value).strip().lower()

    if value == "yes":
        return 1

    return 0


def clean_nan(value):
    """
    Convert pandas NaN values to None so SQL Server receives NULL.
    """
    if value is None:
        return None

    if isinstance(value, float) and math.isnan(value):
        return None

    return value


# ============================================================
# Load suppliers
# ============================================================

def load_suppliers(connection):
    print("\nLoading suppliers...")

    with open(SUPPLIER_FILE, "r", encoding="utf-8") as file:
        suppliers = json.load(file)

    print(f"Supplier records found: {len(suppliers)}")

    sql = """
        INSERT INTO dbo.suppliers
        (
            vendor_id,
            vendor_name,
            category,
            country,
            city,
            contact_email,
            onboarding_date,
            contract_start_date,
            contract_end_date,
            criticality,
            sole_source,
            status
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    cursor = connection.cursor()

    for supplier in suppliers:

        cursor.execute(
            sql,
            supplier["vendor_id"],
            supplier["vendor_name"],
            supplier["category"],
            supplier["country"],
            supplier["city"],
            supplier["contact_email"],
            supplier["onboarding_date"],
            supplier["contract_start_date"],
            supplier["contract_end_date"],
            supplier["criticality"],
            yes_no_to_bit(supplier["sole_source"]),
            supplier["status"],
        )

    connection.commit()

    print(f"Suppliers inserted: {len(suppliers)}")


# ============================================================
# Load performance
# ============================================================

def load_performance(connection):
    print("\nLoading performance records...")

    df = pd.read_csv(PERFORMANCE_FILE)

    print(f"Performance records found: {len(df)}")

    sql = """
        INSERT INTO dbo.performance
        (
            performance_id,
            vendor_id,
            period,
            orders,
            on_time_deliveries,
            late_deliveries,
            quality_score,
            sla_score,
            delivery_score
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    cursor = connection.cursor()

    for _, row in df.iterrows():

        cursor.execute(
            sql,
            row["performance_id"],
            row["vendor_id"],
            row["period"],
            int(row["orders"]),
            int(row["on_time_deliveries"]),
            int(row["late_deliveries"]),
            float(row["quality_score"]),
            float(row["sla_score"]),
            float(row["delivery_score"]),
        )

    connection.commit()

    print(f"Performance records inserted: {len(df)}")


# ============================================================
# Load risk
# ============================================================

def load_risk(connection):
    print("\nLoading risk signals...")

    df = pd.read_csv(RISK_FILE)

    print(f"Risk records found: {len(df)}")

    sql = """
        INSERT INTO dbo.risk
        (
            risk_id,
            vendor_id,
            invoice_count,
            invoice_discrepancies,
            payment_delays,
            compliance_issues,
            critical_dependency,
            risk_event,
            risk_date
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    cursor = connection.cursor()

    for _, row in df.iterrows():

        cursor.execute(
            sql,
            row["risk_id"],
            row["vendor_id"],
            int(row["invoice_count"]),
            int(row["invoice_discrepancies"]),
            int(row["payment_delays"]),
            int(row["compliance_issues"]),
            yes_no_to_bit(row["critical_dependency"]),
            clean_nan(row["risk_event"]),
            row["risk_date"],
        )

    connection.commit()

    print(f"Risk records inserted: {len(df)}")


# ============================================================
# Load contracts
# ============================================================

def load_contracts(connection):
    print("\nLoading contracts...")

    df = pd.read_csv(CONTRACT_FILE)

    print(f"Contract records found: {len(df)}")

    sql = """
        INSERT INTO dbo.contracts
        (
            contract_id,
            vendor_id,
            contract_type,
            contract_value,
            currency,
            payment_terms_days,
            renewal_notice_days,
            contract_status
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """

    cursor = connection.cursor()

    for _, row in df.iterrows():

        cursor.execute(
            sql,
            row["contract_id"],
            row["vendor_id"],
            row["contract_type"],
            float(row["contract_value"]),
            row["currency"],
            int(row["payment_terms_days"]),
            int(row["renewal_notice_days"]),
            row["contract_status"],
        )

    connection.commit()

    print(f"Contract records inserted: {len(df)}")


# ============================================================
# Main
# ============================================================

def main():

    print("=" * 60)
    print("Vendor360 Dataset Loader")
    print("=" * 60)

    connection = None

    try:

        # ----------------------------------------------------
        # Connect to Azure SQL
        # ----------------------------------------------------

        print("\nConnecting to Azure SQL...")

        connection = get_connection()

        print("Azure SQL connection successful.")

        # ----------------------------------------------------
        # Load datasets
        # ----------------------------------------------------

        load_suppliers(connection)

        load_performance(connection)

        load_risk(connection)

        load_contracts(connection)

        print("\n" + "=" * 60)
        print("DATA LOADING COMPLETED SUCCESSFULLY")
        print("=" * 60)

    except Exception as error:

        print("\nERROR:")
        print(error)

        if connection:
            connection.rollback()

        raise

    finally:

        if connection:
            connection.close()

        print("\nDatabase connection closed.")


if __name__ == "__main__":
    main()