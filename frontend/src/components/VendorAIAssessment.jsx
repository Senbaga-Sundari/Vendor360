import { useState } from "react";

function VendorAIAssessment({ vendorId }) {

  const [assessment, setAssessment] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const getAssessment = async () => {

    try {

      setLoading(true);
      setError("");
      setAssessment("");

      const response = await fetch(
        `http://localhost:8080/api/vendors/${vendorId}/ai-assessment`
      );

      if (!response.ok) {
        throw new Error("Failed to generate AI assessment");
      }

      const data = await response.text();

      setAssessment(data);

    } catch (err) {

      setError(err.message);

    } finally {

      setLoading(false);

    }
  };

  return (

    <div className="ai-assessment">

      <button
        className="ai-button"
        onClick={getAssessment}
      >

        🤖 AI Assessment

      </button>


      {loading && (
        <p className="loading">
          🤖 AI is analyzing vendor data...
        </p>
      )}


      {error && (
        <p className="error">
          Error: {error}
        </p>
      )}


      {assessment && (

        <div className="ai-result">

          <h3>🤖 AI Vendor Risk Assessment</h3>

          <p>
            {assessment}
          </p>

        </div>

      )}

    </div>

  );

}

export default VendorAIAssessment;