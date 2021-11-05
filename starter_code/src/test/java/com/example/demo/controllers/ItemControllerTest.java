package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp () {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getAllItems () {
        ResponseEntity<List<Item>> items = itemController.getItems();
        Assertions.assertNotNull(items);
        Assertions.assertEquals(200, items.getStatusCodeValue());
    }

    @Test
    public void getItemsByName () {
        List<Item> i = new ArrayList<Item>();
        i.add(new Item());
        when(itemRepository.findByName("Round Widget")).thenReturn(i);
        ResponseEntity<List<Item>> items = itemController.getItemsByName("Round Widget");
        Assertions.assertNotNull(items);
        Assertions.assertEquals(200, items.getStatusCodeValue());

        Assertions.assertEquals(1, items.getBody().size());
    }

    @Test
    public void getItemsByInvalidName () {
        List<Item> i = new ArrayList<Item>();
        when(itemRepository.findByName("missing Widget")).thenReturn(i);
        ResponseEntity<List<Item>> items = itemController.getItemsByName("missing Widget");
        Assertions.assertNotNull(items);
        Assertions.assertEquals(404, items.getStatusCodeValue());
    }

    @Test
    public void getItemsById () {
        Item i = new Item();
        i.setId(1l);
        when(itemRepository.findById(1l)).thenReturn(Optional.of(i));
        ResponseEntity<Item> item = itemController.getItemById(1l);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(200, item.getStatusCodeValue());
    }

    @Test
    public void getItemsByInvalidId () {
        when(itemRepository.findById(22l)).thenReturn(Optional.ofNullable(null));
        ResponseEntity<Item> item = itemController.getItemById(2l);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(404, item.getStatusCodeValue());
    }
}
