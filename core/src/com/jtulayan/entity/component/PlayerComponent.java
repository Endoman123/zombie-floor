package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;

/**
 * @author Jared Tulayan
 */
public class PlayerComponent implements Component {
    public float
            walkSpeed,
            runSpeed,
            stamina,
            burnRate,
            regenRate,
            restTimer,
            restRate;

    public PlayerComponent() {
        walkSpeed = 100f;
        runSpeed = 200f;
        burnRate = 10f;
        regenRate = 15f;

        stamina = 100;
        restRate = 10f;
        restTimer = -1;
    }
}
