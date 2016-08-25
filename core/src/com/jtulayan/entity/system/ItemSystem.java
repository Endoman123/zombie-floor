package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.HealthComponent;
import com.jtulayan.entity.component.ItemComponent;

/**
 * @author Jared Tulayan
 */
public class ItemSystem extends IteratingSystem {
    public ItemSystem() {
        super(Family.all(ItemComponent.class, HealthComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent item = Mapper.ITEM_MAPPER.get(entity);
        HealthComponent health = Mapper.HEALTH_MAPPER.get(entity);

        if (item.dropped) {
            if (item.stack == 0)
                getEngine().removeEntity(entity);

            if (health.health != -1) {
                health.health -= deltaTime;
                if (health.health <= 0) {
                    getEngine().removeEntity(entity);
                }
            }
        }
    }
}
