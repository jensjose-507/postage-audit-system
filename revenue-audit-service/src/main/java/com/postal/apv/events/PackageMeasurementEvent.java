package com.postal.apv.events;

import java.time.LocalDateTime;

public record PackageMeasurementEvent(
        String trackingNumber,
        String facilityId,
        String declaredService,     // GROUND_ADVANTAGE, PRIORITY_MAIL
        double declaredWeightLbs,
        double paidAmount,
        double actualWeightLbs,
        double lengthIn,
        double widthIn,
        double heightIn,
        String detectedPackaging,   // CUSTOMER_BOX, PRIORITY_BRANDED_BOX
        LocalDateTime scannedAt
) {}
