package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;

/**
 * {@link Component} that contains data that will give them health.
 *
 * @author Jared Tulayan
 */
public class HealthComponent implements Component {
    public float health, maxHealth;

    public HealthComponent() {
        this(100);
    }

    public HealthComponent(float mh) {
        maxHealth = mh;
        health = maxHealth;
    }

    public String toString() {
        return "" + health + "/" + maxHealth;
    }
}
