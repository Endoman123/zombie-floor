package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.Method;

/**
 * @author Jared Tulayan
 */
public class SpawnerComponent implements Component {
    public final float MIN_TIME, MAX_TIME;
    public final SpawnerType TYPE;
    public float timer;
    public Array<Method> constructors;

    public SpawnerComponent(float min, float max, SpawnerType t) {
        MIN_TIME = min;
        MAX_TIME = max;
        timer = 0;

        TYPE = t;
    }
}
