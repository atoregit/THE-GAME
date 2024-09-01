package io.github.thegame;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BulletDamage {
    private Vector2 position;
    private float alpha;
    private String damageText;
    private static final float FADE_SPEED = 1f;
    private static final float RISE_SPEED = 50f;

    public BulletDamage(float x, float y, int damage) {
        position = new Vector2(x, y);
        alpha = 1f;
        damageText = String.valueOf(damage);
    }

    public void update(float delta) {
        alpha -= FADE_SPEED * delta;
        position.y += RISE_SPEED * delta;
    }

    public void draw(SpriteBatch batch, BitmapFont font) {
        Color oldColor = font.getColor();
        font.setColor(Color.RED.r, Color.RED.g, Color.RED.b, alpha);
        font.draw(batch, damageText, position.x, position.y);
        font.setColor(oldColor);
    }

    public boolean isExpired() {
        return alpha <= 0;
    }
}
