package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.ColliderComponent;
import com.jtulayan.entity.component.TransformComponent;

/**
 * {@link EntitySystem} that handles all entities with {@link ColliderComponent}s and calls their callbacks when they
 * have been intersected with.
 *
 * @author Jared Tulayan
 */
public class CollisionSystem extends IteratingSystem {
    private Camera camera;
    private ShapeRenderer shapeRenderer;

    public CollisionSystem(Camera c) {
        super(Family.all(ColliderComponent.class, TransformComponent.class).get(), 10);

        if (c != null) {
            shapeRenderer = new ShapeRenderer(5124);
            camera = c;
            shapeRenderer.setColor(Color.RED);
        }
    }

    public CollisionSystem() {
        this(null);
    }

    @Override
    public void update(float deltaTime) {
        if (shapeRenderer != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }

            super.update(deltaTime);

        if (shapeRenderer != null)
            shapeRenderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ColliderComponent collider = Mapper.COLLIDER_MAPPER.get(entity);
        TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);

        // For debug purposes, draw collider, collider origin, and WORLD_ORIGIN of transform
        if (shapeRenderer != null && shapeRenderer.isDrawing()) {
            shapeRenderer.polygon(collider.body.getTransformedVertices());
            shapeRenderer.circle(collider.body.getX() + collider.body.getOriginX(), collider.body.getY() + collider.body.getOriginY(), 3);
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.circle(transform.WORLD_ORIGIN.x, transform.WORLD_ORIGIN.y, 3);
            shapeRenderer.setColor(Color.RED);
        }

        // Snap collider position to transform's origin
        collider.body.setPosition(transform.WORLD_ORIGIN.x - collider.body.getOriginX(), transform.WORLD_ORIGIN.y - collider.body.getOriginY());

        // Rotate collider so that it is facing the same direction as the transform
        collider.body.setRotation(transform.rotation);

        collider.collidingWith.shrink();

        for (Entity e : getEntities()) {
            if (e == entity)
                continue;

            ColliderComponent otherCollider = Mapper.COLLIDER_MAPPER.get(e);

            Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

            if (Intersector.overlapConvexPolygons(collider.body, otherCollider.body, mtv)) { // if in collision
                if (!collider.collidingWith.contains(e, true)) { // if entering collision

                    collider.collidingWith.add(e); // add to list
                    otherCollider.collidingWith.add(entity);

                    if (collider.handler != null)
                        collider.handler.enterCollision(e, mtv, otherCollider.solid); // run method
                } else { // if already colliding
                    if (collider.handler != null)
                        collider.handler.updateCollision(e, mtv, otherCollider.solid); // run method
                }

            } else if (collider.collidingWith.contains(e, true) && otherCollider.collidingWith.contains(entity, true)) { // if exiting collision
                collider.collidingWith.removeValue(e, true);
                otherCollider.collidingWith.removeValue(entity, true);

                if (collider.handler != null)
                    collider.handler.exitCollision();
            }
        }

        // Entities not in the engine will still exist in the array.
        // Remove them all here.
        for (Entity e : collider.collidingWith) {
            if (!getEngine().getEntities().contains(e, true)) {
                collider.collidingWith.removeValue(e, true);
            }
        }
    }
}
