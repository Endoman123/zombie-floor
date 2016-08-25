package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Array;
import com.jtulayan.handler.CollisionHandler;

/**
 * {@link Component} that simply contains a {@link Polygon} to act as a collision shape.
 *
 * @author Jared Tulayan
 */
public class ColliderComponent implements Component {
    public Polygon body;
    public CollisionHandler handler;
    public boolean solid;
    public Array<Entity> collidingWith;

    public ColliderComponent() {
        this(null, null, true);
    }

    public ColliderComponent(Polygon b, CollisionHandler cb, boolean s) {
        body = b;
        handler = cb;
        solid = s;

        collidingWith = new Array<Entity>();
    }
}
