package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;
public class BulletEnemy {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private float speed;
    private float health;
    private int type;
    private float shootTimer;
    private static final float SHOOT_INTERVAL = 2f;
    public BulletEnemy(float x, float y, float difficulty, int types) {
        position = new Vector2(x, y);
        type = types;

        Texture originalTexture = new Texture(Gdx.files.internal("enemy.png"));

        // Create a Pixmap from the original texture
        Pixmap originalPixmap = new Pixmap(Gdx.files.internal("enemy.png"));

        // Create a new Pixmap with the desired size (50x50)
        Pixmap resizedPixmap = new Pixmap(50, 50, originalPixmap.getFormat());

        // Scale the original pixmap to the new pixmap
        resizedPixmap.drawPixmap(originalPixmap,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, resizedPixmap.getWidth(), resizedPixmap.getHeight()
        );
        shootTimer = MathUtils.random(0, SHOOT_INTERVAL);
        // Create a new texture from the resized pixmap
        texture = new Texture(resizedPixmap);

        // Set up the bounds
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());

        // Dispose of the pixmaps and original texture as they're no longer needed
        resizedPixmap.dispose();
        originalPixmap.dispose();
        originalTexture.dispose();


    }

    public void init(float x, float y, float difficultyMultiplier, int type) {
        this.position.set(x, y);
        this.bounds.setPosition(position);
        this.type = type;
        if(type == 1){
            //normal
            speed = 140;
            health = 50;

        }else if(type == 2){
            speed = 250;
            health = 25;
        }else if(type == 3){
            //textile waste
        }else if(type == 4){

        }else if(type == 5){
            speed = 140;
            health = 50;
        }
        // Reset any other necessary fields
    }

    public void update(float delta) {
        position.y -= speed * delta;
        bounds.setPosition(position);
        if(type == 5){
            shootTimer += delta;
        }
    }
    public EnemyBullet shoot() {
        return new EnemyBullet(position.x + bounds.width / 2, position.y);
    }
    public boolean canShoot() {
        if (shootTimer >= SHOOT_INTERVAL) {
            shootTimer = 0;
            return true;
        }
        return false;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void hit(float damage) {
        health -= damage;
        if (health < 0) health = 0;
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
