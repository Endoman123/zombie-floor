package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author Jared Tulayan
 */
public class AIComponent implements Component {
    public AIStates state = AIStates.WANDER;

    public float
            turnSpeed = 500,
            targetDirection = MathUtils.random(360),
            targetRange = 250f,
            walkSpeed = 80f,
            runSpeed = 120f,
            randomTimer = 500f,
            attackRange = 30f,
            attackRate = 1.5f,
            attackTimer = -1f;
}
