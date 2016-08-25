package com.jtulayan.main;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

/**
 * Class wrapping around all the assets for this game.
 *
 * @author Jared Tulayan
 */
public class Assets implements Disposable {
    public final AssetManager MANAGER;

    public Assets() {
        MANAGER = new AssetManager();
        MANAGER.finishLoading();
    }

    public static class UI {

        public static final String
            SKIN = "ui/uiskin.json",
        ATLAS = "ui/uiskin.atlas";
    }

    public static class HUD {

        public static final String
            ATLAS = "hud/hud.pack";
    }

    @Override
    public void dispose() {
        MANAGER.dispose();
    }
}
