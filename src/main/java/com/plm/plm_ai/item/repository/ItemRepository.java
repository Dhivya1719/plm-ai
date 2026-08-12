package com.plm.plm_ai.item.repository;

import com.plm.plm_ai.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByItemNumber(String itemNumber);

    boolean existsByItemNumber(String itemNumber);
}