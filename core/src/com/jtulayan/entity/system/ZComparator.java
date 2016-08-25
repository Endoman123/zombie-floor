package com.jtulayan.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.jtulayan.entity.Mapper;
import com.jtulayan.entity.component.RenderComponent;

import java.util.Comparator;

/**
 * @author Jared Tulayan
 */
public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<RenderComponent> rm;

    public ZComparator() {
        rm = Mapper.RENDER_MAPPER;
    }

    @Override
    public int compare(Entity e1, Entity e2) {
        return (int) Math.signum(rm.get(e1).z - rm.get(e2).z);
    }
}
