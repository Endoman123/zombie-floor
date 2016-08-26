package com.jtulayan.main;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Class wrapping around all the assets for this game.
 *
 * @author Jared Tulayan
 */
@SuppressWarnings("unchecked")
public class Assets implements Disposable {
    public final AssetManager MANAGER;
    private static final String TEXTURES = "textures/";

    public Assets() {
        MANAGER = new AssetManager();
        MANAGER.finishLoading();
    }

    public static class UI {
        public static final AssetDescriptor
            SKIN = new AssetDescriptor(TEXTURES + "ui/uiskin.json", Skin.class),
            ATLAS = new AssetDescriptor(TEXTURES + "ui/uiskin.atlas", TextureAtlas.class);
    }

    public static class HUD {
        public static final AssetDescriptor
            ATLAS = new AssetDescriptor(TEXTURES + "hud/hud.pack", TextureAtlas.class);
    }

    public static class GameObjects {
        public static final AssetDescriptor
            ATLAS = new AssetDescriptor(TEXTURES + "gameobjects/gameobjects.pack", TextureAtlas.class);
    }

    @Override
    public void dispose() {
        MANAGER.dispose();
    }
}
