package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ECOBOMChange;
import com.plm.plm_ai.change.service.ECOBOMChangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eco")
public class ECOBOMChangeController {

    private final ECOBOMChangeService ecoBOMChangeService;

    public ECOBOMChangeController(
            ECOBOMChangeService ecoBOMChangeService) {

        this.ecoBOMChangeService = ecoBOMChangeService;
    }

    // ---------------------------------------------
    // ADD
    // ---------------------------------------------

    @PostMapping("/{ecoId}/bom/add")
    public ResponseEntity<ECOBOMChange> addComponent(

            @PathVariable Long ecoId,

            @RequestParam Long parentRevisionId,

            @RequestParam Long childRevisionId,

            @RequestParam Integer quantity,

            @RequestParam String reason,

            @RequestParam String changedBy) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ecoBOMChangeService.addComponent(
                                ecoId,
                                parentRevisionId,
                                childRevisionId,
                                quantity,
                                reason,
                                changedBy
                        )
                );
    }

    // ---------------------------------------------
    // REMOVE
    // ---------------------------------------------

    @DeleteMapping("/{ecoId}/bom/remove")
    public ResponseEntity<ECOBOMChange> removeComponent(

            @PathVariable Long ecoId,

            @RequestParam Long parentRevisionId,

            @RequestParam Long childRevisionId,

            @RequestParam String reason,

            @RequestParam String changedBy) {

        return ResponseEntity.ok(
                ecoBOMChangeService.removeComponent(
                        ecoId,
                        parentRevisionId,
                        childRevisionId,
                        reason,
                        changedBy
                )
        );
    }

    // ---------------------------------------------
    // QUANTITY
    // ---------------------------------------------

    @PutMapping("/{ecoId}/bom/quantity")
    public ResponseEntity<ECOBOMChange> changeQuantity(

            @PathVariable Long ecoId,

            @RequestParam Long parentRevisionId,

            @RequestParam Long childRevisionId,

            @RequestParam Integer newQuantity,

            @RequestParam String reason,

            @RequestParam String changedBy) {

        return ResponseEntity.ok(
                ecoBOMChangeService.changeQuantity(
                        ecoId,
                        parentRevisionId,
                        childRevisionId,
                        newQuantity,
                        reason,
                        changedBy
                )
        );
    }

    // ---------------------------------------------
    // REPLACE
    // ---------------------------------------------

    @PutMapping("/{ecoId}/bom/replace")
    public ResponseEntity<ECOBOMChange> replaceComponent(

            @PathVariable Long ecoId,

            @RequestParam Long parentRevisionId,

            @RequestParam Long oldChildRevisionId,

            @RequestParam Long newChildRevisionId,

            @RequestParam Integer quantity,

            @RequestParam String reason,

            @RequestParam String changedBy) {

        return ResponseEntity.ok(
                ecoBOMChangeService.replaceComponent(
                        ecoId,
                        parentRevisionId,
                        oldChildRevisionId,
                        newChildRevisionId,
                        quantity,
                        reason,
                        changedBy
                )
        );
    }

    // ---------------------------------------------
    // HISTORY
    // ---------------------------------------------

    @GetMapping("/{ecoId}/bom/changes")
    public ResponseEntity<List<ECOBOMChange>> getChanges(
            @PathVariable Long ecoId) {

        return ResponseEntity.ok(
                ecoBOMChangeService.getChangesByECO(ecoId)
        );
    }
}