package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.math.Rectangle;
public class ChemicalSymbol {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private String symbol;
    private float speed;

    public ChemicalSymbol(String symbol, float x, float y) {
        this.symbol = symbol;
        position = new Vector2(x, y);
        texture = new Texture(Gdx.files.internal("elements/Al.png"));
        bounds = new Rectangle((int)x, (int)y, texture.getWidth(), texture.getHeight());
        speed = 120;
    }
    public void init(float x, float y) {
        position = new Vector2(x, y);
        this.bounds.setPosition(position);
        // Reset any other necessary fields
    }
    public void update(float delta) {
        position.y -= speed * delta;
        bounds.setPosition(position);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
          // 50x50 white square
        this.texture.dispose();  // Dispose old texture
        this.texture = new Texture(Gdx.files.internal("elements/"+symbol+".png"));
    }

    public String getSymbol() {
        return symbol;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return position.y;
    }

    public float getHeight() {
        return texture.getHeight();
    }

    public void dispose() {
        texture.dispose();
    }
}
