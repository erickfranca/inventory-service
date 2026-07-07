package com.lab.inventory.service;

import com.lab.inventory.model.Item;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InventoryService {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public Collection<Item> findAll() {
        return items.values();
    }

    public Item findById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NoSuchElementException("Item not found: " + id);
        }
        return item;
    }

    public Item create(Item item) {
        long id = sequence.incrementAndGet();
        Item created = new Item(id, item.name(), item.quantity());
        items.put(id, created);
        return created;
    }

    public Item update(Long id, Item item) {
        findById(id);
        Item updated = new Item(id, item.name(), item.quantity());
        items.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        items.remove(id);
    }
}
