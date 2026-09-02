import { useEffect, useState } from 'react'
import './App.css'

function App() {
  // ============================================
  // STATE VARIABLES
  // ============================================

  const [vendors, setVendors] = useState([])

  const [dashboard, setDashboard] = useState(null)

  const [error, setError] = useState(null)


  // ============================================
  // FETCH DATA FROM BACKEND
  // ============================================

  useEffect(() => {

    async function fetchData() {

      try {

        setError(null)


        // ============================================
        // FETCH ALL VENDORS
        // API: /api/vendors
        // ============================================

        const vendorResponse = await fetch(
          'http://localhost:8080/api/vendors'
        )


        if (!vendorResponse.ok) {

          throw new Error(
            'Failed to fetch vendor data'
          )

        }


        const vendorData = await vendorResponse.json()

        setVendors(vendorData)


        // ============================================
        // FETCH DASHBOARD SUMMARY
        // API: /api/vendors/dashboard
        // ============================================

        const dashboardResponse = await fetch(
          'http://localhost:8080/api/vendors/dashboard'
        )


        if (!dashboardResponse.ok) {

          throw new Error(
            'Failed to fetch dashboard data'
          )

        }


        const dashboardData =
          await dashboardResponse.json()

        setDashboard(dashboardData)


      } catch (err) {

        console.error(err)

        setError(err.message)

      }

    }


    fetchData()

  }, [])


  // ============================================
  // PAGE UI
  // ============================================

  return (

    <div className="app">


      {/* ============================================
          HEADER
      ============================================ */}

      <header className="header">

        <h1>Vendor360</h1>

        <p>
          Integrated Vendor Management Dashboard
        </p>

      </header>



      {/* ============================================
          MAIN CONTENT
      ============================================ */}

      <main className="main-content">


        {/* ============================================
            DASHBOARD OVERVIEW
        ============================================ */}

        <section className="dashboard-section">

          <h2>Dashboard Overview</h2>


          {/* ERROR MESSAGE */}

          {error && (

            <p className="error-message">

              Error: {error}

            </p>

          )}


          {/* ============================================
              DASHBOARD SUMMARY CARDS
          ============================================ */}

          <div className="dashboard-cards">


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



        {/* ============================================
            VENDOR DETAILS
        ============================================ */}

        <section className="vendor-section">

          <h2>Vendor Details</h2>


          <div className="table-container">


            <table>


              {/* ============================================
                  TABLE HEADER
              ============================================ */}

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



              {/* ============================================
                  TABLE BODY
              ============================================ */}

              <tbody>


                {vendors.map((vendor) => (

                  <tr
                    key={vendor.vendorId}
                  >


                    <td>

                      {vendor.vendorId}

                    </td>


                    <td>

                      {vendor.vendorName}

                    </td>


                    <td>

                      {vendor.category}

                    </td>


                    <td>

                      {vendor.country}

                    </td>


                    <td>

                      {vendor.city}

                    </td>


                    <td>

                      {vendor.currency}{' '}

                      {vendor.contractValue}

                    </td>


                    <td>

                      {vendor.qualityScore}

                    </td>


                    <td>

                      {vendor.slaScore}

                    </td>


                    <td>

                      {vendor.deliveryScore}

                    </td>


                    <td>

                      {vendor.riskEvent
                        ? vendor.riskEvent
                        : 'No Risk'}

                    </td>


                  </tr>

                ))}


              </tbody>


            </table>


          </div>


        </section>


      </main>


    </div>

  )
}

export default App