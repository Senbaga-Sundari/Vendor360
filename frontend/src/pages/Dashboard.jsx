import { useEffect, useState } from "react";

import {
    Box,
    Typography,
    Grid,
    CircularProgress
} from "@mui/material";

import SummaryCard from "../components/SummaryCard";

import { getDashboardSummary } from "../services/api";


function Dashboard() {

    const [dashboard, setDashboard] = useState(null);

    const [loading, setLoading] = useState(true);


    useEffect(() => {

        loadDashboard();

    }, []);


    const loadDashboard = async () => {

        try {

            const response =
                await getDashboardSummary();

            console.log(response.data);

            setDashboard(response.data);

        }
        catch (error) {

            console.error(
                "Error loading dashboard:",
                error
            );

        }
        finally {

            setLoading(false);

        }

    };


    if (loading) {

        return (

            <Box
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    marginTop: 10
                }}
            >

                <CircularProgress />

            </Box>

        );

    }


    return (

        <Box
            sx={{
                padding: 4
            }}
        >

            <Typography
                variant="h4"
                sx={{
                    fontWeight: "bold",
                    marginBottom: 4
                }}
            >

                Vendor360 Dashboard

            </Typography>


            <Grid container spacing={3}>


                <Grid item xs={12} sm={6} md={3}>

                    <SummaryCard
                        title="Total Vendors"
                        value={dashboard?.totalVendors}
                    />

                </Grid>


                <Grid item xs={12} sm={6} md={3}>

                    <SummaryCard
                        title="Active Contracts"
                        value={dashboard?.activeContracts}
                    />

                </Grid>


                <Grid item xs={12} sm={6} md={3}>

                    <SummaryCard
                        title="High Risk Vendors"
                        value={dashboard?.highRiskVendors}
                    />

                </Grid>


                <Grid item xs={12} sm={6} md={3}>

                    <SummaryCard
                        title="Critical Dependencies"
                        value={dashboard?.criticalDependencies}
                    />

                </Grid>


            </Grid>


            <Typography
                variant="h5"
                sx={{
                    marginTop: 5,
                    marginBottom: 3,
                    fontWeight: "bold"
                }}
            >

                Performance Overview

            </Typography>


            <Grid container spacing={3}>


                <Grid item xs={12} md={4}>

                    <SummaryCard
                        title="Average Quality Score"
                        value={
                            dashboard?.averageQualityScore?.toFixed(2)
                        }
                    />

                </Grid>


                <Grid item xs={12} md={4}>

                    <SummaryCard
                        title="Average SLA Score"
                        value={
                            dashboard?.averageSlaScore?.toFixed(2)
                        }
                    />

                </Grid>


                <Grid item xs={12} md={4}>

                    <SummaryCard
                        title="Average Delivery Score"
                        value={
                            dashboard?.averageDeliveryScore?.toFixed(2)
                        }
                    />

                </Grid>


            </Grid>


        </Box>

    );

}


export default Dashboard;