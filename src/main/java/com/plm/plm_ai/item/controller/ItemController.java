package com.plm.plm_ai.item.controller;

import com.plm.plm_ai.item.Item;
import com.plm.plm_ai.item.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        return new ResponseEntity<>(
                itemService.createItem(item),
                HttpStatus.CREATED
        );
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @RequestBody Item item) {

        return ResponseEntity.ok(
                itemService.updateItem(id, item)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {

        itemService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}