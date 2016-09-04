package com.jtulayan.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Wrapper class for {@link Animation}s.
 *
 * @author Jared Tulayan
 */
public abstract class SpriteAnimation {
    public final Animation ANIMATION;
    public boolean isLooping;
    protected float stateTime;

    public SpriteAnimation(float frameDuration, TextureRegion... frames) {
        ANIMATION = new Animation(frameDuration, frames);
    }

    public SpriteAnimation(float frameDuration, Array<TextureRegion> frames) {
        ANIMATION = new Animation(frameDuration, frames);
    }

    public SpriteAnimation(float frameDuration, Array<TextureRegion> frames, Animation.PlayMode playMode) {
        ANIMATION = new Animation(frameDuration, frames, playMode);
    }

    public SpriteAnimation(float frameDuration, Animation.PlayMode playMode, TextureRegion... frames) {
        ANIMATION = new Animation(frameDuration, new Array<TextureRegion>(frames.clone()), playMode);
    }

    public void update(float dt) {
        stateTime += dt;
        changeFrame(ANIMATION.getKeyFrame(stateTime, isLooping));
    }

    public abstract void changeFrame(TextureRegion frame);
}
