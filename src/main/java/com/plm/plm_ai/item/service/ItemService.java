package com.plm.plm_ai.item.service;

import com.plm.plm_ai.item.Item;
import com.plm.plm_ai.item.repository.ItemRepository;
import org.springframework.stereotype.Service;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.plm.plm_ai.item.LifecycleStatus;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemRevisionRepository revisionRepository;

    public ItemService(
            ItemRepository itemRepository,
            ItemRevisionRepository revisionRepository) {

        this.itemRepository = itemRepository;
        this.revisionRepository = revisionRepository;
    }

    //CREATE
    @Transactional
    public Item createItem(Item item) {

        if (itemRepository.existsByItemNumber(item.getItemNumber())) {
            throw new RuntimeException(
                    "Item number already exists: " + item.getItemNumber()
            );
        }

        // 1. Create the Item
        Item savedItem = itemRepository.save(item);

        // 2. Automatically create initial Revision A
        ItemRevision initialRevision = new ItemRevision();

        initialRevision.setItem(savedItem);
        initialRevision.setRevisionCode("A");
        initialRevision.setDescription(
                "Initial revision of " + savedItem.getName()
        );
        initialRevision.setStatus(LifecycleStatus.IN_WORK);

        revisionRepository.save(initialRevision);

        return savedItem;
    }

    // READ ALL
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // READ ONE
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Item not found with id: " + id)
                );
    }

    // UPDATE
    public Item updateItem(Long id, Item updatedItem) {

        Item existingItem = getItemById(id);

        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setItemType(updatedItem.getItemType());

        return itemRepository.save(existingItem);
    }

    // DELETE
    public void deleteItem(Long id) {

        Item existingItem = getItemById(id);

        itemRepository.delete(existingItem);
    }
}