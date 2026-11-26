package com.postal.apv.audit.repository;

import com.postal.apv.audit.model.PostageAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostageAdjustmentRepository extends JpaRepository<PostageAdjustment, Long> {
    List<PostageAdjustment> findByTrackingNumber(String trackingNumber);
    List<PostageAdjustment> findByStatus(String status);
}
