package com.jtulayan.main.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.jtulayan.entity.GameObjects;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.system.*;
import com.jtulayan.main.ZombieFloor;
import com.sun.javafx.geom.Dimension2D;

/**
 * @author Jared Tulayan
 */
public class GameScreen implements Screen {
    private final ZombieFloor PARENT;
    private final Engine ENGINE;
    private final Dimension2D MAP_SIZE;
    private Camera gameCam;

    public GameScreen(ZombieFloor p) {
        PARENT = p;
        gameCam = new OrthographicCamera();
        ENGINE = new PooledEngine();

        ENGINE.addSystem(new CollisionSystem(gameCam));
        ENGINE.addSystem(new RenderSystem(PARENT.getBatch(), gameCam, PARENT.getViewport()));
        ENGINE.addSystem(new PlayerSystem(gameCam));
        ENGINE.addSystem(new ZombieSystem(ENGINE.getSystem(PlayerSystem.class)));
        ENGINE.addSystem(new MovementSystem());
        ENGINE.addSystem(new DamageSystem());
        ENGINE.addSystem(new ItemSystem());
        ENGINE.addSystem(new SpawnerSystem());

        Entity[] map = GameObjects.createMap("map.tmx").toArray(Entity.class);

        MapProperties prop = Mapper.MAP_MAPPER.get(map[map.length - 1]).map.getProperties();
        MAP_SIZE = new Dimension2D(
                (Integer)prop.get("width") * (Integer)prop.get("tilewidth"),
                (Integer)prop.get("height") * (Integer)prop.get("tileheight")
        );

        for (Entity e : map)
            ENGINE.addEntity(e);

        ENGINE.addEntity(GameObjects.createPlayer(100, 100, PARENT.getViewport(), PARENT.getBatch(), PARENT.getAssets()));
        ENGINE.addEntity(GameObjects.createZombie(300, 500));
        ENGINE.addEntity(GameObjects.createM4(200, 200, -1, ENGINE));
        ENGINE.addEntity(GameObjects.createBenelli(300, 200, -1, ENGINE));


    }

    @Override
    public void show() {
        PARENT.getViewport().setCamera(gameCam);
        PARENT.getViewport().apply(true);

        for (EntitySystem s : ENGINE.getSystems()) {
            s.setProcessing(true);
        }
    }

    @Override
    public void render(float delta) {
        // If anything messes with the position, always make sure its in the bounds of the map.
        gameCam.position.set(
                MathUtils.clamp(gameCam.position.x, gameCam.viewportWidth / 2, MAP_SIZE.width - gameCam.viewportWidth / 2),
                MathUtils.clamp(gameCam.position.y, gameCam.viewportHeight / 2, MAP_SIZE.height - gameCam.viewportHeight / 2),
                1);

        gameCam.update();
        ENGINE.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        PARENT.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        for (EntitySystem s : ENGINE.getSystems()) {
            s.setProcessing(false);
        }
    }

    @Override
    public void resume() {
        for (EntitySystem s : ENGINE.getSystems()) {
            s.setProcessing(true);
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
