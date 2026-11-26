package com.postal.apv.scanner.controller;

import com.postal.apv.events.PackageMeasurementEvent;
import com.postal.apv.scanner.model.PlantScanRecord;
import com.postal.apv.scanner.repository.PlantScanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/scans")
@RequiredArgsConstructor
public class ScannerController {

    private final PlantScanRepository repository;
    private final KafkaTemplate<String, PackageMeasurementEvent> kafkaTemplate;

    public record ScanSubmission(
            String trackingNumber,
            String facilityId,
            String declaredService,
            double declaredWeightLbs,
            double paidAmount,
            double actualWeightLbs,
            double lengthIn,
            double widthIn,
            double heightIn,
            String detectedPackaging
    ) {}

    @PostMapping("/dimension")
    public ResponseEntity<PlantScanRecord> ingestDimensionScan(@RequestBody ScanSubmission req) {
        LocalDateTime now = LocalDateTime.now();

        PlantScanRecord saved = repository.save(PlantScanRecord.builder()
                .trackingNumber(req.trackingNumber())
                .facilityId(req.facilityId())
                .declaredService(req.declaredService())
                .declaredWeightLbs(req.declaredWeightLbs())
                .paidAmount(req.paidAmount())
                .actualWeightLbs(req.actualWeightLbs())
                .lengthIn(req.lengthIn())
                .widthIn(req.widthIn())
                .heightIn(req.heightIn())
                .detectedPackaging(req.detectedPackaging())
                .scannedAt(now)
                .build());

        PackageMeasurementEvent event = new PackageMeasurementEvent(
                req.trackingNumber(),
                req.facilityId(),
                req.declaredService(),
                req.declaredWeightLbs(),
                req.paidAmount(),
                req.actualWeightLbs(),
                req.lengthIn(),
                req.widthIn(),
                req.heightIn(),
                req.detectedPackaging(),
                now
        );

        kafkaTemplate.send("package-measurements", req.trackingNumber(), event);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
