package io.github.thegame;

import com.badlogic.gdx.Gdx;
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
    private boolean invincible;
    private boolean isGlowing;
    private float glowDuration;
    private static final float MAX_GLOW_DURATION = 10f;

    public BulletPlayer(float x, float y) {
        position = new Vector2(x, y);

        texture = new Texture(Gdx.files.internal("playerShoot.png"));
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        speed = 210;
        collectedSymbol = "";
        fireRate = 0.1f;
        bulletDamage = 50;
        numBullets = 1;
        health = 12;
        this.isStunned = false;
        invincible = false;
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

    public void stopMoving() {
       return;
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
    public void setInvincible(boolean tpy) {
        invincible = tpy;
    }
    public boolean getInvincible() {
        return invincible;
    }
    public void startGlowing() {
        isGlowing = true;
        glowDuration = MAX_GLOW_DURATION;
    }

    public void updateGlow(float delta) {
        if (isGlowing) {
            glowDuration -= delta;
            if (glowDuration <= 0) {
                isGlowing = false;
            }
        }
    }

    public boolean isGlowing() {
        return isGlowing;
    }
    public float getHeight() {
        return bounds.getHeight();
    }
    public void dispose() {
        texture.dispose();
    }

}
