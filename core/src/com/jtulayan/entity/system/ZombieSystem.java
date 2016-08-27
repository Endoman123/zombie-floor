package com.jtulayan.entity.system;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.jtulayan.entity.GameObjects;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.*;

/**
 * {@link EntitySystem} that processes zombie AI and properly moves, rotates, and handles states of all zombies.
 *
 * @author Jared Tulayan
 */
public class ZombieSystem extends IteratingSystem {
    private final Family PLAYER_FAMILY;
    private final Array<Entity> PLAYERS;

    public ZombieSystem(PlayerSystem ps) {
        super(Family.all(AIComponent.class, HealthComponent.class, ColliderComponent.class).get());

        // Pull player family straight from PlayerSystem
        PLAYER_FAMILY = ps.getFamily();
        PLAYERS = new Array<Entity>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        // Use an EntityListener to check if the player list has been updated.
        engine.addEntityListener(PLAYER_FAMILY, new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                PLAYERS.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                PLAYERS.removeValue(entity, true);
            }
        });
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Entity target = null;
        AIComponent ai = Mapper.AI_MAPPER.get(entity);
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);
        TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);
        MovementComponent movement = Mapper.MOVEMENT_MAPPER.get(entity);
        float angleDiff = 0;

        // Set zombie's movement normal to "forward"
        movement.NORMAL.set(0, 1);

        // Find the closest player
        for (Entity p : PLAYERS) {
            TransformComponent playerTransform = Mapper.TRANSFORM_MAPPER.get(p);

            if (target == null) {
                if (transform.WORLD_ORIGIN.dst2(playerTransform.WORLD_ORIGIN) <= ai.targetRange * ai.targetRange)
                    target = p;
            } else {
                TransformComponent targetTrans = Mapper.TRANSFORM_MAPPER.get(target);
                if (transform.WORLD_ORIGIN.dst2(playerTransform.WORLD_ORIGIN) <= transform.WORLD_ORIGIN.dst2(targetTrans.WORLD_ORIGIN))
                    target = p;
            }
        }

        // Properly figure out the target rotation based on whether or not the current zombie is targeting a player
        if (target != null) {
            TransformComponent targetTransform = Mapper.TRANSFORM_MAPPER.get(target);

            if (ai.state != AIStates.ATTACK) {
                ai.state = AIStates.ATTACK;
                ai.randomTimer = -1;
            }

            ai.targetDirection = MathUtils.atan2(targetTransform.WORLD_ORIGIN.y - transform.WORLD_ORIGIN.y, targetTransform.WORLD_ORIGIN.x - transform.WORLD_ORIGIN.x) * MathUtils.radDeg;

            if (ai.targetDirection >= 360)
                ai.targetDirection -= 360;
        } else {
            if (ai.state != AIStates.WANDER) {
                ai.state = AIStates.WANDER;
                ai.targetDirection = MathUtils.random(359);
                ai.randomTimer = MathUtils.random(2, 5);
            }

            ai.randomTimer -= deltaTime;

            if (ai.randomTimer <= 0) {
                ai.targetDirection = MathUtils.random(359);
                ai.randomTimer = MathUtils.random(2, 5);
            }
        }

        // Configure the angle difference
        angleDiff = transform.rotation - ai.targetDirection;

        if (Math.abs(angleDiff) > 180) {
            if (ai.targetDirection >= transform.rotation)
                angleDiff = (360 - ai.targetDirection) + transform.rotation;
            else
                angleDiff = -((360 - transform.rotation) + ai.targetDirection);
        }

        Gdx.app.log("ZOMBIE", "angleDiff = " + angleDiff);

        // Rotate zombie towards target
        if (Math.abs(angleDiff) > 10) {
            if (angleDiff > 10)
                movement.rotationSpeed = -ai.turnSpeed;
            else if (angleDiff < 10)
                movement.rotationSpeed = ai.turnSpeed;
        } else {
            movement.rotationSpeed = 0;
            transform.rotation = ai.targetDirection;
        }

        // Rotate movement normal and set movement translationSpeed
        movement.NORMAL.rotate(transform.rotation - 90);

        // Depending on state of attack, we want to change walking speeds and choose when to start attacking.
        if (ai.state == AIStates.ATTACK) {
            float dst = Mapper.TRANSFORM_MAPPER.get(target).WORLD_ORIGIN.dst(transform.WORLD_ORIGIN);

            if (dst <= ai.attackRange)
                movement.translationSpeed = 0;
            else
                movement.translationSpeed = ai.runSpeed;

            if (dst <= ai.attackRange + 32) {
                if (ai.attackTimer == -1)
                    ai.attackTimer = 1;
                else if (ai.attackTimer > 0)
                    ai.attackTimer -= deltaTime;
                else if (ai.attackTimer <= 0) {
                    getEngine().addEntity(GameObjects.createSlash(transform.WORLD_ORIGIN.x, transform.WORLD_ORIGIN.y, ai.attackRange, transform.rotation));
                    ai.attackTimer = 1 / ai.attackRate;
                }
            }
        } else {
            movement.translationSpeed = ai.walkSpeed;
            ai.attackTimer = -1;
        }

        if (health.health <= 0)
            getEngine().removeEntity(entity);
    }
}