package com.example.telecom.vendor.evaluation;

public interface VendorEvaluator {

    EvaluationResult evaluate(String vendorId);

    String getEvaluatorType();
}
