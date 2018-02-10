package com.jtulayan.input;

import com.badlogic.gdx.InputAdapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jared Tulayan
 */
public class Input extends InputAdapter {
    private static final Map<String, Bind> BINDS;
    private static final Map<String, Axis> AXES;

    static {
        BINDS = new HashMap<String, Bind>();
        AXES = new HashMap<String, Axis>();
    }

    @Override
    public boolean keyDown(int keycode) {
        Iterator<Map.Entry<String, Bind>> it = BINDS.entrySet().iterator();
        while (it.hasNext()) {
            Bind b = it.next().getValue();
            int[] vals = b.getValues();

            if (vals[0] == keycode || vals[1] == keycode) {
                b.setPressed(true);
                b.setReleased(false);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Iterator<Map.Entry<String, Bind>> it = BINDS.entrySet().iterator();
        while (it.hasNext()) {
            Bind b = it.next().getValue();
            int[] vals = b.getValues();

            if (vals[0] == keycode || vals[1] == keycode) {
                b.setReleased(true);
                b.setPressed(false);
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
