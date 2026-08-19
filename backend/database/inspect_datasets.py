from pathlib import Path
import pandas as pd
import json


DATASET_DIR = Path("datasets")


def inspect_csv(file_path):
    print("\n" + "=" * 60)
    print(f"FILE: {file_path}")
    print("=" * 60)

    df = pd.read_csv(file_path)

    print(f"Records : {len(df)}")
    print(f"Columns : {len(df.columns)}")
    print("\nColumns:")
    
    for column in df.columns:
        print(f"  - {column}")

    print("\nFirst 3 records:")
    print(df.head(3).to_string(index=False))


def inspect_json(file_path):
    print("\n" + "=" * 60)
    print(f"FILE: {file_path}")
    print("=" * 60)

    with open(file_path, "r", encoding="utf-8") as file:
        data = json.load(file)

    if isinstance(data, list):
        print(f"Records : {len(data)}")

        if data:
            print("\nColumns:")
            for column in data[0].keys():
                print(f"  - {column}")

            print("\nFirst 3 records:")
            for record in data[:3]:
                print(record)

    elif isinstance(data, dict):
        print("JSON root object")
        print("Keys:")
        for key in data.keys():
            print(f"  - {key}")


def main():
    print("\nVendor360 Dataset Inspection")
    print("=" * 60)

    for file_path in DATASET_DIR.glob("*.csv"):
        inspect_csv(file_path)

    for file_path in DATASET_DIR.glob("*.json"):
        inspect_json(file_path)


if __name__ == "__main__":
    main()