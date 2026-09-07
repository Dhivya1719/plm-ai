package com.plm.plm_ai.change;

import com.plm.plm_ai.item.ItemRevision;
import jakarta.persistence.*;

@Entity
@Table(
        name = "ecr_affected_revisions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "ecr_id",
                                "item_revision_id"
                        }
                )
        }
)
public class ECRAffectedRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ecr_id", nullable = false)
    private EngineeringChangeRequest ecr;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_revision_id", nullable = false)
    private ItemRevision itemRevision;

    @Column(length = 1000)
    private String impactDescription;

    public ECRAffectedRevision() {
    }

    public Long getId() {
        return id;
    }

    public EngineeringChangeRequest getEcr() {
        return ecr;
    }

    public void setEcr(EngineeringChangeRequest ecr) {
        this.ecr = ecr;
    }

    public ItemRevision getItemRevision() {
        return itemRevision;
    }

    public void setItemRevision(ItemRevision itemRevision) {
        this.itemRevision = itemRevision;
    }

    public String getImpactDescription() {
        return impactDescription;
    }

    public void setImpactDescription(String impactDescription) {
        this.impactDescription = impactDescription;
    }
}