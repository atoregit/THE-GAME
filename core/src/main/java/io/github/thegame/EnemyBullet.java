package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
public class EnemyBullet {
    private static final float SPEED = 200f;
    private Vector2 position;
    private Rectangle bounds;
    private Texture bullet;

    public EnemyBullet(float x, float y) {
        position = new Vector2(x, y);
        Pixmap originalPixmap = new Pixmap(Gdx.files.internal("bulletTextile.png"));

        // Create a new Pixmap with the desired size (50x50)
        Pixmap resizedPixmap = new Pixmap(15, 35, originalPixmap.getFormat());

        // Scale the original pixmap to the new pixmap
        resizedPixmap.drawPixmap(originalPixmap,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, resizedPixmap.getWidth(), resizedPixmap.getHeight()
        );
        // Create a new texture from the resized pixmap
        bullet  = new Texture(resizedPixmap);

        // Set up the bounds
        bounds = new Rectangle(x, y, bullet.getWidth(), bullet.getHeight());

        // Dispose of the pixmaps and original texture as they're no longer needed
        resizedPixmap.dispose();
        originalPixmap.dispose();
    }
    public void init(float x, float y){
        position.x = x;
        position.y = y;
    }
    public void update(float delta) {
        position.y -= SPEED * delta;
        bounds.setPosition(position);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), position.x, position.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return position.y;
    }

    private Texture getTexture() {
        // You should create and store this texture somewhere else, like in your asset manager
        return bullet;
    }
    public void dispose(){
        bullet.dispose();
    }

}
