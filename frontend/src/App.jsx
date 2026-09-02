import { useEffect, useState } from 'react'
import VendorAIAssessment from "./components/VendorAIAssessment";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
} from 'recharts'

import './App.css'

function App() {
  const [vendors, setVendors] = useState([])
  const [dashboard, setDashboard] = useState(null)
  const [error, setError] = useState(null)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
const [selectedCountry, setSelectedCountry] = useState('')
const [highRiskOnly, setHighRiskOnly] = useState(false)
const [currentPage, setCurrentPage] = useState(1)

const vendorsPerPage = 10

  useEffect(() => {
    fetch('http://localhost:8080/api/vendors')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch vendors')
        }

        return response.json()
      })
      .then((data) => {
        setVendors(data)
      })
      .catch((error) => {
        setError(error.message)
      })

    fetch('http://localhost:8080/api/vendors/dashboard')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch dashboard')
        }

        return response.json()
      })
      .then((data) => {
        setDashboard(data)
      })
      .catch((error) => {
        setError(error.message)
      })
  }, [])

  // Show only first 10 vendors in chart
  const chartData = vendors.slice(0, 10).map((vendor) => ({
    vendor: vendor.vendorName,
    quality: Number(vendor.qualityScore),
    sla: Number(vendor.slaScore),
    delivery: Number(vendor.deliveryScore),
  }))

  const categoryData = Object.values(
  vendors.reduce((acc, vendor) => {

    const category = vendor.category || 'Unknown'

    if (!acc[category]) {
      acc[category] = {
        name: category,
        value: 0,
      }
    }

    acc[category].value += 1

    return acc

  }, {})
)

const COLORS = [
  '#3b5998',
  '#4caf50',
  '#ff9800',
  '#f44336',
  '#9c27b0',
  '#00acc1',
  '#795548',
]

const filteredVendors = vendors.filter((vendor) => {

  const search = searchTerm.toLowerCase()

  const matchesSearch =
    vendor.vendorId?.toLowerCase().includes(search) ||
    vendor.vendorName?.toLowerCase().includes(search) ||
    vendor.category?.toLowerCase().includes(search) ||
    vendor.country?.toLowerCase().includes(search) ||
    vendor.city?.toLowerCase().includes(search)

  const matchesCategory =
    !selectedCategory ||
    vendor.category === selectedCategory

  const matchesCountry =
    !selectedCountry ||
    vendor.country === selectedCountry

  const matchesRisk =
    !highRiskOnly ||
    (
      vendor.riskEvent &&
      vendor.riskEvent !== 'None' &&
      vendor.riskEvent !== 'NULL'
    )

  return (
    matchesSearch &&
    matchesCategory &&
    matchesCountry &&
    matchesRisk
  )
})

const categories = [
  ...new Set(
    vendors
      .map((vendor) => vendor.category)
      .filter(Boolean)
  ),
]

const countries = [
  ...new Set(
    vendors
      .map((vendor) => vendor.country)
      .filter(Boolean)
  ),
]

useEffect(() => {
  setCurrentPage(1)
}, [
  searchTerm,
  selectedCategory,
  selectedCountry,
  highRiskOnly,
])

const totalPages = Math.ceil(
  filteredVendors.length / vendorsPerPage
)

const startIndex =
  (currentPage - 1) * vendorsPerPage

const endIndex =
  startIndex + vendorsPerPage

const paginatedVendors =
  filteredVendors.slice(
    startIndex,
    endIndex
  )

  const riskEventData = Object.entries(
  vendors.reduce((acc, vendor) => {

    const riskEvent =
      vendor.riskEvent &&
      vendor.riskEvent !== "None"
        ? vendor.riskEvent
        : "No Risk Event";

    acc[riskEvent] = (acc[riskEvent] || 0) + 1;

    return acc;

  }, {})
).map(([name, value]) => ({
  name,
  value,
}));

const highRiskVendors = vendors
  .map((vendor) => {

    let riskScore = 0;

    // Invoice discrepancies
    riskScore += (vendor.invoiceDiscrepancies || 0) * 2;

    // Payment delays
    riskScore += (vendor.paymentDelays || 0) * 3;

    // Compliance issues
    riskScore += (vendor.complianceIssues || 0) * 5;

    // Critical dependency
    if (vendor.criticalDependency === true) {
      riskScore += 10;
    }

    // Risk event
    if (
      vendor.riskEvent &&
      vendor.riskEvent !== "None"
    ) {
      riskScore += 5;
    }

    return {
      ...vendor,
      riskScore,
    };
  })

  .sort((a, b) => b.riskScore - a.riskScore)

  .slice(0, 10);



  return (
    <>
      {/* HEADER */}
      <header className="header">
        <h1>Vendor360</h1>
        <p>Integrated Vendor Management Dashboard</p>
      </header>

      <main className="main-content">

     <section className="dashboard-section">
        {/* DASHBOARD SUMMARY */}
        <h2>Dashboard Overview</h2>

        {error && (
          <p className="error">
            Error: {error}
          </p>
        )}

        <div className="cards">
 {/* TOTAL VENDORS */}

            <div className="card card-blue">

              <h3>Total Vendors</h3>

              <p>

                {dashboard
                  ? dashboard.totalVendors
                  : 'Loading...'}

              </p>

            </div>



            {/* ACTIVE CONTRACTS */}

            <div className="card card-green">

              <h3>Active Contracts</h3>

              <p>

                {dashboard
                  ? dashboard.activeContracts
                  : 'Loading...'}

              </p>

            </div>



            {/* HIGH RISK VENDORS */}

            <div className="card card-red">

              <h3>High Risk Vendors</h3>

              <p>

                {dashboard
                  ? dashboard.highRiskVendors
                  : 'Loading...'}

              </p>

            </div>



            {/* CRITICAL DEPENDENCIES */}

            <div className="card card-orange">

              <h3>Critical Dependencies</h3>

              <p>

                {dashboard
                  ? dashboard.criticalDependencies
                  : 'Loading...'}

              </p>

            </div>



            {/* AVERAGE QUALITY SCORE */}

            <div className="card card-purple">

              <h3>Average Quality Score</h3>

              <p>

                {dashboard &&
                dashboard.averageQualityScore != null

                  ? Number(
                      dashboard.averageQualityScore
                    ).toFixed(2)

                  : 'Loading...'}

              </p>

            </div>



            {/* AVERAGE SLA SCORE */}

            <div className="card card-blue">

              <h3>Average SLA Score</h3>

              <p>

                {dashboard &&
                dashboard.averageSlaScore != null

                  ? Number(
                      dashboard.averageSlaScore
                    ).toFixed(2)

                  : 'Loading...'}

              </p>

            </div>



            {/* AVERAGE DELIVERY SCORE */}

            <div className="card card-green">

              <h3>Average Delivery Score</h3>

              <p>

                {dashboard &&
                dashboard.averageDeliveryScore != null

                  ? Number(
                      dashboard.averageDeliveryScore
                    ).toFixed(2)

                  : 'Loading...'}

              </p>

            </div>
            
            </div>
</section>

<div className="risk-table-card">

  <h2>🚨 Top 10 High-Risk Vendors</h2>

  <div className="table-container">

    <table>

      <thead>
        <tr>
          <th>Rank</th>
          <th>Vendor ID</th>
          <th>Vendor Name</th>
          <th>Risk Event</th>
          <th>Invoice Issues</th>
          <th>Payment Delays</th>
          <th>Compliance Issues</th>
          <th>Critical Dependency</th>
          <th>Risk Score</th>
          <th>AI Analysis</th>
        </tr>
      </thead>

      <tbody>

        {highRiskVendors.map((vendor, index) => (

          <tr key={vendor.vendorId}>

            <td>{index + 1}</td>

            <td>{vendor.vendorId}</td>

            <td>{vendor.vendorName}</td>

            <td>
              {vendor.riskEvent &&
              vendor.riskEvent !== "None"
                ? vendor.riskEvent
                : "No Risk Event"}
            </td>

            <td>
              {vendor.invoiceDiscrepancies || 0}
            </td>

            <td>
              {vendor.paymentDelays || 0}
            </td>

            <td>
              {vendor.complianceIssues || 0}
            </td>

            <td>
              {vendor.criticalDependency
                ? "Yes"
                : "No"}
            </td>

            <td>
              <strong>
                {vendor.riskScore}
              </strong>
            </td>

            <td>

  <VendorAIAssessment
    vendorId={vendor.vendorId}
  />

</td>

          </tr>

        ))}

      </tbody>

    </table>

  </div>

</div>

        {/* PERFORMANCE CHART */}
        <h2 className="section-title">
          Vendor Performance Comparison
        </h2>

        <div className="chart-container">

          <ResponsiveContainer width="100%" height={400}>

            <BarChart data={chartData}>

              <CartesianGrid strokeDasharray="3 3" />

              <XAxis
                dataKey="vendor"
              />

              <YAxis
                domain={[0, 100]}
              />

              <Tooltip />

              <Legend />

              <Bar
                dataKey="quality"
                name="Quality Score"
              />

              <Bar
                dataKey="sla"
                name="SLA Score"
              />

              <Bar
                dataKey="delivery"
                name="Delivery Score"
              />

            </BarChart>

          </ResponsiveContainer>

          <div className="chart-card">

  <h2>Risk Event Distribution</h2>

  <ResponsiveContainer width="100%" height={350}>

    <PieChart>

      <Pie
        data={riskEventData}
        dataKey="value"
        nameKey="name"
        cx="50%"
        cy="50%"
        outerRadius={120}
        label
      >
        {riskEventData.map((entry, index) => (

          <Cell key={`cell-${index}`} 
          fill={COLORS[index % COLORS.length]}
           />
        ))}
      </Pie>

      <Tooltip />

      <Legend />

    </PieChart>

  </ResponsiveContainer>

</div>

        </div>


        {/* CATEGORY DISTRIBUTION */}

<h2 className="section-title">
  Vendor Category Distribution
</h2>

<div className="chart-container">

  <ResponsiveContainer width="100%" height={400}>

    <PieChart>

      <Pie
        data={categoryData}
        cx="50%"
        cy="50%"
        labelLine={false}
        label={({ name, value }) =>
          `${name}: ${value}`
        }
        outerRadius={140}
        fill="#8884d8"
        dataKey="value"
      >

        {categoryData.map((entry, index) => (

          <Cell
            key={`cell-${index}`}
            fill={COLORS[index % COLORS.length]}
          />

        ))}

      </Pie>

      <Tooltip />

      <Legend />

    </PieChart>

  </ResponsiveContainer>

</div>


        {/* VENDOR TABLE */}
<div className="vendor-header">

  <h2>Vendor Details</h2>

  <div className="filters">

    {/* SEARCH */}

    <input
      type="text"
      placeholder="Search vendors..."
      value={searchTerm}
      onChange={(e) =>
        setSearchTerm(e.target.value)
      }
      className="search-box"
    />


    {/* CATEGORY FILTER */}

    <select
      value={selectedCategory}
      onChange={(e) =>
        setSelectedCategory(e.target.value)
      }
    >

      <option value="">
        All Categories
      </option>

      {categories.map((category) => (

        <option
          key={category}
          value={category}
        >
          {category}
        </option>

      ))}

    </select>


    {/* COUNTRY FILTER */}

    <select
      value={selectedCountry}
      onChange={(e) =>
        setSelectedCountry(e.target.value)
      }
    >

      <option value="">
        All Countries
      </option>

      {countries.map((country) => (

        <option
          key={country}
          value={country}
        >
          {country}
        </option>

      ))}

    </select>


    {/* HIGH RISK FILTER */}

    <label className="risk-filter">

      <input
        type="checkbox"
        checked={highRiskOnly}
        onChange={(e) =>
          setHighRiskOnly(e.target.checked)
        }
      />

      High Risk Only

    </label>


    {/* RESET BUTTON */}

    <button
      className="reset-button"
      onClick={() => {

        setSearchTerm('')
        setSelectedCategory('')
        setSelectedCountry('')
        setHighRiskOnly(false)

      }}
    >

      Reset

    </button>

  </div>

</div>

<p className="result-count">

  Showing{' '}

  {filteredVendors.length === 0
    ? 0
    : startIndex + 1}

  {' '}–{' '}

  {Math.min(
    endIndex,
    filteredVendors.length
  )}

  {' '}of{' '}

  {filteredVendors.length}

  {' '}vendors

</p>

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

              {paginatedVendors.map((vendor) => (

                <tr key={vendor.vendorId}>

                  <td>{vendor.vendorId}</td>

                  <td>{vendor.vendorName}</td>

                  <td>{vendor.category}</td>

                  <td>{vendor.country}</td>

                  <td>{vendor.city}</td>

                  <td>
                    {vendor.currency}{' '}
                    {vendor.contractValue}
                  </td>

                  <td>{vendor.qualityScore}</td>

                  <td>{vendor.slaScore}</td>

                  <td>{vendor.deliveryScore}</td>

                  <td>
                    {vendor.riskEvent || 'No Risk'}
                  </td>

                </tr>

              ))}

            </tbody>

          </table>
          <div className="pagination">

  <button
    onClick={() =>
      setCurrentPage((page) =>
        Math.max(page - 1, 1)
      )
    }
    disabled={currentPage === 1}
  >
    Previous
  </button>


  <span>
    Page {currentPage} of {totalPages}
  </span>


  <button
    onClick={() =>
      setCurrentPage((page) =>
        Math.min(page + 1, totalPages)
      )
    }
    disabled={
      currentPage === totalPages ||
      totalPages === 0
    }
  >
    Next
  </button>

</div>

        </div>

      </main>
    </>
  )
}

export default App


