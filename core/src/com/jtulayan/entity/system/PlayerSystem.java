package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.*;

/**
 * {@link EntitySystem} that handles all player entities' health processor, and movement.
 *
 * @author Jared Tulayan
 */
public class PlayerSystem extends IteratingSystem {
    private final Camera CAMERA;
    private final ShapeRenderer DEBUG;
    private final Vector2 MOUSE;

    public PlayerSystem(Camera c) {
        super(Family.all(PlayerComponent.class, HealthComponent.class, ColliderComponent.class, InputComponent.class).get());
        CAMERA = c;
        DEBUG = new ShapeRenderer();
        MOUSE = new Vector2();

        DEBUG.setColor(Color.RED);
    }

    @Override
    public void update(float deltaTime) {
        Vector3 unproject = CAMERA.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        MOUSE.set(unproject.x, unproject.y);

        DEBUG.setProjectionMatrix(CAMERA.combined);
        DEBUG.begin(ShapeRenderer.ShapeType.Filled);
        DEBUG.circle(MOUSE.x, MOUSE.y, 3);
        DEBUG.end();

        super.update(deltaTime);
    }

    @Override
    // There is normally only one player for the entire scene.
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent player = Mapper.PLAYER_MAPPER.get(entity);
        TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
        InventoryComponent inventory = Mapper.INVENTORY_MAPPER.get(entity);
        MovementComponent movement = Mapper.MOVEMENT_MAPPER.get(entity);
        ColliderComponent collider = Mapper.COLLIDER_MAPPER.get(entity);
        InputComponent input = Mapper.INPUT_MAPPER.get(entity);

        // Zero out our movement normal
        movement.NORMAL.setZero();

        // Rotate body to look towards MOUSE
        transform.rotation = (float) Math.atan2(MOUSE.y - transform.WORLD_ORIGIN.y, MOUSE.x - transform.WORLD_ORIGIN.x) * MathUtils.radDeg;

        // Poll processor
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            movement.NORMAL.add(0, 1);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            movement.NORMAL.add(0, -1);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            movement.NORMAL.add(1, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            movement.NORMAL.add(-1, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            if (player.stamina > 0) {
                movement.translationSpeed = player.runSpeed;
                player.stamina -= deltaTime * player.burnRate;
            } else {
                movement.translationSpeed = player.walkSpeed;
                player.stamina = 0;
            }

            player.restTimer = -1;
        } else {
            if (player.stamina < 100) {
                if (player.restTimer == -1)
                    player.restTimer = 100;
                else if (player.restTimer > 0)
                    player.restTimer -= deltaTime * player.restRate;
                else if (player.restTimer <= 0)
                    player.stamina += deltaTime * player.regenRate;
            }

            movement.translationSpeed = player.walkSpeed;
        }


        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            inventory.use();

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
            inventory.useAlt();

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Entity[] collidingWith = collider.collidingWith.toArray(Entity.class);
            for (Entity e : collidingWith) {
                if (Mapper.ITEM_MAPPER.has(e))
                Mapper.ITEM_MAPPER.get(e).handler.pickup(entity);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            inventory.drop();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && inventory.getActiveItem() != null && Mapper.GUN_MAPPER.has(inventory.getActiveItem()))
            Mapper.GUN_MAPPER.get(inventory.getActiveItem()).reload();

        // Rotate movement normal to face mouse
        movement.NORMAL.rotate(transform.rotation - 90);

        CAMERA.position.set(transform.WORLD_ORIGIN.x, transform.WORLD_ORIGIN.y, 0);

        inventory.update(deltaTime);

        player.stamina = MathUtils.clamp(player.stamina, 0, 100);

        if (Mapper.CANVAS_MAPPER.has(entity))
            Mapper.CANVAS_MAPPER.get(entity).handler.update(deltaTime);

        health.health = MathUtils.clamp(health.health, 0, health.maxHealth);

        if (health.health == 0) {
            getEngine().removeEntity(entity);
        }
    }
}
