package com.jtulayan.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.CanvasComponent;
import com.jtulayan.entity.component.ColliderComponent;
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
    private final ShapeRenderer DEBUG;
    private final OrthographicCamera CAMERA;
    private final Matrix4 OLD_MATRIX;
    private final Viewport VIEWPORT;
    private final int SRC, DST;
    private boolean debug;

    public RenderSystem(SpriteBatch b, OrthographicCamera c, Viewport v) {
        super(Family.one(RenderComponent.class, TransformComponent.class, ColliderComponent.class).get(), new ZComparator());
        BATCH = b;
        DEBUG = new ShapeRenderer();
        CAMERA = c;
        VIEWPORT = v;
        SRC = BATCH.getBlendSrcFunc();
        DST = BATCH.getBlendDstFunc();
        OLD_MATRIX = BATCH.getProjectionMatrix();
    }

    public void setDebug(boolean b) {
        debug = b;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderComponent render = Mapper.RENDER_MAPPER.get(entity);

        if (Mapper.TRANSFORM_MAPPER.has(entity)) {
            TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(entity);

            if (debug) {
                DEBUG.setProjectionMatrix(CAMERA.combined);
                DEBUG.begin(ShapeRenderer.ShapeType.Line);
                DEBUG.setColor(Color.CYAN);
                DEBUG.circle(transform.WORLD_ORIGIN.x, transform.WORLD_ORIGIN.y, 3);
                DEBUG.setColor(Color.WHITE);

                if (Mapper.COLLIDER_MAPPER.has(entity)) {
                    ColliderComponent collider = Mapper.COLLIDER_MAPPER.get(entity);

                    DEBUG.setColor(Color.RED);
                    DEBUG.polygon(collider.body.getTransformedVertices());
                    DEBUG.circle(collider.body.getX() + collider.body.getOriginX(), collider.body.getY() + collider.body.getOriginY(), 3);
                    DEBUG.setColor(Color.WHITE);
                }
                DEBUG.end();
            }

            if (render != null && render.SPRITES.size > 0) {
                for (SpriteAnimation s : render.ANIMATIONS)
                    s.update(deltaTime);

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
        }

        if (Mapper.MAP_MAPPER.has(entity)) {
            TiledMap map = Mapper.MAP_MAPPER.get(entity).map;

            OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(map, BATCH);

            mapRenderer.setView(CAMERA);
            mapRenderer.render();
        }

        if (Mapper.CANVAS_MAPPER.has(entity)) {
            CanvasComponent canvas = Mapper.CANVAS_MAPPER.get(entity);

            canvas.CANVAS.setDebugAll(debug);
            canvas.CANVAS.getViewport().update(VIEWPORT.getScreenWidth(), VIEWPORT.getScreenHeight(), true);
            canvas.CANVAS.draw();
        }
    }
}
