package com.jtulayan.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jtulayan.entity.component.*;
import com.jtulayan.handler.CollisionHandler;
import com.jtulayan.handler.GUIHandler;
import com.jtulayan.handler.ItemHandler;
import com.jtulayan.main.Assets;

/**
 * Contains functions to quickly create any predefined {@link Entity} on the fly. This is just convenient to clean up code.
 *
 * @author Jared Tulayan
 */
public class GameObjects {
    /**
     * Creates a new player at the specified coordinate
     *
     * @param x the x-coordinate to place the player
     * @param y the y-coordinate to place the player
     * @return an {@link Entity} with the {@link Component}s of a player attached.
     */
    public static Entity createPlayer(float x, float y, Viewport v, Batch b, Assets assets) {
        final Entity e = new Entity();
        final TransformComponent transform = new TransformComponent();
        final InventoryComponent inventory = new InventoryComponent(1, 4, e);
        final InputComponent input = new InputComponent();
        final HealthComponent health = new HealthComponent();
        RenderComponent render = new RenderComponent();
        ColliderComponent collider = new ColliderComponent();
        CanvasComponent canvas = new CanvasComponent(v, b);
        final PlayerComponent player = new PlayerComponent();
        MovementComponent movement = new MovementComponent();

        transform.LOCAL_ORIGIN.set(16, 16);
        transform.width = 32;
        transform.height = 32;
        transform.POSITION.set(x, y);
        transform.WORLD_ORIGIN.set(x, y).sub(transform.LOCAL_ORIGIN);

        Texture tex = new Texture(Gdx.files.internal("player.png"));
        Sprite main = new Sprite(tex);
        main.setSize(tex.getWidth(), tex.getHeight());
        main.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        render.SPRITES.add(main);
        render.z = 1;

        collider.body = new Polygon(new float[]{
                0, 0,
                24, 0,
                24, 24,
                0, 24
        });
        collider.body.setOrigin(12, 12);
        collider.body.setPosition(x, y);
        collider.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity e, Intersector.MinimumTranslationVector mtv, boolean solid) {

            }

            @Override
            public void updateCollision(Entity e2, Intersector.MinimumTranslationVector m, boolean solid) {
                if (solid) {
                    Vector2 mtv = m.normal.scl(m.depth);

                    transform.POSITION.add(mtv);
                }
            }

            @Override
            public void exitCollision() {

            }
        };

        input.processor = new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                int area = inventory.STORAGE.length * inventory.STORAGE[0].length;
                inventory.activeSlot = (area + inventory.activeSlot + amount) % area;

                if (inventory.getActiveItem() != null && Mapper.GUN_MAPPER.has(inventory.getActiveItem())) {
                    GunComponent gun = Mapper.GUN_MAPPER.get(inventory.getActiveItem());

                    if (gun.reloading && gun.gunTimer > 0)
                        gun.gunTimer = gun.reloadRate;
                }

                return true;
            }
        };

        Skin skin = assets.MANAGER.get(Assets.UI.SKIN, Skin.class);

        Table table = new Table(), bars = new Table();

        final TextureAtlas hudAtlas = assets.MANAGER.get(Assets.HUD.ATLAS, TextureAtlas.class);

        final Image
                grid = new Image(hudAtlas.findRegion("hotbar")),
                hot = new Image(hudAtlas.findRegion("hotbarselection")),
                healthBack = new Image(hudAtlas.createPatch("barback")),
                staminaBack = new Image(hudAtlas.createPatch("barback")),
                healthFill = new Image(hudAtlas.createPatch("bar")),
                staminaFill = new Image(hudAtlas.createPatch("staminabar"));
        final Stack inventoryGrid = new Stack(grid, hot), healthBar = new Stack(healthBack, healthFill), staminaBar = new Stack(staminaBack, staminaFill);

        bars.debugAll();
        bars.add(healthBar).align(Align.left).width(256).height(32).row();
        bars.add(staminaBar).align(Align.left).width(128).height(32);

        table.debugAll();
        table.pad(10).setFillParent(true);
        table.add(bars).align(Align.left).row();
        table.add().fillX().expandY().row();
        table.add(inventoryGrid).align(Align.center).expandX();

        for (int r = 0; r < inventory.STORAGE.length; r++) {
            for (int c = 0; c < inventory.STORAGE[r].length; c++) {
                Image image = new Image();
                Label label = new Label("", skin);

                label.setAlignment(Align.bottomLeft);
                label.setFillParent(false);
                label.setLayoutEnabled(false);
                label.setFontScale(0.75f);

                image.setScaling(Scaling.fit);
                image.setLayoutEnabled(false);

                inventoryGrid.addActorBefore(hot, image);
                inventoryGrid.addActorBefore(hot, label);
            }
        }

        canvas.handler = new GUIHandler() {
            @Override
            public void update(float dt) {
                Entity[][] storage = inventory.STORAGE;

                int area = storage.length * storage[0].length;
                int pos = inventory.activeSlot;
                int x = 9 * (pos + 1) + 64 * pos;

                hot.setSize(64, 64);
                hot.setPosition(x, 9);

                for (int i = 0; i < area; i++) {
                    Entity curSlot = storage[i / storage[0].length][i % storage[0].length];
                    Image image = (Image) inventoryGrid.getChildren().get(2 * i + 1);
                    Label label = (Label) inventoryGrid.getChildren().get(2 * (i + 1));

                    if (curSlot != null) {
                        ItemComponent item = Mapper.ITEM_MAPPER.get(curSlot);
                        Sprite sprite = new Sprite(Mapper.RENDER_MAPPER.get(curSlot).SPRITES.get(Mapper.RENDER_MAPPER.get(curSlot).SPRITES.size() - 1));

                        image.setDrawable(new SpriteDrawable(sprite));

                        if (i == pos) {
                            image.setColor(Color.RED);
                            label.setColor(Color.RED);
                        } else {
                            image.setColor(Color.DARK_GRAY);
                            label.setColor(Color.WHITE);
                        }

                        label.setText(item.toString());
                    } else {
                        image.setDrawable(null);
                        label.setText(null);

                        image.setColor(Color.WHITE);
                        label.setColor(Color.WHITE);
                    }

                    image.layout();
                    image.setSize(48, 48);
                    image.setPosition(9 * (i + 1) + 64 * i + 8, 17);

                    label.layout();
                    label.setSize(64, 8);
                    label.setPosition(12 * (i + 1) + 64 * i, 9);
                }

                healthFill.setAlign(Align.bottomLeft);
                healthFill.setPosition(11, 11, Align.bottomLeft);
                healthFill.setHeight(healthBack.getHeight() - 22);
                healthFill.setWidth(Math.max((health.health / health.maxHealth * (healthBack.getWidth() - 22)), 0));

                staminaFill.setAlign(Align.bottomLeft);
                staminaFill.setPosition(11, 11, Align.bottomLeft);
                staminaFill.setHeight(staminaBack.getHeight() - 22);
                staminaFill.setWidth(Math.max((player.stamina / 100 * (staminaBack.getWidth() - 22)), 0));
            }
        };

        canvas.CANVAS.addActor(table);

        Gdx.input.setInputProcessor(input.processor);

        e.add(transform).add(render).add(collider).add(health).add(player).add(movement).add(inventory).add(input).add(canvas);

        return e;
    }

    /**
     * Creates a new zombie at the specified coordinate.
     *
     * @param x the x-coordinate to place the zombie
     * @param y the y-coordinate to place the zombie
     * @return an {@link Entity} with the {@link Component}s of a zombie attached
     */
    public static Entity createZombie(float x, float y) {
        final Entity e = new Entity();
        final TransformComponent transform = new TransformComponent();
        final InventoryComponent inventory = new InventoryComponent(1, 2, e);
        RenderComponent render = new RenderComponent();
        ColliderComponent collider = new ColliderComponent();
        HealthComponent human = new HealthComponent();
        final AIComponent ai = new AIComponent();
        MovementComponent movement = new MovementComponent();

        transform.LOCAL_ORIGIN.set(16, 16);
        transform.width = 32;
        transform.height = 32;
        transform.POSITION.set(x, y);
        transform.WORLD_ORIGIN.set(x, y).sub(transform.LOCAL_ORIGIN);

        Texture tex = new Texture(Gdx.files.internal("zombie.png"));
        Sprite main = new Sprite(tex);
        main.setSize(tex.getWidth(), tex.getHeight());
        main.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        render.SPRITES.add(main);

        collider.body = new Polygon(new float[]{
                0, 0,
                24, 0,
                24, 24,
                0, 24
        });
        collider.body.setOrigin(12, 12);
        collider.handler = new CollisionHandler() {
            @Override
            public void updateCollision(Entity e2, Intersector.MinimumTranslationVector m, boolean solid) {
                if (solid) {
                    Vector2 mtv = m.normal.scl(m.depth);

                    transform.POSITION.add(mtv);

                    if (ai.state == AIStates.WANDER)
                        ai.randomTimer = 0;
                }
            }
        };

        e.add(transform).add(render).add(collider).add(human).add(ai).add(movement);

        return e;
    }

    /**
     * Creates a rectanglular wall with the specified position and size.
     *
     * @param x the x-coordinate to place the zombie
     * @param y the y-coordinate to place the zombie
     * @return an {@link Entity} with the {@link Component}s of a zombie attached
     */
    public static Entity createWall(float x, float y, float w, float h) {
        Entity e = createWall(x, y, new float[]{
                0, 0,
                w, 0,
                w, h,
                0, h
        });

        TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(e);

        transform.width = w;
        transform.height = h;

        return e;
    }

    /**
     * Creates a wall at the specified position with the specified vertices.
     *
     * @param x the x-coordinate to place the zombie
     * @param y the y-coordinate to place the zombie
     * @return an {@link Entity} with the {@link Component}s of a zombie attached
     */
    public static Entity createWall(float x, float y, float[] verts) {
        Entity e = new Entity();
        TransformComponent transform = new TransformComponent();
        ColliderComponent collider = new ColliderComponent();

        transform.POSITION.set(x, y);
        transform.WORLD_ORIGIN.set(x, y);

        collider.body = new Polygon(verts);
        collider.body.setOrigin(0, 0);
        collider.body.setPosition(x, y);

        e.add(transform).add(collider);

        return e;
    }

    /**
     * Creates a bullet that deals damage.
     *
     * @param x         the x-coordinate to spawn the bullet
     * @param y         the y-coordinate to spawn the bullet
     * @param direction the direction the bullet should travel
     * @param spread    the degree of inaccuracy
     * @param lifeTime  the amount of time before the bullet is destroyed (in seconds)
     * @param ENGINE    the {@link Engine} to add the bullet to
     * @return an {@link Entity} with the {@link Component}s of a bullet attached
     */
    public static Entity createBullet(float x, float y, float direction, float spread, float lifeTime, final Engine ENGINE) {
        final Entity e = new Entity();
        TransformComponent transform = new TransformComponent();
        RenderComponent render = new RenderComponent();
        ColliderComponent collider = new ColliderComponent();
        HealthComponent life = new HealthComponent();
        final DamageComponent damage = new DamageComponent();
        MovementComponent movement = new MovementComponent();

        transform.width = 16;
        transform.height = 4;
        transform.LOCAL_ORIGIN.set(14, 2);
        transform.POSITION.set(x, y).sub(transform.LOCAL_ORIGIN);
        transform.rotation = direction + MathUtils.random(-spread / 2, spread / 2);

        movement.NORMAL.set(1, 0).rotate(transform.rotation);
        movement.translationSpeed = 1000f;

        Sprite main = new Sprite(new Texture("bullet.png"));
        main.setSize(transform.width, transform.height);
        main.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        main.setAlpha(1f);
        render.SPRITES.add(main);

        collider.body = new Polygon(new float[]{
                0, 0,
                1, 0,
                1, 1,
                0, 1
        });
        collider.body.setOrigin(0.5f, 0.5f);
        collider.handler = new CollisionHandler() {
            @Override
            public void enterCollision(Entity e2, Intersector.MinimumTranslationVector m, boolean solid) {
                if (solid && !Mapper.DAMAGE_MAPPER.has(e2)) {
                    if (Mapper.HEALTH_MAPPER.has(e2))
                        Mapper.HEALTH_MAPPER.get(e2).health -= damage.damage;

                    ENGINE.removeEntity(e);
                }
            }
        };

        life.maxHealth = life.health = lifeTime;

        e.add(transform).add(render).add(collider).add(life).add(damage).add(movement);

        return e;
    }

    /**
     * Creates a "slash" damage object that is short lived.
     * On touch it is destroyed immediately, dealing damage to the player touching it.
     *
     * @param x the x-coordinate to spawn it at
     * @param y the y-coordinate to spawn it at
     * @param r the range of the attack
     * @param rot the rotation of the attack object
     * @param ENGINE the {@link Engine} to add this object to
     * @return an {@link Entity} configured to be a damage object, placed at a specified location with a specified rotation
     */
    public static Entity createSlash(float x, float y, float r, float rot, final Engine ENGINE) {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent(x, y, r, r);
        final HealthComponent LIFETIME = new HealthComponent(0.1f);
        final DamageComponent DAMAGE = new DamageComponent();
        final ColliderComponent COLLIDER = new ColliderComponent(null, null, false);

        TRANSFORM.LOCAL_ORIGIN.set(0, 0);
        TRANSFORM.WORLD_ORIGIN.set(x, y);
        TRANSFORM.rotation = rot - 45;

        DAMAGE.damage = 5;

        COLLIDER.body = new Polygon(new float[] {
            r, r,
            r, 0,
            0, r
        });
        COLLIDER.body.setOrigin(0, 0);
        COLLIDER.body.setPosition(x, y);
        COLLIDER.body.setRotation(TRANSFORM.rotation);
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void updateCollision(Entity e, Intersector.MinimumTranslationVector mtv, boolean solid) {
                if (Mapper.PLAYER_MAPPER.has(e)) {
                    Gdx.app.log("COLLISION", "Player!");
                    HealthComponent health = Mapper.HEALTH_MAPPER.get(e);

                    health.health -= DAMAGE.damage;
                    ENGINE.removeEntity(E);
                }
            }
        };

        E.add(TRANSFORM).add(LIFETIME).add(DAMAGE).add(COLLIDER);

        return E;
    }

    /**
     * Creates the basic {@link Entity} for an item.
     * Note that this entity should not be used as-is, rather as a base entity to build off of.
     *
     * @param lifeTime the amount of time before the item is destroyed (in seconds)
     * @param ENGINE   the {@link Engine} to add the item to
     * @return an {@link Entity} with the {@link Component}s of a bullet attached
     */
    private static Entity createItem(float lifeTime, final Engine ENGINE) {
        final Entity e = new Entity();
        final TransformComponent transform = new TransformComponent();
        RenderComponent render = new RenderComponent();
        ColliderComponent collider = new ColliderComponent();
        HealthComponent life = new HealthComponent();
        final MovementComponent movement = new MovementComponent();
        final ItemComponent item = new ItemComponent();

        render.z = -10;

        transform.rotation = MathUtils.random(360);

        movement.rotationSpeed = 30f;

        life.maxHealth = life.health = lifeTime;

        e.add(transform).add(render).add(collider).add(life).add(item).add(movement);

        return e;
    }

    /**
     * Creates a gun that can be picked up and fired.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @param ENGINE   the {@link Engine} to add this item to.
     * @return an item {@link Entity} with the proper {@link Component}s for a gun
     */
    public static Entity createM4(float x, float y, float lifeTime, final Engine ENGINE) {
        final Entity e = createItem(lifeTime, ENGINE);
        final ItemComponent item = Mapper.ITEM_MAPPER.get(e);
        final TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(e);
        final ColliderComponent collider = Mapper.COLLIDER_MAPPER.get(e);
        final RenderComponent render = Mapper.RENDER_MAPPER.get(e);
        final GunComponent gun;

        transform.LOCAL_ORIGIN.set(23, 7.5f);
        transform.width = 46;
        transform.height = 15;
        transform.POSITION.set(x, y).sub(transform.LOCAL_ORIGIN);

        Texture main = new Texture("rifle.png");
        Sprite sprite = new Sprite(main);
        sprite.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        sprite.setSize(transform.width, transform.height);
        sprite.setScale(1.5f);
        render.SPRITES.add(sprite);

        collider.body = new Polygon(new float[]{
                0, 0,
                transform.width, 0,
                transform.width, transform.height,
                0, transform.height
        });
        collider.body.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        collider.solid = false;

        gun = new GunComponent(13.75f, 1, 4, 30);

        item.name = "M4A1";
        item.maxStack = 1;
        item.handler = new ItemHandler() {
            @Override
            public String toString() {
                return "" + gun.magAmmo + "/" + gun.ammo;
            }

            @Override
            public void update(float dt) {
                if (gun.gunTimer > 0)
                    gun.gunTimer -= dt;
                else if (gun.reloading)
                    gun.reload();
            }

            @Override
            public void pickup(Entity source) {
                transform.POSITION.set(-999, -999);
                InventoryComponent inventory = Mapper.INVENTORY_MAPPER.get(source);
                inventory.addByFill(e);
            }

            @Override
            public void use(Entity source) {
                if (!gun.reloading && gun.gunTimer <= 0 && gun.magAmmo > 0) {
                    TransformComponent sourceTransform = Mapper.TRANSFORM_MAPPER.get(source);
                    Vector2 spawn = new Vector2(1, 0);

                    spawn.rotate(sourceTransform.rotation).scl(16).add(sourceTransform.WORLD_ORIGIN);

                    for (int i = 0; i < 1; i++)
                        ENGINE.addEntity(GameObjects.createBullet(spawn.x, spawn.y, sourceTransform.rotation, 10, 5, ENGINE));

                    gun.magAmmo -= 1;
                    gun.gunTimer = 1 / gun.fireRate;
                }
            }

            @Override
            public void useAlt(Entity source) {

            }

            @Override
            public void drop(Entity source) {
                TransformComponent sourceTransform = Mapper.TRANSFORM_MAPPER.get(source);

                float
                    x = sourceTransform.WORLD_ORIGIN.x + MathUtils.random(-sourceTransform.LOCAL_ORIGIN.x, sourceTransform.LOCAL_ORIGIN.x),
                    y = sourceTransform.WORLD_ORIGIN.y + MathUtils.random(-sourceTransform.LOCAL_ORIGIN.y, sourceTransform.LOCAL_ORIGIN.y);

                transform.POSITION.set(x, y);
                item.dropped = true;
            }
        };

        e.add(gun);

        return e;
    }

    /**
     * Creates a gun that can be picked up and fired.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @param ENGINE   the {@link Engine} to add this item to.
     * @return an item {@link Entity} with the proper {@link Component}s for a gun
     */
    public static Entity createBenelli(float x, float y, float lifeTime, final Engine ENGINE) {
        final Entity e = createItem(lifeTime, ENGINE);
        final ItemComponent item = Mapper.ITEM_MAPPER.get(e);
        final TransformComponent transform = Mapper.TRANSFORM_MAPPER.get(e);
        final ColliderComponent collider = Mapper.COLLIDER_MAPPER.get(e);
        final RenderComponent render = Mapper.RENDER_MAPPER.get(e);
        final GunComponent gun;

        transform.LOCAL_ORIGIN.set(23, 7.5f);
        transform.width = 46;
        transform.height = 15;
        transform.POSITION.set(x, y).sub(transform.LOCAL_ORIGIN);

        Texture main = new Texture("shotgun.png");
        Sprite sprite = new Sprite(main);
        sprite.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        sprite.setSize(transform.width, transform.height);
        sprite.setScale(1.5f);
        render.SPRITES.add(sprite);

        collider.body = new Polygon(new float[]{
                0, 0,
                transform.width, 0,
                transform.width, transform.height,
                0, transform.height
        });
        collider.body.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        collider.solid = false;

        gun = new GunComponent(2.5f, 1, 4, 8);

        item.name = "Benelli M9";
        item.maxStack = 1;
        item.handler = new ItemHandler() {
            @Override
            public String toString() {
                return "" + gun.magAmmo + "/" + gun.ammo;
            }

            @Override
            public void update(float dt) {
                if (gun.gunTimer > 0)
                    gun.gunTimer -= dt;
                else if (gun.reloading)
                    gun.reload();
            }

            @Override
            public void pickup(Entity source) {
                InventoryComponent inventory = Mapper.INVENTORY_MAPPER.get(source);
                inventory.addByFill(e);
                transform.POSITION.set(-999, -999);
            }

            @Override
            public void use(Entity source) {
                if (!gun.reloading && gun.gunTimer <= 0 && gun.magAmmo > 0) {
                    TransformComponent sourceTransform = Mapper.TRANSFORM_MAPPER.get(source);
                    Vector2 spawn = new Vector2(1, 0);

                    spawn.rotate(sourceTransform.rotation).scl(16).add(sourceTransform.WORLD_ORIGIN);

                    for (int i = 0; i < 8; i++)
                        ENGINE.addEntity(GameObjects.createBullet(spawn.x, spawn.y, sourceTransform.rotation, 30, 5, ENGINE));

                    gun.magAmmo -= 1;
                    gun.gunTimer = 1 / gun.fireRate;
                }
            }

            @Override
            public void useAlt(Entity source) {

            }

            @Override
            public void drop(Entity source) {
                TransformComponent sourceTransform = Mapper.TRANSFORM_MAPPER.get(source);
                float
                        x = sourceTransform.WORLD_ORIGIN.x + MathUtils.random(-sourceTransform.LOCAL_ORIGIN.x, sourceTransform.LOCAL_ORIGIN.x),
                        y = sourceTransform.WORLD_ORIGIN.y + MathUtils.random(-sourceTransform.LOCAL_ORIGIN.y, sourceTransform.LOCAL_ORIGIN.y);

                transform.POSITION.set(x, y);
                item.dropped = true;
            }
        };

        e.add(gun);

        return e;
    }

    /**
     * Creates entities to build a map using the specified map file.
     *
     * @param path the location of the map file
     * @return all {@link Entity}s of the map with the proper {@link Component}s
     */
    public static Array<Entity> createMap(String path) {
        final Array<Entity> mapEntities = new Array<Entity>();
        final Entity main = new Entity();

        RenderComponent render = new RenderComponent();
        MapComponent mapComponent = new MapComponent();

        mapComponent.map = new TmxMapLoader().load(path);
        render.z = -100;

        MapObjects
                collisions = mapComponent.map.getLayers().get("Collisions").getObjects(),
                spawners = mapComponent.map.getLayers().get("Spawners").getObjects();

        main.add(mapComponent).add(render);

        for (MapObject o : collisions) {
            MapProperties properties = o.getProperties();
            float x, y, w, h;

            x = (Float) properties.get("x");
            y = (Float) properties.get("y");
            w = (Float) properties.get("width");
            h = (Float) properties.get("height");

            mapEntities.add(createWall(x, y, w, h));
        }

        for (MapObject o : spawners) {
            MapProperties properties = o.getProperties();
            float x, y, w, h;

            x = (Float) properties.get("x");
            y = (Float) properties.get("y");
            w = (Float) properties.get("width");
            h = (Float) properties.get("height");

            mapEntities.add(createSpawner(x, y, w, h, SpawnerType.ZOMBIE));
        }

        mapEntities.add(main);

        return mapEntities;
    }

    /**
     * Creates a spawner that spawns a certain group of {@link Entity}s.
     *
     * @param x the x-coordinate of the spawner
     * @param y the y-coordinate of the spawner
     * @param w the width of the spawner
     * @param h the height of the spawner
     * @param t the {@link SpawnerType}
     * @return an spawner {@link Entity} that spawns a set of {@link Entity}s
     *         in a defined region
     */
    public static Entity createSpawner(float x, float y, float w, float h, SpawnerType t) {
        final Entity e = new Entity();
        TransformComponent transform = new TransformComponent(x, y);
        SpawnerComponent spawner = new SpawnerComponent(2, 5, t);

        transform.width = w;
        transform.height = h;
        transform.LOCAL_ORIGIN.set(w / 2, h / 2);

        e.add(transform).add(spawner);

        return e;
    }
}
