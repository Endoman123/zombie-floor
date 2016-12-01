package com.jtulayan.main.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.*;
import com.jtulayan.main.ZombieFloor;
import com.jtulayan.util.ScoreComparator;
import com.jtulayan.util.ScoreEntry;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Jared Tulayan
 */
public class HighScoreScreen extends ScreenAdapter {
    private final ZombieFloor PARENT;
    private final Stage STAGE;

    public HighScoreScreen(ZombieFloor p) {
        super();
        PARENT = p;

        STAGE = new Stage(PARENT.getViewport(), PARENT.getBatch());
    }

    @Override
    public void show() {
        final VisTable TABLE = new VisTable();
        TABLE.center().pad(20, 256, 20, 256).setFillParent(true);
        TABLE.add("HIGH SCORES").colspan(2).row();
        TABLE.addSeparator().colspan(2);
        TABLE.add("NAME").expandX();
        TABLE.add("SCORE").expandX().row();

        final Array<ScoreEntry> map = getScores();

        for (ScoreEntry s : map) {
            TABLE.add(s.NAME).align(Align.center).expand();
            TABLE.add("" + s.SCORE).align(Align.center).expand().row();
        }

        STAGE.addActor(TABLE);
        STAGE.setDebugAll(true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        STAGE.draw();
    }

    @Override
    public void resize(int width, int height) {
        PARENT.getViewport().update(width, height, true);
    }

    private Array<ScoreEntry> getScores() {
        FileHandle scoreFile = Gdx.files.local("highscore.json");
        Array<ScoreEntry> scoreMap = new Array<ScoreEntry>();
        Json scoreJSON = new Json();

        try {
            String json = scoreFile.readString();
            JsonReader reader = new JsonReader();
            JsonValue val = reader.parse(json);

            for (JsonValue v : val) {
                scoreMap.add(new ScoreEntry(v.getString("NAME"), v.getInt("SCORE", 0)));
            }
        } catch (Exception e) {
            Gdx.app.log("EXCEPTION", e.toString());
            scoreMap = new Array<ScoreEntry>(true, 10, ScoreEntry.class);
            for (int i = 0; i < 10; i++) {
                scoreMap.add(new ScoreEntry("", 0));
            }

            String pretty = scoreJSON.prettyPrint(scoreMap);

            scoreFile.writeString(pretty, false);
        }

        scoreMap.sort(new ScoreComparator());
        scoreMap.truncate(10);

        return scoreMap;
    }
}
