package com.postal.apv.audit.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "postage_adjustments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostageAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;
    private String facilityId;
    private String declaredService;
    private String detectedPackaging;
    private double declaredWeightLbs;
    private double billableWeightLbs;
    private double paidAmount;
    private double expectedAmount;
    private double shortfallAmount;

    @Column(length = 500)
    private String violationSummary;

    private String status;
    private LocalDateTime detectedAt;
}
