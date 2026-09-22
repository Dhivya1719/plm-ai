package com.plm.plm_ai.item.repository;

import com.plm.plm_ai.item.Item;
import com.plm.plm_ai.item.ItemRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRevisionRepository
        extends JpaRepository<ItemRevision, Long> {

    List<ItemRevision> findByItem(Item item);

    List<ItemRevision> findByItemId(Long itemId);

    Optional<ItemRevision> findByItemAndRevisionCode(
            Item item,
            String revisionCode
    );

    boolean existsByItemAndRevisionCode(
            Item item,
            String revisionCode
    );

    Optional<ItemRevision> findTopByItemOrderByRevisionCodeDesc(
            Item item
    );
}