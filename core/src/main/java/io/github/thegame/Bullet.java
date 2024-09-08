package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private float speed;
    private float damage;
    private Texture defaultTexture;
    private Texture hhTexture;
    private Texture hclTexture;
    private Texture hoTexture;
    private Texture hnaTexture;
    private Texture naclTexture;
    private Texture naoTexture;




    private static Texture loadTexture(String fileName) {
        Pixmap originalPixmap = new Pixmap(Gdx.files.internal(fileName));
        Pixmap resizedPixmap = new Pixmap(30, 37, originalPixmap.getFormat());
        resizedPixmap.drawPixmap(originalPixmap,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, resizedPixmap.getWidth() , resizedPixmap.getHeight()
        );
        Texture texture = new Texture(resizedPixmap);
        resizedPixmap.dispose();
        originalPixmap.dispose();
        return texture;
    }

    public Bullet(float x, float y, float damage) {
        defaultTexture = loadTexture("bulletPlayer.png");
        hhTexture = loadTexture("bulletPlayerBluer.png");
        hclTexture = loadTexture("bulletPlayerGreen.png");
        hoTexture = loadTexture("bulletPlayerRed.png");
        hnaTexture = loadTexture("bulletPlayerMaroon.png");
        naclTexture = loadTexture("bulletPlayerWhite.png");
        naoTexture = loadTexture("bulletPlayerViolet.png");
        position = new Vector2(x, y);
        texture = defaultTexture;
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
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        bounds.setPosition(position);
    }

    public void setTexture(String powerUp) {
        switch (powerUp) {
            case "HH":
                texture = hhTexture;
                break;
            case "HCl":
            case "ClH":
                texture = hclTexture;
                break;
            case "HO":
            case "OH":
                texture = hoTexture;
                break;
            case "HNa":
            case "NaH":
                texture = hnaTexture;
                break;
            case "NaCl":
            case "ClNa":
                texture = naclTexture;
                break;
            case "NaO":
            case "ONa":
                texture = naoTexture;
                break;
            default:
                texture = defaultTexture;
        }
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
        defaultTexture.dispose();
        hhTexture.dispose();
        hclTexture.dispose();
        hoTexture.dispose();
        hnaTexture.dispose();
        naclTexture.dispose();
        naoTexture.dispose();
        texture.dispose();
    }
}
