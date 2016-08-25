package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

/**
 * This {@link Component} contains the data to give the parent {@link Entity}
 * a position, size, scale, and rotation in its world.
 *
 * @author Jared Tulayan
 */
public class TransformComponent implements Component {
    public final Vector2 POSITION, LOCAL_ORIGIN, WORLD_ORIGIN;
    public float
            width,
            height,
            scaleX,
            scaleY,
            rotation;

    /**
     * Initializes the {@link TransformComponent} at the position {@code (0, 0)}, centers the origin,
     * sets the width, height, scaleX, and scaleY to 1, and sets the rotation to 0.
     */
    public TransformComponent() {
        this(0, 0);
    }

    /**
     * Initializes this {@link TransformComponent} at the position {@code (x, y)}, centers the origin,
     * sets the width, height, scaleX, and scaleY to 1, and sets the rotation to 0.
     *
     * @param x the initial x-coordinate to place the parent {@link Entity} to
     * @param y the initial y-coordinate to place the parent {@link Entity} to
     * @param z the priority in the rendering queue; less is higher priority
     */
    public TransformComponent(float x, float y) {
        this(x, y, 1, 1);
    }

    /**
     * Initializes this {@link TransformComponent} at the position {@code (x, y, z)}, centers the origin,
     * sets the width and height to the specified width and height,
     * and sets the scaleX and scaleY to 1.
     *
     * @param x the initial x-coordinate to place the parent {@link Entity} to
     * @param y the initial y-coordinate to place the parent {@link Entity} to
     * @param w the initial width to size the parent {@link Entity} to
     * @param h the initial height to size the parent {@link Entity} to
     */
    public TransformComponent(float x, float y, float w, float h) {
        width = w;
        height = h;
        scaleX = 1;
        scaleY = 1;
        POSITION = new Vector2(x, y);
        LOCAL_ORIGIN = new Vector2(width / 2, height / 2);
        WORLD_ORIGIN = new Vector2(POSITION.x - LOCAL_ORIGIN.x, POSITION.y - LOCAL_ORIGIN.y);
        rotation = 0;
    }
}
