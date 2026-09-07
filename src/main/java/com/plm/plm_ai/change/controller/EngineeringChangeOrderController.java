package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ECOStatus;
import com.plm.plm_ai.change.EngineeringChangeOrder;
import com.plm.plm_ai.change.service.EngineeringChangeOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eco")
public class EngineeringChangeOrderController {

    private final EngineeringChangeOrderService ecoService;

    public EngineeringChangeOrderController(
            EngineeringChangeOrderService ecoService) {

        this.ecoService = ecoService;
    }

    // ============================================================
    // CREATE ECO FROM APPROVED ECR
    // ============================================================

    @PostMapping("/from-ecr/{ecrId}")
    public ResponseEntity<EngineeringChangeOrder> createECO(
            @PathVariable Long ecrId,
            @RequestParam String createdBy) {

        return new ResponseEntity<>(
                ecoService.createECO(ecrId, createdBy),
                HttpStatus.CREATED
        );
    }

    // ============================================================
    // GET ALL ECOs
    // ============================================================

    @GetMapping
    public ResponseEntity<List<EngineeringChangeOrder>> getAllECOs() {

        return ResponseEntity.ok(
                ecoService.getAllECOs()
        );
    }

    // ============================================================
    // GET ECO BY ID
    // ============================================================

    @GetMapping("/{ecoId}")
    public ResponseEntity<EngineeringChangeOrder> getECOById(
            @PathVariable Long ecoId) {

        return ResponseEntity.ok(
                ecoService.getECOById(ecoId)
        );
    }

    // ============================================================
    // UPDATE ECO STATUS
    // ============================================================

    @PutMapping("/{ecoId}/status")
    public ResponseEntity<EngineeringChangeOrder> updateStatus(
            @PathVariable Long ecoId,
            @RequestParam ECOStatus status) {

        return ResponseEntity.ok(
                ecoService.updateStatus(
                        ecoId,
                        status
                )
        );
    }
}