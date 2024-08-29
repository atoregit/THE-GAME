package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Main extends com.badlogic.gdx.Game {

    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // use libGDX's default Arial font
        this.setScreen(new MainMenuScreen(this));

        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor.png"));
        int xHotspot = 5, yHotspot = 5;
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        pixmap.dispose();
        Gdx.graphics.setCursor(cursor);

    }

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}
