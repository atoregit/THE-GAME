package io.github.thegame;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;
public class BulletEnemy {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private float speed;
    private float health;

    public BulletEnemy(float x, float y, float difficulty) {
        position = new Vector2(x, y);
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);  // 50x50 white square
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fillRectangle(0, 0, 30, 30);
        texture = new Texture(pixmap);
        pixmap.dispose();
        bounds = new Rectangle((int)x, (int)y, texture.getWidth(), texture.getHeight());
        speed = 100 * difficulty;
        health = 50 * difficulty;
    }
    public void init(float x, float y, float difficultyMultiplier) {
        this.position.set(x, y);
        this.bounds.setPosition(position);
        this.health = 100 * difficultyMultiplier; // Adjust as needed
        // Reset any other necessary fields
    }
    public void update(float delta) {
        position.y -= speed * delta;
        bounds.setPosition(position);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void hit(float damage) {
        health -= damage;
    }

    public float getHealth() {
        return health;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX(){ return position.x;}
    public float getY() {
        return position.y;
    }

    public float getHeight() {
        return texture.getHeight();
    }

    public float getWidth() {
        return texture.getWidth();
    }

    public void dispose() {
        texture.dispose();
    }
}
