package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
    private float maxHealth;
    private Color tint;

    public BulletEnemy(float x, float y, float difficulty) {
        position = new Vector2(x, y);

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

        // Create a new texture from the resized pixmap
        texture = new Texture(resizedPixmap);

        // Set up the bounds
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());

        // Dispose of the pixmaps and original texture as they're no longer needed
        resizedPixmap.dispose();
        originalPixmap.dispose();
        originalTexture.dispose();

        speed = 140 * difficulty;
        maxHealth = 50 * difficulty;
        health = maxHealth;
        tint = new Color(Color.WHITE);
    }

    public void init(float x, float y, float difficultyMultiplier) {
        this.position.set(x, y);
        this.bounds.setPosition(position);
        this.maxHealth = 100 * difficultyMultiplier;
        this.health = this.maxHealth;
        this.tint.set(Color.WHITE);
        // Reset any other necessary fields
    }

    public void update(float delta) {
        position.y -= speed * delta;
        bounds.setPosition(position);
        updateTint();
    }

    private void updateTint() {
        float healthPercentage = health / maxHealth;
        float redIntensity = 1f - healthPercentage;
        tint.set(1f, 1f - redIntensity, 1f - redIntensity, 0.2f);
    }

    public void draw(SpriteBatch batch) {
        Color originalColor = batch.getColor();
        batch.setColor(tint);
        batch.draw(texture, position.x, position.y);
        batch.setColor(originalColor);
    }

    public void hit(float damage) {
        health -= damage;
        if (health < 0) health = 0;
        updateTint();
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
