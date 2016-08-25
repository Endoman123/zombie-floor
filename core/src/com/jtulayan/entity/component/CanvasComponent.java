package com.jtulayan.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jtulayan.handler.GUIHandler;

/**
 * A {@link Component} with a {@link Stage} with the intended use as a GUI canvas.
 * @author Jared Tulayan
 */
public class CanvasComponent implements Component {
    public final Stage CANVAS;
    public GUIHandler handler;

    public CanvasComponent(Viewport v, Batch b){
        CANVAS = new Stage(v, b);
    }
}
