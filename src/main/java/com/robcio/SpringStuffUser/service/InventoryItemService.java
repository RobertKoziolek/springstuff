package com.robcio.SpringStuffUser.service;

import com.robcio.SpringStuffUser.dto.InventoryItemData;
import com.robcio.SpringStuffUser.entity.InventoryItem;
import com.robcio.SpringStuffUser.entity.User;
import com.robcio.SpringStuffUser.enumeration.ItemType;
import com.robcio.SpringStuffUser.repository.InventoryItemRepository;
import com.robcio.SpringStuffUser.util.ItemNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Service
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final UserService userService;
    private final ItemNameGenerator itemNameGenerator;

    @Autowired
    public InventoryItemService(final InventoryItemRepository inventoryItemRepository,
                                final UserService userService,
                                final ItemNameGenerator itemNameGenerator) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.userService = userService;
        this.itemNameGenerator = itemNameGenerator;
    }

    public void giveItem(final Long userId, final InventoryItem item) {
        final User user = userService.getUser(userId);
        item.setUser(user);
        inventoryItemRepository.save(item);
    }

    public InventoryItem getRandomItem() {
        final ItemType[] types = ItemType.values();
        final ItemType type = types[new Random().nextInt(types.length)];
        return getRandomItem(type);
    }

    public InventoryItem getRandomItem(final ItemType itemType) {
        final InventoryItem item = new InventoryItem();
        item.setType(itemType);
        item.setName(itemNameGenerator.getFor(itemType));
        return item;
    }

    public String getUserItemsListed(final Long userId) {
        return inventoryItemRepository.findByUserId(userId)
                                      .stream()
                                      .map(InventoryItem::getName)
                                      .sorted()
                                      .reduce((f, s) -> String.format("%s, %s", f, s))
                                      .orElse("");
    }

    public InventoryItem createItem(final InventoryItemData itemData) {
        validateItemData(itemData);
        final InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setName(itemData.getName());
        inventoryItem.setType(itemData.getType());
        return inventoryItem;
    }

    private void validateItemData(final InventoryItemData itemData) {
        assertThat(itemData.getName()).isNotNull();
        assertThat(itemData.getType()).isNotNull();
    }
}