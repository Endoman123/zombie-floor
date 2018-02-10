package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.DamageComponent;
import com.jtulayan.entity.component.HealthComponent;

/**
 * @author Jared Tulayan
 */
public class DamageSystem extends IteratingSystem {
    public DamageSystem() {
        super(Family.all(DamageComponent.class, HealthComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);

        health.health -= deltaTime;

        if (health.health <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}
