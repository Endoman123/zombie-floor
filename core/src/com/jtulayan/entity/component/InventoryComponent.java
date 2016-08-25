package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.jtulayan.entity.Mapper;

/**
 * @author Jared Tulayan
 */
public class InventoryComponent implements Component {
    public final Entity[][] STORAGE;
    public int activeSlot;
    public Entity owner;

    public InventoryComponent(int r, int c, Entity o) {
        STORAGE = new Entity[r][c];
        owner = o;
        activeSlot = 0;
    }

    /**
     * Gets the active item in the inventory
     *
     * @return the item in the index of the active slot.
     */
    public Entity getActiveItem() {
        int area = STORAGE.length * STORAGE[0].length;
        activeSlot = (area + activeSlot) % area;

        return STORAGE[activeSlot / STORAGE[0].length][activeSlot % STORAGE[0].length];
    }

    /**
     * If able to, picks up item currently standing over and adds it to storage.
     */
    public void pickup(Entity e) {
            addByFill(e);
            Mapper.ITEM_MAPPER.get(e).handler.pickup(owner);
    }

    /**
     * Uses the current item.
     */
    public void use() {
        if (getActiveItem() != null)
            Mapper.ITEM_MAPPER.get(getActiveItem()).handler.use(owner);
    }

    /**
     * Uses the current item (alternative use).
     */
    public void useAlt() {
        if (getActiveItem() != null)
            Mapper.ITEM_MAPPER.get(getActiveItem()).handler.useAlt(owner);
    }

    /**
     * Updates current item.
     *
     * @param dt the time in between calls
     */
    public void update(float dt) {
        if (getActiveItem() != null)
            Mapper.ITEM_MAPPER.get(getActiveItem()).handler.update(dt);
    }

    /**
     * Drops current item from inventory.
     */
    public void drop() {
        if (getActiveItem() != null) {
            Mapper.ITEM_MAPPER.get(getActiveItem()).handler.drop(owner);
            STORAGE[activeSlot / STORAGE[0].length][activeSlot % STORAGE[0].length] = null;
        }
    }

    /**
     * Adds the specified item into the first available slot. Extra will be fit in to the STORAGE recursively.
     *
     * @param item the item to add to the {@link InventoryComponent}.
     *
     * @return true if the item can be put into the STORAGE,
     *         false if either the item has no space, or the STORAGE overflows
     */
    public boolean addByFill(Entity item) {
        ItemComponent fillItem = Mapper.ITEM_MAPPER.get(item);
        for (int i = 0; i < STORAGE.length; i++) {
            for (int j = 0; j < STORAGE[i].length; j++) {
                Entity slot = STORAGE[i][j];
                if (slot == null) {
                    STORAGE[i][j] = item;
                    fillItem.dropped = false;
                    return true;
                } else {
                    ItemComponent otherItem = Mapper.ITEM_MAPPER.get(slot);
                    if (fillItem.name.equals(otherItem.name) && otherItem.stack < otherItem.maxStack) {
                        otherItem.stack += fillItem.stack;
                        if (otherItem.stack > otherItem.maxStack) {
                            fillItem.stack = otherItem.maxStack - otherItem.stack;
                            return addByFill(item);
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates a string representation of this inventory.
     *
     * @return the string representation of the inventory
     */
    public String toString() {
        String s = "";

        for (int i = 0; i < STORAGE.length * STORAGE[0].length; i++) {
            if (i % STORAGE[0].length == 0)
                s += "[ ";

            if (i == activeSlot)
                s += "*";

            Entity currentItem = STORAGE[i / STORAGE[0].length][i % STORAGE[0].length];
            if (currentItem != null) {
                ItemComponent item = Mapper.ITEM_MAPPER.get(currentItem);
                s += item.toString();
            } else
                s += "Empty";

            if (i % STORAGE[0].length < STORAGE[0].length - 1)
                s += ", ";
            else
                s += " ]";
        }

        return s;
    }
}
