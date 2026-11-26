package com.postal.apv.audit.controller;

import com.postal.apv.audit.model.PostageAdjustment;
import com.postal.apv.audit.repository.PostageAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditController {

    private final PostageAdjustmentRepository repository;

    @GetMapping("/short-paid")
    public List<PostageAdjustment> getAllShortPaidCases() {
        return repository.findAll();
    }

    @GetMapping("/short-paid/{trackingNumber}")
    public List<PostageAdjustment> getByTrackingNumber(@PathVariable String trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber);
    }

    @GetMapping("/summary")
    public Map<String, Object> getRecoveredRevenueSummary() {
        List<PostageAdjustment> all = repository.findAll();
        double totalRecovered = all.stream()
                .mapToDouble(PostageAdjustment::getShortfallAmount)
                .sum();
        return Map.of(
                "totalViolationsCaught", all.size(),
                "totalShortfallRecoveredUsd", Math.round(totalRecovered * 100.0) / 100.0
        );
    }
}
