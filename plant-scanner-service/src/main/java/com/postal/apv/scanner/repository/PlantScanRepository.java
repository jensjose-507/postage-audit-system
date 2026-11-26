package com.postal.apv.scanner.repository;

import com.postal.apv.scanner.model.PlantScanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantScanRepository extends JpaRepository<PlantScanRecord, Long> {}
