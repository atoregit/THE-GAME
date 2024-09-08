package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BulletBoss {
    private Vector2 position;
    private Rectangle bounds;
    private float health;
    private float maxHealth;
    private float speed;
    private float shootTimer;
    private static final float SHOOT_INTERVAL = 2f;
    private TextureAtlas atlas;
    private Animation<TextureRegion> animation;
    private float elapsedTime = 0f;
    private int height;
    private int width;
    public BulletBoss(float x, float y, float difficultyMultiplier) {
        position = new Vector2(x, y);
        atlas = new TextureAtlas(Gdx.files.internal("boss/boss.atlas"));
        Array<TextureRegion> frames = new Array<TextureRegion>();
        int frameCount = 28;

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion("frame" + i);
            if (region != null) {
                frames.add(region);
            } else {
                Gdx.app.error("Animation Error", "Could not find region: frame" + i);
            }
        }
        animation = new Animation<TextureRegion>(0.07f, frames, Animation.PlayMode.LOOP);
        TextureRegion currentFrame = animation.getKeyFrame(elapsedTime, true);

        width = currentFrame.getRegionWidth();
        height = currentFrame.getRegionHeight();
        bounds = new Rectangle(x, y, width, height);
        maxHealth = 1000 * difficultyMultiplier;
        health = maxHealth;
        speed = 65 * difficultyMultiplier;
        shootTimer = 0;
    }

    public void update(float delta) {
        // Implement boss movement pattern here
        // For example, move left and right:
        elapsedTime += delta;
        position.x += speed * delta;
        if (position.x < 0 || position.x >300) {
            speed = -speed;
        }
        bounds.setPosition(position);

        shootTimer += delta;
    }

    public boolean canShoot() {
        return shootTimer >= SHOOT_INTERVAL;
    }

    public void resetShootTimer() {
        shootTimer = 0;
    }

    public void hit(float damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(elapsedTime, true), position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
    public int getWidth(){
        return width;
    }
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }
    public void dispose() {
        atlas.dispose();
    }
}
