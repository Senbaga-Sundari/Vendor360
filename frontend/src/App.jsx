import { useEffect, useState } from "react";
import "./App.css";

function App() {

  const [vendors, setVendors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {

    fetch("http://localhost:8080/api/vendors")
      .then((response) => {

        if (!response.ok) {
          throw new Error("Failed to fetch vendor data");
        }

        return response.json();
      })
      .then((data) => {

        console.log("Vendor Data:", data);

        setVendors(data);
        setLoading(false);
      })
      .catch((error) => {

        console.error(error);

        setError(error.message);
        setLoading(false);
      });

  }, []);


  return (

    <div className="app">

      {/* HEADER */}

      <header className="header">

        <h1>Vendor360</h1>

        <p>Integrated Vendor Management Dashboard</p>

      </header>


      {/* MAIN CONTENT */}

      <main className="container">

        <h2>Dashboard Overview</h2>


        {loading && (
          <p>Loading vendor data...</p>
        )}


        {error && (
          <p className="error">
            Error: {error}
          </p>
        )}


        {/* VENDOR DETAILS */}

        <h2>Vendor Details</h2>


        <div className="table-container">

          <table>

            <thead>

              <tr>

                <th>Vendor ID</th>
                <th>Vendor Name</th>
                <th>Category</th>
                <th>Country</th>
                <th>City</th>
                <th>Contract Value</th>
                <th>Quality Score</th>
                <th>SLA Score</th>
                <th>Delivery Score</th>
                <th>Risk Event</th>

              </tr>

            </thead>


            <tbody>

              {vendors.map((vendor) => (

                <tr key={vendor.vendorId}>

                  <td>{vendor.vendorId}</td>

                  <td>{vendor.vendorName}</td>

                  <td>{vendor.category}</td>

                  <td>{vendor.country}</td>

                  <td>{vendor.city}</td>

                  <td>
                    {vendor.currency} {vendor.contractValue}
                  </td>

                  <td>{vendor.qualityScore}</td>

                  <td>{vendor.slaScore}</td>

                  <td>{vendor.deliveryScore}</td>

                  <td>
                    {vendor.riskEvent || "No Risk"}
                  </td>

                </tr>

              ))}

            </tbody>

          </table>

        </div>

      </main>

    </div>

  );
}

export default App;