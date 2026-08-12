package com.plm.plm_ai.item.service;

import com.plm.plm_ai.item.ItemRevision;
import org.springframework.stereotype.Service;
import com.plm.plm_ai.item.LifecycleStatus;

@Service
public class LifecycleService {

    private final ItemRevisionService revisionService;

    public LifecycleService(ItemRevisionService revisionService) {
        this.revisionService = revisionService;
    }

    public ItemRevision moveToInReview(Long revisionId) {

        ItemRevision revision =
                revisionService.getRevisionById(revisionId);

        if (revision.getStatus() != LifecycleStatus.IN_WORK) {
            throw new RuntimeException(
                    "Only IN_WORK revisions can move to IN_REVIEW"
            );
        }

        revision.setStatus(LifecycleStatus.IN_REVIEW);

        return revisionService.saveRevision(revision);
    }

    public ItemRevision releaseRevision(Long revisionId) {

        ItemRevision revision =
                revisionService.getRevisionById(revisionId);

        if (revision.getStatus() != LifecycleStatus.IN_REVIEW) {
            throw new RuntimeException(
                    "Only IN_REVIEW revisions can be released"
            );
        }

        revision.setStatus(LifecycleStatus.RELEASED);

        return revisionService.saveRevision(revision);
    }
}