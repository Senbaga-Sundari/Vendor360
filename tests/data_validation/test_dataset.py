import json
from pathlib import Path

import pandas as pd


# ============================================================
# Vendor360 Dataset Validation
# ============================================================

# Project root:
# C:\Projects\Vendor360
PROJECT_ROOT = Path(__file__).resolve().parents[2]

# Dataset directory:
# C:\Projects\Vendor360\datasets
DATASET_DIR = PROJECT_ROOT / "datasets"


# Dataset files
SUPPLIER_FILE = DATASET_DIR / "supplier_profiles.json"
PERFORMANCE_FILE = DATASET_DIR / "performance_records.csv"
RISK_FILE = DATASET_DIR / "risk_signals.csv"
CONTRACT_FILE = DATASET_DIR / "contracts.csv"


# ============================================================
# Helper Functions
# ============================================================

def load_supplier_data():
    """Load supplier profiles from JSON."""
    with open(SUPPLIER_FILE, "r", encoding="utf-8") as file:
        return json.load(file)


def load_performance_data():
    """Load performance records from CSV."""
    return pd.read_csv(PERFORMANCE_FILE)


def load_risk_data():
    """Load risk signals from CSV."""
    return pd.read_csv(RISK_FILE)


def load_contract_data():
    """Load contracts from CSV."""
    return pd.read_csv(CONTRACT_FILE)


# ============================================================
# 1. RECORD COUNT VALIDATION
# ============================================================

def test_supplier_record_count():
    suppliers = load_supplier_data()

    assert len(suppliers) == 500


def test_performance_record_count():
    performance = load_performance_data()

    assert len(performance) == 500


def test_risk_record_count():
    risk = load_risk_data()

    assert len(risk) == 500


def test_contract_record_count():
    contracts = load_contract_data()

    assert len(contracts) == 500


# ============================================================
# 2. VENDOR ID VALIDATION
# ============================================================

def test_supplier_vendor_ids_are_unique():
    suppliers = load_supplier_data()

    vendor_ids = [
        supplier["vendor_id"]
        for supplier in suppliers
    ]

    assert len(vendor_ids) == len(set(vendor_ids))


def test_supplier_vendor_ids_are_not_empty():
    suppliers = load_supplier_data()

    for supplier in suppliers:
        assert supplier["vendor_id"] is not None
        assert str(supplier["vendor_id"]).strip() != ""


# ============================================================
# 3. RELATIONSHIP VALIDATION
# ============================================================

def test_performance_vendor_ids_exist():
    suppliers = load_supplier_data()
    performance = load_performance_data()

    supplier_vendor_ids = {
        supplier["vendor_id"]
        for supplier in suppliers
    }

    performance_vendor_ids = set(
        performance["vendor_id"]
    )

    invalid_vendor_ids = (
        performance_vendor_ids - supplier_vendor_ids
    )

    assert invalid_vendor_ids == set()


def test_risk_vendor_ids_exist():
    suppliers = load_supplier_data()
    risk = load_risk_data()

    supplier_vendor_ids = {
        supplier["vendor_id"]
        for supplier in suppliers
    }

    risk_vendor_ids = set(
        risk["vendor_id"]
    )

    invalid_vendor_ids = (
        risk_vendor_ids - supplier_vendor_ids
    )

    assert invalid_vendor_ids == set()


def test_contract_vendor_ids_exist():
    suppliers = load_supplier_data()
    contracts = load_contract_data()

    supplier_vendor_ids = {
        supplier["vendor_id"]
        for supplier in suppliers
    }

    contract_vendor_ids = set(
        contracts["vendor_id"]
    )

    invalid_vendor_ids = (
        contract_vendor_ids - supplier_vendor_ids
    )

    assert invalid_vendor_ids == set()


# ============================================================
# 4. SUPPLIER SCHEMA VALIDATION
# ============================================================

def test_supplier_required_fields():
    suppliers = load_supplier_data()

    required_fields = {
        "vendor_id",
        "vendor_name",
        "category",
        "country",
        "city",
        "contact_email",
        "onboarding_date",
        "contract_start_date",
        "contract_end_date",
        "criticality",
        "sole_source",
        "status"
    }

    actual_fields = set(suppliers[0].keys())

    assert required_fields.issubset(actual_fields)


# ============================================================
# 5. PERFORMANCE SCHEMA VALIDATION
# ============================================================

def test_performance_required_columns():
    performance = load_performance_data()

    required_columns = {
        "performance_id",
        "vendor_id",
        "period",
        "orders",
        "on_time_deliveries",
        "late_deliveries",
        "quality_score",
        "sla_score",
        "delivery_score"
    }

    assert required_columns.issubset(
        set(performance.columns)
    )


# ============================================================
# 6. RISK SCHEMA VALIDATION
# ============================================================

def test_risk_required_columns():
    risk = load_risk_data()

    required_columns = {
        "risk_id",
        "vendor_id",
        "invoice_count",
        "invoice_discrepancies",
        "payment_delays",
        "compliance_issues",
        "critical_dependency",
        "risk_event",
        "risk_date"
    }

    assert required_columns.issubset(
        set(risk.columns)
    )


# ============================================================
# 7. CONTRACT SCHEMA VALIDATION
# ============================================================

def test_contract_required_columns():
    contracts = load_contract_data()

    required_columns = {
        "contract_id",
        "vendor_id",
        "contract_type",
        "contract_value",
        "currency",
        "payment_terms_days",
        "renewal_notice_days",
        "contract_status"
    }

    assert required_columns.issubset(
        set(contracts.columns)
    )


# ============================================================
# 8. AI SCENARIO VALIDATION
# ============================================================

def test_high_performing_vendor_count():
    suppliers = load_supplier_data()

    high_performing = [
        supplier
        for supplier in suppliers
        if supplier["vendor_id"] <= "V167"
    ]

    assert len(high_performing) == 167


def test_at_risk_vendor_count():
    suppliers = load_supplier_data()

    at_risk = [
        supplier
        for supplier in suppliers
        if "V168" <= supplier["vendor_id"] <= "V334"
    ]

    assert len(at_risk) == 167


def test_critical_vendor_count():
    suppliers = load_supplier_data()

    critical = [
        supplier
        for supplier in suppliers
        if supplier["sole_source"] == "Yes"
    ]

    assert len(critical) == 166


# ============================================================
# 9. PERFORMANCE VALUE VALIDATION
# ============================================================

def test_performance_scores_are_valid():
    performance = load_performance_data()

    assert performance["quality_score"].between(0, 100).all()
    assert performance["sla_score"].between(0, 100).all()
    assert performance["delivery_score"].between(0, 100).all()


def test_order_counts_are_valid():
    performance = load_performance_data()

    assert (performance["orders"] >= 0).all()
    assert (performance["on_time_deliveries"] >= 0).all()
    assert (performance["late_deliveries"] >= 0).all()


# ============================================================
# 10. RISK VALUE VALIDATION
# ============================================================

def test_risk_values_are_valid():
    risk = load_risk_data()

    assert (risk["invoice_count"] >= 0).all()
    assert (risk["invoice_discrepancies"] >= 0).all()
    assert (risk["payment_delays"] >= 0).all()
    assert (risk["compliance_issues"] >= 0).all()


def test_critical_dependency_values():
    risk = load_risk_data()

    allowed_values = {"Yes", "No"}

    assert set(
        risk["critical_dependency"].unique()
    ).issubset(allowed_values)