package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;

/**
 * @author Jared Tulayan
 */
public class WeaponComponent implements Component {
    public float attackTimer, attackRate, spread, range, damage, critChance;

    public WeaponComponent() {

    }
}
