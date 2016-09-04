package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.CanvasComponent;
import com.jtulayan.entity.component.RenderComponent;
import com.jtulayan.entity.component.TransformComponent;
import com.jtulayan.util.SpriteAnimation;

/**
 * {@link EntitySystem} that handles rendering any {@link Entity} with a {@code RenderComponent}.
 *
 * @author Jared Tulayan
 */
public class RenderSystem extends SortedIteratingSystem {
    private final SpriteBatch BATCH;
    private final OrthographicCamera CAMERA;
    private final int SRC, DST;
    private final Matrix4 OLD_MATRIX;
    private final Viewport VIEWPORT;

    public RenderSystem(SpriteBatch b, OrthographicCamera c, Viewport v) {
        super(Family.all(RenderComponent.class).get(), new ZComparator());
        BATCH = b;
        CAMERA = c;
        VIEWPORT = v;
        SRC = BATCH.getBlendSrcFunc();
        DST = BATCH.getBlendDstFunc();
        OLD_MATRIX = BATCH.getProjectionMatrix();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderComponent render = Mapper.RENDER_MAPPER.get(entity);

        for (SpriteAnimation s : render.ANIMATIONS) {
            s.update(deltaTime);
        }

        if (Mapper.TRANSFORM_MAPPER.has(entity) && render.SPRITES.size > 0) {
            TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);
            Sprite[] spriteset = Mapper.RENDER_MAPPER.get(entity).SPRITES.items;

            BATCH.enableBlending();
            //BATCH.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_SRC_COLOR);
            BATCH.setProjectionMatrix(CAMERA.combined);
            BATCH.begin();

            for (Sprite s : spriteset) {
                if (s != null) {
                    s.setPosition(transform.WORLD_ORIGIN.x - s.getOriginX(), transform.WORLD_ORIGIN.y - s.getOriginY());
                    s.setRotation(transform.rotation);
                    s.setScale(render.scaleX, render.scaleY);
                    s.draw(BATCH);
                }
            }

            BATCH.end();
            BATCH.setProjectionMatrix(OLD_MATRIX);
            BATCH.setBlendFunction(SRC, DST);
        }

        if (Mapper.MAP_MAPPER.has(entity)) {
            TiledMap map = Mapper.MAP_MAPPER.get(entity).map;

            OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(map, BATCH);

            mapRenderer.setView(CAMERA);
            mapRenderer.render();
        }

        if (Mapper.CANVAS_MAPPER.has(entity)) {
            CanvasComponent canvas = Mapper.CANVAS_MAPPER.get(entity);

            canvas.CANVAS.getViewport().update(VIEWPORT.getScreenWidth(), VIEWPORT.getScreenHeight(), true);
            canvas.CANVAS.draw();
        }
    }
}
