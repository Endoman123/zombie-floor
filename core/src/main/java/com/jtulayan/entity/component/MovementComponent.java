package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * {@link Component} that contains data for moving a {@link TransformComponent}.
 *
 * @author Jared Tulayan
 */
public class MovementComponent implements Component {
    public final Vector2 NORMAL;
    public float translationSpeed;
    public float rotationSpeed;

    public MovementComponent() {
        NORMAL = new Vector2();
        translationSpeed = 0;
        rotationSpeed = 0;
    }
}
