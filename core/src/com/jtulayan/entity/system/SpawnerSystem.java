package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.jtulayan.entity.GameObjects;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.AIComponent;
import com.jtulayan.entity.component.ColliderComponent;
import com.jtulayan.entity.component.SpawnerComponent;
import com.jtulayan.entity.component.TransformComponent;

/**
 * @author Jared Tulayan
 */
public class SpawnerSystem extends IteratingSystem {
    private int zombieCount = 0;
    private final int ZOMBIE_LIMIT = 50;

    public SpawnerSystem() {
        super(Family.all(SpawnerComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        engine.addEntityListener(Family.all(AIComponent.class, TransformComponent.class, ColliderComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                zombieCount++;
            }

            @Override
            public void entityRemoved(Entity entity) {
                zombieCount--;
            }
        });
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpawnerComponent spawner = Mapper.SPAWNER_MAPPER.get(entity);
        TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);

        spawner.timer -= deltaTime;

        if (spawner.timer <= 0) {
            float
                x = transform.WORLD_ORIGIN.x + MathUtils.random(-transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.x),
                y = transform.WORLD_ORIGIN.y + MathUtils.random(-transform.LOCAL_ORIGIN.y, transform.LOCAL_ORIGIN.y);

            switch(spawner.TYPE) {
                case ZOMBIE:
                    if (zombieCount < ZOMBIE_LIMIT)
                        getEngine().addEntity(GameObjects.createZombie(x, y));
                    break;

                case ITEM:
                    int chance = MathUtils.random(2);

                    switch (chance) {
                        case 0:
                            getEngine().addEntity(GameObjects.createAmmoBox(x, y, 120));
                            break;
                        case 1:
                            getEngine().addEntity(GameObjects.createBandage(x, y, 120));
                            break;
                        case 2:
                            getEngine().addEntity(GameObjects.createDressing(x, y, 120));
                            break;
                    }
                    break;
            }

            spawner.timer = MathUtils.random(spawner.MIN_TIME, spawner.MAX_TIME);
        }
    }
}
