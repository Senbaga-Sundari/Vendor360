import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api/vendors";

const api = axios.create({
    baseURL: API_BASE_URL
});

export const getDashboardSummary = () => {
    return api.get("/dashboard");
};

export const getAllVendors = () => {
    return api.get("");
};

export default api;