package com.example.gocart.Predictor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPreprocessor {

    // Map item IDs to integer indices for model input
    private Map<String, Integer> itemToIndex;
    private List<String> indexToItem;

    public DataPreprocessor() {
        itemToIndex = new HashMap<>();
        indexToItem = new ArrayList<>();
    }

    public List<int[]> preprocessOrders(Map<String, List<String>> orders) {
        List<int[]> sequences = new ArrayList<>();

        for (List<String> order : orders.values()) {
            int[] sequence = new int[order.size()];
            for (int i = 0; i < order.size(); i++) {
                String itemId = order.get(i);
                int index = getItemIndex(itemId);
                sequence[i] = index;
            }
            sequences.add(sequence);
        }

        return sequences;
    }

    private int getItemIndex(String itemId) {
        if (!itemToIndex.containsKey(itemId)) {
            itemToIndex.put(itemId, itemToIndex.size());
            indexToItem.add(itemId);
        }
        return itemToIndex.get(itemId);
    }

    public String getItemFromIndex(int index) {
        return indexToItem.get(index);
    }

    public int getNumItems() {
        return itemToIndex.size();
    }
}
