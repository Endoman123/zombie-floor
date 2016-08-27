package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.MovementComponent;
import com.jtulayan.entity.component.TransformComponent;

/**
 * {@link EntitySystem} to keep rotation and world center variables managed {@link Entity}s as well as
 * move them if they can be moved.
 *
 * @author Jared Tulayan
 */
public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Family.all(TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);

        // For moving entities only
        if (Mapper.MOVEMENT_MAPPER.has(entity)) {
            MovementComponent movement = Mapper.MOVEMENT_MAPPER.get(entity);

            // Keep movement normal normalized and scaled by translationSpeed.
            // The vector is copied so we are only scaling a copy and not the actual normal.
            Vector2 move = movement.NORMAL.nor().cpy().scl(movement.translationSpeed * deltaTime);

            // Move transform by movement vector and rotate by rotation speed
            // Note that the direction of the normal is not handled here;
            // We let code from other systems handle that.
            transform.POSITION.add(move);
            transform.rotation += movement.rotationSpeed * deltaTime;
        }

        // Keep rotation between 0 and 359
        if (transform.rotation > 360)
            transform.rotation -= 360;
        if (transform.rotation <= 0)
            transform.rotation += 360;

        // Modify WORLD_ORIGIN to be located where the transform's LOCAL_ORIGIN is in world coordinates
        transform.WORLD_ORIGIN.set(transform.POSITION.x + transform.LOCAL_ORIGIN.x, transform.POSITION.y + transform.LOCAL_ORIGIN.y);
    }
}
