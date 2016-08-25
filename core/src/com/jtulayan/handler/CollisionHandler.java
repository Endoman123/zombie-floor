package com.jtulayan.handler;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Intersector;

/**
 * Callback class used when one {@link Entity} collides with another.
 *
 * @author Jared Tulayan
 */
public abstract class CollisionHandler {
    public void enterCollision(Entity e, Intersector.MinimumTranslationVector mtv, boolean solid) {

    }

    public void updateCollision(Entity e, Intersector.MinimumTranslationVector mtv, boolean solid) {

    }

    public void exitCollision() {

    }

}
