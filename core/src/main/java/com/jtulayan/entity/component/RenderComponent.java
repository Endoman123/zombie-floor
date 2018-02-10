package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.jtulayan.util.SpriteAnimation;

/**
 * @author Jared Tulayan
 */
public class RenderComponent implements Component {
    public final Array<Sprite> SPRITES;
    public final Array<SpriteAnimation> ANIMATIONS;
    public float scaleX, scaleY;
    public int z;

    public RenderComponent() {
        SPRITES = new Array<Sprite>(true, 1, Sprite.class);
        ANIMATIONS = new Array<SpriteAnimation>(true, 1, SpriteAnimation.class);
        scaleX = 1;
        scaleY = 1;
    }
}
