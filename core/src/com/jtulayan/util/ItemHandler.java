package com.jtulayan.util;

import com.badlogic.ashley.core.Entity;

/**
 * @author Jared Tulayan
 */
public interface ItemHandler {

    /**
     * Called when the item is picked up
     *
     * @param source the {@link Entity} to pick up this item
     */
    public void pickup(Entity source);

    /**
     * Called when the item is being used
     *
     * @param source the {@link Entity} to use this item
     */
    public void use(Entity source);

    /**
     * Called when the item is being used
     * (Alternative use)
     *
     * @param source the {@link Entity} to use this item
     */
    public void useAlt(Entity source);

    /**
     * Called when the item is dropped, probably from an inventory
     *
     * @param source the {@link Entity} to drop this item
     */
    public void drop(Entity source);

    /**
     * Called when the item is being updated
     *
     * @param dt the time passed in between calls
     */
    public void update(float dt);

    public String toString();
}
