package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * {@link Component} that keeps track of amount of ammunition for any one {@link Entity}.
 * Intended to be used with guns.
 *
 * @author Jared Tulayan
 */
public class AmmoComponent implements Component {
    public int magAmmo, magSize, maxAmmo, ammo, bulletsPerRound;
    public float reloadTimer, reloadRate;
    public boolean isReloading;

    public AmmoComponent() {
        magAmmo = 1;
        magSize = 1;
        maxAmmo = 1;
        ammo = 1;
    }

    public void reload() {
        if (!isReloading && ammo > 0 && magAmmo < magSize) {
            reloadTimer = 1;
            isReloading = true;
        } else {
            if (reloadTimer <= 0) {
                int empty = magSize - magAmmo;

                if (empty >= ammo) {
                    magAmmo += ammo;
                    ammo = 0;
                } else {
                    magAmmo += empty;
                    ammo -= empty;
                }

                reloadTimer = 0;
                isReloading = false;
            }
        }
    }

    public String toString() {
        return "" + magAmmo + "/" + ammo;
    }
}
