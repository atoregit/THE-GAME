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
    private Texture texture1, texture2, texture3;
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

        // Create a Pixmap from the original texture
        Pixmap originalPixmap = new Pixmap(Gdx.files.internal("playerShoot.png"));
        Pixmap originLeft = new Pixmap(Gdx.files.internal("playerShootLeft.png"));
        Pixmap originRight = new Pixmap(Gdx.files.internal("playerShootRight.png"));

        // Create a new Pixmap with the desired size (50x50)
        Pixmap resizedPixmap1 = new Pixmap(70, 80, originalPixmap.getFormat());
        Pixmap resizedPixmap2 = new Pixmap(70, 80, originLeft.getFormat());
        Pixmap resizedPixmap3 = new Pixmap(70, 80, originRight.getFormat());
        // Scale the original pixmap to the new pixmap
        resizedPixmap1.drawPixmap(originalPixmap,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, resizedPixmap1.getWidth(), resizedPixmap1.getHeight()
        );
        resizedPixmap2.drawPixmap(originLeft,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, resizedPixmap2.getWidth(), resizedPixmap2.getHeight()
        );
        resizedPixmap3.drawPixmap(originRight,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, resizedPixmap3.getWidth(), resizedPixmap3.getHeight()
        );

        // Create a new texture from the resized pixmap
        texture1 = new Texture(resizedPixmap1);
        texture2 = new Texture(resizedPixmap2);
        texture3 = new Texture(resizedPixmap3);
        originalPixmap.dispose();
        originLeft.dispose();
        originRight.dispose();
        resizedPixmap1.dispose();
        resizedPixmap2.dispose();
        resizedPixmap3.dispose();
        bounds = new Rectangle(x, y, texture1.getWidth(), texture1.getHeight());
        speed = 210;
        collectedSymbol = "";
        fireRate = 0.1f;
        bulletDamage = 50;
        numBullets = 1;
        health = 3;
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
        position.x = MathUtils.clamp(position.x, 0, 480 - texture1.getWidth());
    }

    public void moveRight(float delta) {
        position.x += speed * delta;
        position.x = MathUtils.clamp(position.x, 0, 480 - texture1.getWidth());
    }

    public void draw(SpriteBatch batch, int state) {
        if(state == 1){
            batch.draw(texture1, position.x, position.y + 10);
        }else if(state == 2){
            batch.draw(texture2, position.x, position.y + 10);
        }else if(state == 3){
            batch.draw(texture3, position.x, position.y+ 10);
        }
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
    public void heal(){
        setHealth(getHealth()+1);
    }
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
        texture1.dispose();
        texture2.dispose();
        texture3.dispose();
    }

}
