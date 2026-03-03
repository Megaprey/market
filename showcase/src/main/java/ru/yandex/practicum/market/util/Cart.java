package ru.yandex.practicum.market.util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Component
@Getter
@Setter
public class Cart implements Serializable {
    private HashMap<Long, Short>  mapItemInCart = new HashMap<>();

    public void addItem(Long id, Short countItem) {
        mapItemInCart.put(id, mapItemInCart.containsKey(id) ? (short)(mapItemInCart.get(id) + countItem)
                : countItem);
    }

    public void lessItem(Long id) {
        if(mapItemInCart.containsKey(id) && (mapItemInCart.get(id) == 1)) {
            mapItemInCart.remove(id);
        } else {
            mapItemInCart.put(id, mapItemInCart.containsKey(id) && mapItemInCart.get(id) >= 1 ? (short) (mapItemInCart.get(id) - 1)
                    : 1);
        }
    }

    public Short getItemCount(Long id) {
        return mapItemInCart.getOrDefault(id,  (short) 0);
    }

    public void removeItem(Long id) {
        mapItemInCart.remove(id);
    }

    public boolean contains(Long id) {
        return mapItemInCart.containsKey(id);
    }

    public List<Long> getIds() {
        return List.copyOf(mapItemInCart.keySet());
    }

    public void handleItemAction(String action, Long id) {
        switch (action) {
            case "PLUS" -> {
                this.addItem(id, (short) 1);
            }
            case "MINUS" -> {
                this.lessItem(id);
            }
            case "DELETE" -> {
                this.removeItem(id);
            }
        }
    }

    public void removeAllItems() {
        mapItemInCart.clear();
    }
}
