package com.jtulayan.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.EntitySystem;
import com.jtulayan.entity.component.*;

/**
 * Static class mainly being used to access mappers to {@link ComponentMapper}s for all classes to use.
 * This is just a handy class to have so that you don't need to reuse the same lines in {@link EntitySystem}s.
 *
 * @author Jared Tulayan
 */
public class Mapper {
    public static final ComponentMapper<AIComponent> AI_MAPPER;
    public static final ComponentMapper<ColliderComponent> COLLIDER_MAPPER;
    public static final ComponentMapper<HealthComponent> HEALTH_MAPPER;
    public static final ComponentMapper<RenderComponent> RENDER_MAPPER;
    public static final ComponentMapper<TransformComponent> TRANSFORM_MAPPER;
    public static final ComponentMapper<PlayerComponent> PLAYER_MAPPER;
    public static final ComponentMapper<MovementComponent> MOVEMENT_MAPPER;
    public static final ComponentMapper<DamageComponent> DAMAGE_MAPPER;
    public static final ComponentMapper<ItemComponent> ITEM_MAPPER;
    public static final ComponentMapper<InventoryComponent> INVENTORY_MAPPER;
    public static final ComponentMapper<WeaponComponent> WEAPON_MAPPER;
    public static final ComponentMapper<AmmoComponent> AMMO_MAPPER;
    public static final ComponentMapper<MapComponent> MAP_MAPPER;
    public static final ComponentMapper<InputComponent> INPUT_MAPPER;
    public static final ComponentMapper<CanvasComponent> CANVAS_MAPPER;
    public static final ComponentMapper<SpawnerComponent> SPAWNER_MAPPER;

    static {
        AI_MAPPER = ComponentMapper.getFor(AIComponent.class);
        COLLIDER_MAPPER = ComponentMapper.getFor(ColliderComponent.class);
        HEALTH_MAPPER = ComponentMapper.getFor(HealthComponent.class);
        RENDER_MAPPER = ComponentMapper.getFor(RenderComponent.class);
        TRANSFORM_MAPPER = ComponentMapper.getFor(TransformComponent.class);
        PLAYER_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
        MOVEMENT_MAPPER = ComponentMapper.getFor(MovementComponent.class);
        DAMAGE_MAPPER = ComponentMapper.getFor(DamageComponent.class);
        ITEM_MAPPER = ComponentMapper.getFor(ItemComponent.class);
        INVENTORY_MAPPER = ComponentMapper.getFor(InventoryComponent.class);
        WEAPON_MAPPER = ComponentMapper.getFor(WeaponComponent.class);
        AMMO_MAPPER = ComponentMapper.getFor(AmmoComponent.class);
        MAP_MAPPER = ComponentMapper.getFor(MapComponent.class);
        INPUT_MAPPER = ComponentMapper.getFor(InputComponent.class);
        CANVAS_MAPPER = ComponentMapper.getFor(CanvasComponent.class);
        SPAWNER_MAPPER = ComponentMapper.getFor(SpawnerComponent.class);
    }
}
