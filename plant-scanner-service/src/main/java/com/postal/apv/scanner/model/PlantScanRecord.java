package com.postal.apv.scanner.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "plant_scan_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantScanRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;
    private String facilityId;
    private String declaredService;
    private double declaredWeightLbs;
    private double paidAmount;
    private double actualWeightLbs;
    private double lengthIn;
    private double widthIn;
    private double heightIn;
    private String detectedPackaging;
    private LocalDateTime scannedAt;
}
