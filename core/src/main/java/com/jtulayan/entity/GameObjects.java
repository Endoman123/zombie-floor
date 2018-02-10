package com.jtulayan.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jtulayan.entity.component.*;
import com.jtulayan.main.Assets;
import com.jtulayan.main.ZombieFloor;
import com.jtulayan.util.CollisionHandler;
import com.jtulayan.util.GUIHandler;
import com.jtulayan.util.ItemHandler;

/**
 * Contains functions to quickly create any predefined {@link Entity} on the fly. This is just convenient to clean up code.
 *
 * @author Jared Tulayan
 */
public class GameObjects {
    private static Assets assets;
    private static Viewport viewport;
    private static Batch batch;
    private static Engine engine;

    public static void initGame(ZombieFloor game) {
        assets = game.getAssets();
        viewport = game.getViewport();
        batch = game.getBatch();
    }

    public static void setEngine(Engine e) {
        engine = e;
    }

    /**
     * Creates a new player at the specified coordinate
     *
     * @param x the x-coordinate to place the player
     * @param y the y-coordinate to place the player
     * @return an {@link Entity} with the {@link Component}s of a player attached.
     */
    public static Entity createPlayer(float x, float y) {
        final Entity e = new Entity();
        final TransformComponent transform = new TransformComponent();
        final InventoryComponent inventory = new InventoryComponent(1, 4, e);
        final InputComponent input = new InputComponent();
        final HealthComponent health = new HealthComponent();
        RenderComponent render = new RenderComponent();
        final ColliderComponent collider = new ColliderComponent();
        CanvasComponent canvas = new CanvasComponent(new ScreenViewport(), batch);
        final PlayerComponent player = new PlayerComponent();
        final MovementComponent movement = new MovementComponent();

        transform.LOCAL_ORIGIN.set(8, 8);
        transform.width = 16;
        transform.height = 16;
        transform.POSITION.set(x, y);
        transform.WORLD_ORIGIN.set(x, y).sub(transform.LOCAL_ORIGIN);

        final TextureAtlas ATLAS = (TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS);

        final Sprite
                SHADOW = ATLAS.createSprite("misc/glow"),
                LEGS = ATLAS.createSprite("player/still"),
                BODY = ATLAS.createSprite("player/body"),
                HEAD = ATLAS.createSprite("player/head");

        SHADOW.setOriginCenter();
        SHADOW.setColor(Color.BLACK);
        SHADOW.setAlpha(0.5f);
        LEGS.setOrigin(16f, 14f);
        LEGS.setColor(Color.NAVY);
        BODY.setOrigin(12f, 18f);
        BODY.setColor(Color.FOREST);
        HEAD.setOrigin(8f, 7f);
        HEAD.setColor(Color.BROWN);
        render.SPRITES.add(SHADOW);
        render.SPRITES.add(LEGS);
        render.SPRITES.add(BODY);
        render.SPRITES.add(HEAD);
        render.scaleX = 1.2f;
        render.scaleY = 1.2f;
        render.z = 1;

//        render.ANIMATIONS.add(new SpriteAnimation(
//                30,
//                Animation.PlayMode.LOOP_PINGPONG,
//                ATLAS.findRegion("player/walk1"),
//                ATLAS.findRegion("player/walk2"),
//                ATLAS.findRegion("player/walk3")) {
//
//            @Override
//            public void update(float dt) {
//                if (movement.NORMAL.len() != 0) {
//                    stateTime += dt * movement.translationSpeed;
//                    changeFrame(ANIMATION.getKeyFrame(stateTime, isLooping));
//                } else {
//                    stateTime = 0;
//                    LEGS.setRegion(ATLAS.findRegion("player/still"));
//                    LEGS.setSize(LEGS.getRegionWidth(), LEGS.getRegionHeight());
//                }
//            }
//
//            @Override
//            public void changeFrame(TextureRegion frame) {
//                LEGS.setRegion(frame);
//                LEGS.setOrigin(0, 0);
//                LEGS.setSize(frame.getRegionWidth(), frame.getRegionHeight());
//                LEGS.setOrigin(16f, 14f);
//            }
//        });

        collider.body = new Polygon(new float[]{
                0, 0,
                16, 0,
                16, 16,
                0, 16
        });
        collider.body.setOrigin(8, 8);
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

                if (inventory.getActiveItem() != null) {
                    if (Mapper.AMMO_MAPPER.has(inventory.getActiveItem())) {
                        AmmoComponent ammo = Mapper.AMMO_MAPPER.get(inventory.getActiveItem());

                        if (ammo.isReloading && ammo.reloadTimer > 0) {
                            ammo.isReloading = false;
                            ammo.reloadTimer = -1;
                        }
                    }
                }

                return true;
            }
        };

        //canvas.CANVAS.setDebugAll(true);

        final Skin SKIN = (Skin) assets.MANAGER.get(Assets.UI.SKIN);

        final Table TABLE = new Table(), BARS = new Table();

        final TextureAtlas hudAtlas = (TextureAtlas) assets.MANAGER.get(Assets.HUD.ATLAS);

        final Image
                grid = new Image(hudAtlas.findRegion("hotbar")),
                hot = new Image(hudAtlas.findRegion("hotbarselection")),
                healthBack = new Image(hudAtlas.createPatch("barback")),
                staminaBack = new Image(hudAtlas.createPatch("barback")),
                healthFill = new Image(hudAtlas.createPatch("bar")),
                staminaFill = new Image(hudAtlas.createPatch("bar"));

        final Stack
                inventoryGrid = new Stack(grid, hot),
                healthBar = new Stack(healthBack, healthFill),
                staminaBar = new Stack(staminaBack, staminaFill);

        final Label
                action = new Label("", SKIN),
                time = new Label("", SKIN);

        healthFill.setColor(Color.RED);
        staminaFill.setColor(Color.GOLD);

        BARS.add(healthBar).align(Align.left).width(256).height(32).expandX().row();
        BARS.add(staminaBar).align(Align.left).width(128).height(32).expandX();

        TABLE.pad(10).setFillParent(true);
        TABLE.add(BARS).align(Align.left).expandX();
        TABLE.add(time).align(Align.topRight).expandX().row();
        TABLE.add().expand().fill().colspan(2).row();
        TABLE.add(action).align(Align.center).expandX().colspan(2).row();
        TABLE.add(inventoryGrid).align(Align.center).colspan(2).expandX();

        for (int r = 0; r < inventory.STORAGE.length; r++) {
            for (int c = 0; c < inventory.STORAGE[r].length; c++) {
                Image image = new Image();
                Label label = new Label("", SKIN);

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
            float timer;
            @Override
            public void update(float dt) {
                Entity[][] storage = inventory.STORAGE;

                int area = storage.length * storage[0].length;
                int pos = inventory.activeSlot;
                int x = 9 * (pos + 1) + 64 * pos;

                hot.setSize(64, 64);
                hot.setPosition(x, 9);

                TABLE.layout();

                for (int i = 0; i < area; i++) {
                    Entity curSlot = storage[i / storage[0].length][i % storage[0].length];
                    Image image = (Image) inventoryGrid.getChildren().get(2 * i + 1);
                    Label label = (Label) inventoryGrid.getChildren().get(2 * (i + 1));

                    int cell = 64 * i + 9 * (i + 1);

                    if (curSlot != null) {
                        ItemComponent item = Mapper.ITEM_MAPPER.get(curSlot);
                        Sprite sprite = new Sprite(
                                Mapper.RENDER_MAPPER.get(curSlot).SPRITES.get(
                                        Mapper.RENDER_MAPPER.get(curSlot).SPRITES.size - 1
                                )
                        );

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
                    image.setPosition(cell + 8, 17);

                    label.layout();
                    label.setSize(48, 8);
                    label.setPosition(cell + 8, 9);
                }

                ItemComponent hoverOver = null;

                for (Entity e : collider.collidingWith) {
                    if (Mapper.ITEM_MAPPER.has(e)) {
                        hoverOver = Mapper.ITEM_MAPPER.get(e);

                        break;
                    }
                }

                if (hoverOver != null)
                    action.setText("Press 'E' to pickup " + hoverOver.name);
                else
                    action.setText("");

                healthFill.setAlign(Align.bottomLeft);
                healthFill.setPosition(11, 11, Align.bottomLeft);
                healthFill.setHeight(healthBack.getHeight() - 22);
                healthFill.setWidth(Math.max((health.health / health.maxHealth * (healthBack.getWidth() - 22)), 0));

                staminaFill.setAlign(Align.bottomLeft);
                staminaFill.setPosition(11, 11, Align.bottomLeft);
                staminaFill.setHeight(staminaBack.getHeight() - 22);
                staminaFill.setWidth(Math.max((player.stamina / 100 * (staminaBack.getWidth() - 22)), 0));

                timer += dt;
                time.setText("TIME: " + (int)(timer / 60) + ":" + String.format("%02d", ((int)(timer * 100) / 100) % 60));
            }
        };

        canvas.CANVAS.addActor(TABLE);
        canvas.CANVAS.getActors().get(canvas.CANVAS.getActors().size - 1).setSize(600, 600);

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
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent();
        final InventoryComponent INVENTORY = new InventoryComponent(1, 2, E);
        final RenderComponent RENDER = new RenderComponent();
        final ColliderComponent COLLIDER = new ColliderComponent();
        final HealthComponent HUMAN = new HealthComponent();
        final AIComponent AI = new AIComponent();
        final MovementComponent MOVEMENT = new MovementComponent();

        TRANSFORM.LOCAL_ORIGIN.set(8, 8);
        TRANSFORM.width = 16;
        TRANSFORM.height = 16;
        TRANSFORM.POSITION.set(x, y);
        TRANSFORM.WORLD_ORIGIN.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                shadow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                legs = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("zombie/legs"),
                body = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("zombie/body" + MathUtils.random(1, 2)),
                head = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("zombie/head" + MathUtils.random(1, 2));
        shadow.setOriginCenter();
        shadow.setColor(Color.BLACK);
        shadow.setAlpha(0.5f);
        legs.setOrigin(16f, 14f);
        body.setOrigin(12f, 18f);
        head.setOrigin(8f, 7f);
        RENDER.SPRITES.add(shadow);
        RENDER.SPRITES.add(legs);
        RENDER.SPRITES.add(body);
        RENDER.SPRITES.add(head);
        RENDER.scaleX = 1.2f;
        RENDER.scaleY = 1.2f;
        RENDER.z = -1;

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                16, 0,
                16, 16,
                0, 16
        });
        COLLIDER.body.setOrigin(8, 8);
        COLLIDER.body.setPosition(x, y);
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void updateCollision(Entity e, Intersector.MinimumTranslationVector m, boolean solid) {
                if (solid) {
                    Vector2 mtv = m.normal.scl(m.depth);

                    TRANSFORM.POSITION.add(mtv);

                    if (AI.state == AIStates.WANDER && MOVEMENT.rotationSpeed == 0)
                        AI.randomTimer = 0;
                }
            }

            @Override
            public void enterCollision(Entity e2, Intersector.MinimumTranslationVector m, boolean solid) {
/*                    if (ai.state == AIStates.WANDER) {
                        ai.targetDirection = MathUtils.random(135f, 225f);
                        ai.randomTimer = (float)Math.random();
                    }*/
            }
        };

        E.add(TRANSFORM).add(RENDER).add(COLLIDER).add(HUMAN).add(AI).add(MOVEMENT);

        return E;
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
     * @param rot the direction the bullet should travel
     * @param spread    the degree of inaccuracy
     * @param lt  the amount of time before the bullet is destroyed (in seconds)
     * @return an {@link Entity} with the {@link Component}s of a bullet attached
     */
    public static Entity createBullet(float x, float y, float rot, float spread, float dmg, float lt, Entity o) {
        final Entity e = new Entity();
        TransformComponent transform = new TransformComponent();
        RenderComponent render = new RenderComponent();
        ColliderComponent collider = new ColliderComponent();
        HealthComponent life = new HealthComponent();
        final DamageComponent damage = new DamageComponent();
        MovementComponent movement = new MovementComponent();

        damage.damage = dmg;
        damage.owner = o;

        transform.width = 16;
        transform.height = 4;
        transform.LOCAL_ORIGIN.set(14, 2);
        transform.POSITION.set(x, y).sub(transform.LOCAL_ORIGIN);
        transform.rotation = rot + MathUtils.random(-spread / 2, spread / 2);

        movement.NORMAL.set(1, 0).rotate(transform.rotation);
        movement.translationSpeed = 1000f;

        Sprite main = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/bullet");
        main.setSize(transform.width, transform.height);
        main.setOrigin(transform.LOCAL_ORIGIN.x, transform.LOCAL_ORIGIN.y);
        main.setAlpha(1f);
        render.SPRITES.add(main);

        collider.solid = false;
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
                if (solid) {
                    if (Mapper.HEALTH_MAPPER.has(e2))
                        Mapper.HEALTH_MAPPER.get(e2).health -= damage.damage;

                    engine.removeEntity(e);
                }
            }
        };

        life.maxHealth = life.health = lt;

        e.add(transform).add(render).add(collider).add(life).add(damage).add(movement);

        return e;
    }

    /**
     * Creates a "slash" damage object that is short lived.
     * On touch it is destroyed immediately, dealing damage to the player touching it.
     *
     * @param x   the x-coordinate to spawn it at
     * @param y   the y-coordinate to spawn it at
     * @param r   the range of the attack
     * @param rot the rotation of the attack object
     * @return an {@link Entity} configured to be a damage object, placed at a specified location with a specified rotation
     */
    public static Entity createSlash(float x, float y, float r, float rot, float dmg, Entity o) {
        final Entity E = new Entity();
        final TransformComponent TRANSFORM = new TransformComponent(x, y, r, r);
        final HealthComponent LIFETIME = new HealthComponent(0.1f);
        final DamageComponent DAMAGE = new DamageComponent();
        final ColliderComponent COLLIDER = new ColliderComponent(null, null, false);

        TRANSFORM.LOCAL_ORIGIN.set(0, 0);
        TRANSFORM.WORLD_ORIGIN.set(x, y);
        TRANSFORM.rotation = rot - 45;

        DAMAGE.damage = dmg;
        DAMAGE.owner = o;

        COLLIDER.body = new Polygon(new float[]{
                r, r,
                r, 0,
                0, r
        });
        COLLIDER.body.setOrigin(r / 2, r / 2);
        COLLIDER.body.setPosition(x - COLLIDER.body.getOriginX(), y - COLLIDER.body.getOriginX());
        COLLIDER.body.setRotation(TRANSFORM.rotation);
        COLLIDER.handler = new CollisionHandler() {
            @Override
            public void updateCollision(Entity e, Intersector.MinimumTranslationVector mtv, boolean solid) {
                if (Mapper.HEALTH_MAPPER.has(e)) {
                    // If either there is no owner to the damage object OR
                    // The owner is not the one dealing the damage and its one of the following:
                    // - PLAYER > PLAYER
                    // - ZOMBIE > PLAYER
                    // - PLAYER > ZOMBIE
                    if (DAMAGE.owner == null || DAMAGE.owner != e && (Mapper.PLAYER_MAPPER.has(e) || Mapper.PLAYER_MAPPER.has(DAMAGE.owner) && Mapper.AI_MAPPER.has(e))) {
                        HealthComponent health = Mapper.HEALTH_MAPPER.get(e);

                        health.health -= DAMAGE.damage;
                        engine.removeEntity(E);
                    }
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
     * @return an {@link Entity} with the {@link Component}s of a bullet attached
     */
    private static Entity createItem(float lifeTime) {
        final Entity e = new Entity();
        final TransformComponent transform = new TransformComponent();
        RenderComponent render = new RenderComponent();
        ColliderComponent collider = new ColliderComponent();
        HealthComponent life = new HealthComponent(lifeTime);
        final MovementComponent movement = new MovementComponent();
        final ItemComponent item = new ItemComponent();

        render.z = -10;

        transform.rotation = MathUtils.random(360);

        movement.rotationSpeed = 30f;

        e.add(transform).add(render).add(collider).add(life).add(item).add(movement);

        return e;
    }

    /**
     * Creates a gun that can be picked up and fired.
     *
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a gun
     */
    private static Entity createGun(float lifeTime) {
        final Entity E = createItem(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);
        final WeaponComponent WEAPON = new WeaponComponent();
        final AmmoComponent AMMO = new AmmoComponent();

        ITEM.maxStack = 1;
        ITEM.handler = new ItemHandler() {
            @Override
            public String toString() {
                return AMMO.toString();
            }

            @Override
            public void update(float dt) {
                if (WEAPON.attackTimer > 0)
                    WEAPON.attackTimer -= dt * WEAPON.attackRate;

                if (AMMO.isReloading) {
                    AMMO.reloadTimer -= dt * AMMO.reloadRate;

                    if (AMMO.reloadTimer <= 0)
                        AMMO.reload();
                }

                if (AMMO.bulletsPerRound < 1)
                    AMMO.bulletsPerRound = 1;

                if (WEAPON.damage < 1)
                    WEAPON.damage = 1;
            }

            @Override
            public void pickup(Entity source) {
                Mapper.INVENTORY_MAPPER.get(source).pickup(E);
            }

            @Override
            public void use(Entity source) {
                if (!AMMO.isReloading && WEAPON.attackTimer <= 0 && AMMO.magAmmo > 0) {
                    TransformComponent sourceTransform = Mapper.TRANSFORM_MAPPER.get(source);
                    Vector2 spawn = new Vector2(1, 0);

                    spawn.rotate(sourceTransform.rotation).scl(16).add(sourceTransform.WORLD_ORIGIN);

                    for (int i = 0; i < AMMO.bulletsPerRound; i++)
                        engine.addEntity(GameObjects.createBullet(spawn.x, spawn.y, sourceTransform.rotation, WEAPON.spread, WEAPON.damage, 5, source));

                    AMMO.magAmmo -= 1;
                    WEAPON.attackTimer = 1;
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

                TRANSFORM.POSITION.set(x, y);
                ITEM.dropped = true;
            }
        };

        E.add(WEAPON).add(AMMO);

        return E;
    }

    /**
     * Creates a melee weapon that can be used to attack at a certain range.
     *
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a melee weapon
     */
    private static Entity createMeleeItem(float lifeTime) {
        final Entity E = createItem(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final WeaponComponent WEAPON = new WeaponComponent();

        ITEM.maxStack = 1;
        ITEM.stack = 1;
        ITEM.handler = new ItemHandler() {
            @Override
            public String toString() {
                return "";
            }

            @Override
            public void update(float dt) {
                if (WEAPON.attackTimer > 0)
                    WEAPON.attackTimer -= dt * WEAPON.attackRate;

                if (WEAPON.damage < 1)
                    WEAPON.damage = 1;
            }

            @Override
            public void pickup(Entity source) {
                Mapper.INVENTORY_MAPPER.get(source).pickup(E);
            }

            @Override
            public void use(Entity source) {
                if (WEAPON.attackTimer <= 0) {
                    TransformComponent sourceTransform = Mapper.TRANSFORM_MAPPER.get(source);
                    Vector2 spawn = new Vector2(1, 0);

                    spawn.rotate(sourceTransform.rotation).scl(16).add(sourceTransform.WORLD_ORIGIN);

                    engine.addEntity(GameObjects.createSlash(spawn.x, spawn.y, WEAPON.range, sourceTransform.rotation, WEAPON.damage, source));

                    WEAPON.attackTimer = 1;
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

                TRANSFORM.POSITION.set(x, y);
                ITEM.dropped = true;
            }
        };

        E.add(WEAPON);

        return E;
    }

    /**
     * Creates a knife that can be picked up and used.
     *
     * @param x        the x-coordinate to spawn the knife at
     * @param y        the y-coordinate to spawn the knife at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a knife
     */
    public static Entity createKnife(float x, float y, float lifeTime) {
        final Entity E = createMeleeItem(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);
        final WeaponComponent WEAPON = Mapper.WEAPON_MAPPER.get(E);

        TRANSFORM.LOCAL_ORIGIN.set(12, 3);
        TRANSFORM.width = 24;
        TRANSFORM.height = 6;
        TRANSFORM.POSITION.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                glow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                sprite = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("items/knife");

        glow.setOriginCenter();
        glow.setColor(Color.CHARTREUSE);
        glow.setAlpha(0.8f);
        sprite.setSize(TRANSFORM.width, TRANSFORM.height);
        sprite.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        sprite.setScale(1f);
        RENDER.SPRITES.add(glow);
        RENDER.SPRITES.add(sprite);

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                TRANSFORM.width, 0,
                TRANSFORM.width, TRANSFORM.height,
                0, TRANSFORM.height
        });
        COLLIDER.body.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        COLLIDER.solid = false;

        WEAPON.attackRate = 1.75f;
        WEAPON.damage = 20f;
        WEAPON.range = 16f;

        ITEM.name = "Knife";
        ITEM.auto = false;

        return E;
    }

    /**
     * Creates a gun that can be picked up and fired.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a gun
     */
    public static Entity createM4(float x, float y, float lifeTime) {
        final Entity E = createGun(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);
        final WeaponComponent WEAPON = Mapper.WEAPON_MAPPER.get(E);
        final AmmoComponent AMMO = Mapper.AMMO_MAPPER.get(E);

        TRANSFORM.LOCAL_ORIGIN.set(23, 7.5f);
        TRANSFORM.width = 46;
        TRANSFORM.height = 15;
        TRANSFORM.POSITION.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                glow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                sprite = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("items/rifle");

        glow.setOriginCenter();
        glow.setColor(Color.CHARTREUSE);
        glow.setAlpha(0.8f);
        sprite.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        sprite.setSize(TRANSFORM.width, TRANSFORM.height);
        sprite.setScale(1.5f);
        RENDER.SPRITES.add(glow);
        RENDER.SPRITES.add(sprite);

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                TRANSFORM.width, 0,
                TRANSFORM.width, TRANSFORM.height,
                0, TRANSFORM.height
        });
        COLLIDER.body.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        COLLIDER.solid = false;

        WEAPON.attackRate = 13.75f;
        WEAPON.damage = 20f;
        WEAPON.spread = 10f;

        AMMO.reloadRate = 1.5f;
        AMMO.maxAmmo = 120;
        AMMO.magSize = 30;

        AMMO.ammo = AMMO.maxAmmo;
        AMMO.magAmmo = AMMO.magSize;

        ITEM.name = "M4A1";
        ITEM.auto = true;

        return E;
    }

    /**
     * Creates a gun that can be picked up and fired.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a gun
     */
    public static Entity createBenelli(float x, float y, float lifeTime) {
        final Entity E = createGun(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);
        final WeaponComponent WEAPON = Mapper.WEAPON_MAPPER.get(E);
        final AmmoComponent AMMO = Mapper.AMMO_MAPPER.get(E);

        TRANSFORM.LOCAL_ORIGIN.set(23, 7.5f);
        TRANSFORM.width = 46;
        TRANSFORM.height = 15;
        TRANSFORM.POSITION.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                glow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                sprite = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("items/shotgun");

        glow.setSize(46, 46);
        glow.setOriginCenter();
        glow.setColor(Color.CHARTREUSE);
        glow.setAlpha(0.8f);
        sprite.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        sprite.setSize(TRANSFORM.width, TRANSFORM.height);
        sprite.setScale(1.5f);
        RENDER.SPRITES.add(glow);
        RENDER.SPRITES.add(sprite);

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                TRANSFORM.width, 0,
                TRANSFORM.width, TRANSFORM.height,
                0, TRANSFORM.height
        });
        COLLIDER.body.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        COLLIDER.solid = false;

        WEAPON.attackRate = 1.5f;
        WEAPON.damage = 10f;
        WEAPON.spread = 30f;
        WEAPON.range = 3f;

        AMMO.bulletsPerRound = 12;
        AMMO.reloadRate = 0.3f;
        AMMO.maxAmmo = 32;
        AMMO.magSize = 8;

        AMMO.ammo = AMMO.maxAmmo;
        AMMO.magAmmo = AMMO.magSize;

        ITEM.name = "Benelli M9";

        return E;
    }

    /**
     * Creates an ammo box used to reload the first weapon in your inventory.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a gun
     */
    public static Entity createAmmoBox(float x, float y, float lifeTime) {
        final Entity E = createItem(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);
        final AmmoComponent AMMO = new AmmoComponent();
        float scale = 0.75f;

        TRANSFORM.width = 39 * scale;
        TRANSFORM.height = 36 * scale;
        TRANSFORM.LOCAL_ORIGIN.set(TRANSFORM.width / 2, TRANSFORM.height / 2);
        TRANSFORM.POSITION.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                glow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                sprite = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("items/ammobox");

        glow.setOriginCenter();
        glow.setColor(Color.CHARTREUSE);
        glow.setAlpha(0.8f);
        sprite.setSize(TRANSFORM.width, TRANSFORM.height);
        sprite.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        RENDER.SPRITES.add(glow);
        RENDER.SPRITES.add(sprite);

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                TRANSFORM.width, 0,
                TRANSFORM.width, TRANSFORM.height,
                0, TRANSFORM.height
        });
        COLLIDER.body.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        COLLIDER.solid = false;

        ITEM.name = "Ammo Box";
        ITEM.maxStack = 1;
        ITEM.stack = 1;
        AMMO.ammo = MathUtils.random(10, 50);
        ITEM.handler = new ItemHandler() {
            @Override
            public String toString() {
                return "" + ITEM.stack + "/" + ITEM.maxStack;
            }

            @Override
            public void update(float dt) {

            }

            @Override
            public void pickup(Entity source) {
                Entity active = Mapper.INVENTORY_MAPPER.get(source).getActiveItem();

                if (active != null && Mapper.AMMO_MAPPER.has(active)) {
                    AmmoComponent weaponAmmo = Mapper.AMMO_MAPPER.get(active);

                    if (weaponAmmo.ammo < weaponAmmo.maxAmmo) {
                        weaponAmmo.ammo += AMMO.ammo;

                        if (weaponAmmo.ammo > weaponAmmo.maxAmmo) {
                            AMMO.ammo = weaponAmmo.ammo - weaponAmmo.maxAmmo;
                            weaponAmmo.ammo -= weaponAmmo.ammo - weaponAmmo.maxAmmo;
                        } else
                            engine.removeEntity(E);
                    }
                }
            }

            @Override
            public void use(Entity source) {

            }

            @Override
            public void useAlt(Entity source) {

            }

            @Override
            public void drop(Entity source) {

            }
        };

        return E;
    }

    /**
     * Creates a bandage that can be used to heal the player by 10 HP.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a bandage
     */
    public static Entity createBandage(float x, float y, float lifeTime) {
        final Entity E = createItem(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);
        final float scale = 0.75f;

        TRANSFORM.width = 32 * scale;
        TRANSFORM.height = 16 * scale;
        TRANSFORM.LOCAL_ORIGIN.set(TRANSFORM.width / 2, TRANSFORM.height / 2);
        TRANSFORM.POSITION.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                glow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                sprite = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("items/bandage");

        glow.setOriginCenter();
        glow.setColor(Color.CHARTREUSE);
        glow.setAlpha(0.8f);
        sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
        sprite.setOriginCenter();
        RENDER.scaleX = RENDER.scaleY = scale;
        RENDER.SPRITES.add(glow);
        RENDER.SPRITES.add(sprite);

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                TRANSFORM.width, 0,
                TRANSFORM.width, TRANSFORM.height,
                0, TRANSFORM.height
        });
        COLLIDER.body.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        COLLIDER.solid = false;

        ITEM.name = "Bandage";
        ITEM.maxStack = 8;
        ITEM.stack = 1;
        ITEM.auto = false;
        ITEM.handler = new ItemHandler() {
            @Override
            public String toString() {
                return "" + ITEM.stack + "/" + ITEM.maxStack;
            }

            @Override
            public void update(float dt) {

            }

            @Override
            public void pickup(Entity source) {
                Mapper.INVENTORY_MAPPER.get(source).pickup(E);
            }

            @Override
            public void use(Entity source) {
                if (Mapper.HEALTH_MAPPER.has(source)) {
                    HealthComponent health = Mapper.HEALTH_MAPPER.get(source);

                    if (health.health < health.maxHealth) {
                        health.health += 10;
                        ITEM.stack--;
                    }
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

                TRANSFORM.POSITION.set(x, y);
                ITEM.dropped = true;
            }
        };

        return E;
    }

    /**
     * Creates a dressing that can be used to heal the player by 20 HP.
     *
     * @param x        the x-coordinate to spawn the gun at
     * @param y        the y-coordinate to spawn the gun at
     * @param lifeTime the amount of time before the item despawns (in seconds).
     * @return an item {@link Entity} with the proper {@link Component}s for a dressing
     */
    public static Entity createDressing(float x, float y, float lifeTime) {
        final Entity E = createItem(lifeTime);
        final ItemComponent ITEM = Mapper.ITEM_MAPPER.get(E);
        final TransformComponent TRANSFORM = Mapper.TRANSFORM_MAPPER.get(E);
        final ColliderComponent COLLIDER = Mapper.COLLIDER_MAPPER.get(E);
        final RenderComponent RENDER = Mapper.RENDER_MAPPER.get(E);

        TRANSFORM.width = 32;
        TRANSFORM.height = 32;
        TRANSFORM.LOCAL_ORIGIN.set(TRANSFORM.width / 2, TRANSFORM.height / 2);
        TRANSFORM.POSITION.set(x, y).sub(TRANSFORM.LOCAL_ORIGIN);

        Sprite
                glow = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("misc/glow"),
                sprite = ((TextureAtlas) assets.MANAGER.get(Assets.GameObjects.ATLAS)).createSprite("items/dressing");

        glow.setOriginCenter();
        glow.setColor(Color.CHARTREUSE);
        glow.setAlpha(0.8f);
        sprite.setSize(TRANSFORM.width, TRANSFORM.height);
        sprite.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        RENDER.SPRITES.add(glow);
        RENDER.SPRITES.add(sprite);

        COLLIDER.body = new Polygon(new float[]{
                0, 0,
                TRANSFORM.width, 0,
                TRANSFORM.width, TRANSFORM.height,
                0, TRANSFORM.height
        });
        COLLIDER.body.setOrigin(TRANSFORM.LOCAL_ORIGIN.x, TRANSFORM.LOCAL_ORIGIN.y);
        COLLIDER.solid = false;

        ITEM.name = "Dressing";
        ITEM.maxStack = 8;
        ITEM.stack = 1;
        ITEM.auto = false;
        ITEM.handler = new ItemHandler() {
            @Override
            public String toString() {
                return "" + ITEM.stack + "/" + ITEM.maxStack;
            }

            @Override
            public void update(float dt) {

            }

            @Override
            public void pickup(Entity source) {
                Mapper.INVENTORY_MAPPER.get(source).pickup(E);
            }

            @Override
            public void use(Entity source) {
                if (Mapper.HEALTH_MAPPER.has(source)) {
                    HealthComponent health = Mapper.HEALTH_MAPPER.get(source);

                    if (health.health < health.maxHealth) {
                        health.health += 10;
                        ITEM.stack--;
                    }
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

                TRANSFORM.POSITION.set(x, y);
                ITEM.dropped = true;
            }
        };

        return E;
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
            float
                x,
                y,
                w,
                h,
                min = 120,
                max = 180;
            SpawnerType t = SpawnerType.ITEM;

            x = (Float) properties.get("x");
            y = (Float) properties.get("y");
            w = (Float) properties.get("width");
            h = (Float) properties.get("height");

            try {
                t = SpawnerType.valueOf(((String) properties.get("type")).toUpperCase());
                min = (Float) properties.get("minTime");
                max = (Float) properties.get("maxTime");
            } catch (Exception e) {
                Gdx.app.log("MAP", "ERROR: " + e.getMessage());
            } finally {
                mapEntities.add(createSpawner(x, y, w, h, min, max, t));
            }
        }

        mapEntities.add(main);

        return mapEntities;
    }

    /**
     * Creates a spawner that spawns a certain group of {@link Entity}s.
     *
     * @param x       the x-coordinate of the spawner
     * @param y       the y-coordinate of the spawner
     * @param w       the width of the spawner
     * @param h       the height of the spawner
     * @param t       the {@link SpawnerType}
     * @param minTime the minimum time it will take for the spawner to spawn something
     * @param maxTime the maximum time it will take for the spawner to spawn something
     * @return a spawner {@link Entity} that spawns a set of {@link Entity}s
     *         in a defined region
     */
    public static Entity createSpawner(float x, float y, float w, float h, float minTime, float maxTime, SpawnerType t) {
        final Entity e = new Entity();
        TransformComponent transform = new TransformComponent(x, y);
        SpawnerComponent spawner = new SpawnerComponent(minTime, maxTime, t);

        transform.width = w;
        transform.height = h;
        transform.LOCAL_ORIGIN.set(w / 2, h / 2);

        e.add(transform).add(spawner);

        return e;
    }
}
