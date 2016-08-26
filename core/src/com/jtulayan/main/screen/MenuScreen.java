package com.jtulayan.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.jtulayan.main.Assets;
import com.jtulayan.main.ZombieFloor;

/**
 * @author Jared Tulayan
 */
public class MenuScreen extends ScreenAdapter {
    private final ZombieFloor PARENT;
    private Stage stage;
    private final Assets ASSETS;

    public MenuScreen(ZombieFloor p) {
        PARENT = p;
        ASSETS = PARENT.getAssets();
    }

    @Override
    public void show() {
        stage = new Stage(PARENT.getViewport(), PARENT.getBatch());
        Gdx.input.setInputProcessor(stage);

        final Skin SKIN = ((Skin) ASSETS.MANAGER.get(Assets.UI.SKIN));
        final Table TABLE = new Table();

        final Pixmap BG_CANVAS = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        BG_CANVAS.setColor(Color.LIGHT_GRAY);
        BG_CANVAS.fill();
        SKIN.add("canvas", new Texture(BG_CANVAS));

        final Image BG = new Image(SKIN.newDrawable("canvas", Color.MAGENTA)),
                    TITLE = new Image(SKIN.getDrawable("title"));

        final TextButton
                START = new TextButton("Start", SKIN, "toggle"),
                OPTIONS = new TextButton("Options", SKIN, "toggle"),
                EXIT = new TextButton("Exit", SKIN, "toggle");

        final ChangeListener LISTENER = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((Button) actor).isChecked()) {
                    if (actor == START)
                        PARENT.setScreen(new GameScreen(PARENT));
                    else if (actor == OPTIONS)
                        PARENT.setScreen(new MenuScreen(PARENT));
                    else if (actor == EXIT)
                        Gdx.app.exit();
                }
            }
        };

        TITLE.setScaling(Scaling.fit);
        START.addListener(LISTENER);
        OPTIONS.addListener(LISTENER);
        EXIT.addListener(LISTENER);

        TABLE.center().pad(20).setFillParent(true);
        TABLE.add(TITLE).pad(20).row();
        TABLE.add(START).pad(10).size(100, 32).row();
        TABLE.add(OPTIONS).pad(10).size(100, 32).row();
        TABLE.add(EXIT).pad(10).size(100, 32);

        BG.setFillParent(true);
        stage.addActor(BG);
        stage.addActor(TABLE);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
