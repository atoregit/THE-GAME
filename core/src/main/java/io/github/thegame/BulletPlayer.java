package io.github.thegame;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class BulletPlayer {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private float speed;
    private String collectedSymbol;
    private float fireRate = 0.2f;
    private float bulletDamage;
    private int numBullets;
    private float health;
    private boolean isStunned;

    public BulletPlayer(float x, float y) {
        position = new Vector2(x, y);
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);  // 50x50 white square
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fillRectangle(0, 0, 30, 30);
        texture = new Texture(pixmap);
        pixmap.dispose();
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        speed = 210;
        collectedSymbol = "";
        fireRate = 0.1f;
        bulletDamage = 50;
        numBullets = 1;
        health = 3;
        this.isStunned = false;
    }

    public void update(float delta) {
        if(!isStunned){
            bounds.setPosition(position);
        }
    }

    public void moveLeft(float delta) {
        position.x -= speed * delta;
        position.x = MathUtils.clamp(position.x, 0, 480 - texture.getWidth());
    }

    public void moveRight(float delta) {
        position.x += speed * delta;
        position.x = MathUtils.clamp(position.x, 0, 480 - texture.getWidth());
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }


    public float getFireRate() { return fireRate; }
    public void setFireRate(float fireRate) { this.fireRate = fireRate; }
    public float getBulletDamage() { return bulletDamage; }
    public void setBulletDamage(float bulletDamage) { this.bulletDamage = bulletDamage; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public int getNumBullets() { return numBullets; }
    public void setNumBullets(int numBullets) { this.numBullets = numBullets; }
    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }
    public boolean isStunned() { return isStunned; }
    public void setStunned(boolean stunned) { isStunned = stunned; }

    public Rectangle getBounds() {
        return bounds;
    }


    public float getX() {
        return bounds.getX();
    }

    public float getY() {
        return bounds.getY();
    }

    public float getWidth() {
        return bounds.getWidth();
    }

    public float getHeight() {
        return bounds.getHeight();
    }
    public void dispose() {
        texture.dispose();
    }
}
