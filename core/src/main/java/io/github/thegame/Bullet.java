package io.github.thegame;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private float speed;
    private float damage;

    public Bullet(float x, float y, float damage) {
        position = new Vector2(x, y);
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);  // 50x50 white square
        pixmap.setColor(1, 1, 1, 1);  // Set the color to white
        pixmap.fillRectangle(0, 0, 25, 25);
        texture = new Texture(pixmap);
        pixmap.dispose();
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        speed = 300;
        this.damage = damage;
    }

    public void update(float delta) {
        position.y += speed * delta;
        bounds.setPosition(position);
    }
    public void init(float x, float y, float damage) {
        this.position.set(x, y);
        this.bounds.setPosition(position);
        this.damage = damage;
        // Reset any other necessary fields
    }
    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(position);
    }

    public float getWidth() {
        return texture.getWidth();
    }

    public float getHeight() {
        return texture.getHeight();
    }
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getDamage() {
        return damage;
    }

    public float getY() {
        return position.y;
    }

    public void dispose() {
        texture.dispose();
    }
}
