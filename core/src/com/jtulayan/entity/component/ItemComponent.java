package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.jtulayan.handler.ItemHandler;

/**
 * {@link Component} that makes an {@link Entity} into an object that a player can hold.
 * The intention is for this component to be used anonymously.
 *
 * @author Jared Tulayan
 */
public class ItemComponent implements Component {
    public int maxStack, stack;
    public boolean dropped;
    public String name;
    public ItemHandler handler;

    public ItemComponent() {
        maxStack = 1;
        stack = 1;
        dropped = true;
    }

    public ItemComponent(int s, int ms, String n, ItemHandler cb) {
        if (ms < s)
            throw new IllegalArgumentException("Max stack < Stack");

        stack = s;
        maxStack = ms;
        name = n;
        handler = cb;
    }

    public String toString() {
        if (handler == null)
            return name + " (" + stack + "/" + maxStack + ")";

        return handler.toString();
    }
}
