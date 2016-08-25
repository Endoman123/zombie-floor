package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * {@link Component} for any {@link Entity} that deals damage.
 *
 * @author Jared Tulayan
 */
public class DamageComponent implements Component {
    public int damage = 10;
}
