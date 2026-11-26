package com.postal.apv.audit.service;

import com.postal.apv.audit.model.PostageAdjustment;
import com.postal.apv.audit.repository.PostageAdjustmentRepository;
import com.postal.apv.events.PackageMeasurementEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostageAuditService {

    private final PostageAdjustmentRepository adjustmentRepo;

    @KafkaListener(topics = "package-measurements", groupId = "revenue-audit-group")
    public void evaluatePackage(PackageMeasurementEvent event) {
        List<String> violations = new ArrayList<>();

        // Rule 1: Priority Mail Packaging used with Ground postage
        boolean priorityMismatch = "PRIORITY_BRANDED_BOX".equalsIgnoreCase(event.detectedPackaging())
                && !"PRIORITY_MAIL".equalsIgnoreCase(event.declaredService());
        if (priorityMismatch) {
            violations.add("SERVICE_MISMATCH: Priority Mail packaging used with " + event.declaredService() + " label");
        }

        // Rule 2: Dimensional Weight Trap (Cubic volume > 1728 cu in -> divisor 166)
        double volume = event.lengthIn() * event.widthIn() * event.heightIn();
        double dimWeight = (volume > 1728.0) ? Math.ceil(volume / 166.0) : 0.0;
        double billableWeight = Math.max(Math.ceil(event.actualWeightLbs()), dimWeight);

        if (dimWeight > Math.ceil(event.declaredWeightLbs())) {
            violations.add(String.format("DIM_WEIGHT_UNDERPAID: %.0fx%.0fx%.0f in = %.0f lbs DIM weight (declared %.1f lbs)",
                    event.lengthIn(), event.widthIn(), event.heightIn(), dimWeight, event.declaredWeightLbs()));
        } else if (event.actualWeightLbs() > event.declaredWeightLbs() + 0.1) {
            violations.add(String.format("ACTUAL_WEIGHT_UNDERPAID: Scale measured %.2f lbs vs declared %.2f lbs",
                    event.actualWeightLbs(), event.declaredWeightLbs()));
        }

        if (violations.isEmpty()) {
            return;
        }

        // Rule 3: Compute Postage Shortfall
        String effectiveService = priorityMismatch ? "PRIORITY_MAIL" : event.declaredService();
        double baseRate = "PRIORITY_MAIL".equalsIgnoreCase(effectiveService) ? 9.85 : 5.40;
        double perLbRate = "PRIORITY_MAIL".equalsIgnoreCase(effectiveService) ? 1.75 : 0.95;
        double expectedCost = Math.round((baseRate + (Math.max(0.0, billableWeight - 1.0) * perLbRate)) * 100.0) / 100.0;
        double shortfall = Math.max(0.0, Math.round((expectedCost - event.paidAmount()) * 100.0) / 100.0);

        if (shortfall > 0.25) {
            adjustmentRepo.save(PostageAdjustment.builder()
                    .trackingNumber(event.trackingNumber())
                    .facilityId(event.facilityId())
                    .declaredService(event.declaredService())
                    .detectedPackaging(event.detectedPackaging())
                    .declaredWeightLbs(event.declaredWeightLbs())
                    .billableWeightLbs(billableWeight)
                    .paidAmount(event.paidAmount())
                    .expectedAmount(expectedCost)
                    .shortfallAmount(shortfall)
                    .violationSummary(String.join(" | ", violations))
                    .status("BILLING_ADJUSTMENT_QUEUED")
                    .detectedAt(LocalDateTime.now())
                    .build());
        }
    }
}
