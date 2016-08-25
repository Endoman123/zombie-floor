package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

/**
 * @author Jared Tulayan
 */
public class RenderComponent implements Component {
    public ArrayList<Sprite> SPRITES;
    public int z;

    public RenderComponent() {
        SPRITES = new ArrayList<Sprite>();
    }
}
