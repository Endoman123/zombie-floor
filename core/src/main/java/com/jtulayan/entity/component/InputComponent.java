package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Jared Tulayan
 */
public class InputComponent implements Component {
    public InputAdapter processor;
    public final Vector2 MOUSE_POSITION;

    public InputComponent() {
        MOUSE_POSITION = new Vector2();
    }
}
