package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;

/**
 * @author Jared Tulayan
 */
public class GunComponent implements Component {
    public float
            gunTimer,
            fireRate,
            reloadRate;

    public final int MAX_MAGS, MAG_SIZE, MAX_AMMO;
    public int ammo, magAmmo;
    public boolean reloading = false;

    public GunComponent(float fr, float rr, int mm, int ms) {
        fireRate = fr;
        reloadRate = rr;
        MAX_MAGS = mm;
        MAG_SIZE = ms;
        MAX_AMMO = MAX_MAGS * MAG_SIZE;

        ammo = MAX_AMMO;
        magAmmo = MAG_SIZE;
    }

    /**
     * Refills gun magazine and subtracts ammo needed from the current inventory of ammo
     */
    public void reload() {
        if (!reloading) {
            reloading = true;

            gunTimer = reloadRate;
        } else if (reloading && gunTimer <= 0) {

            int empty = MAG_SIZE - magAmmo;

            if (empty >= ammo) {
                magAmmo += ammo;
                ammo = 0;
            } else {
                magAmmo += empty;
                ammo -= empty;
            }

            gunTimer = 0;
            reloading = false;
        }
    }
}
