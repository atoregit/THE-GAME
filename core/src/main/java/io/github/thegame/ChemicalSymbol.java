package io.github.thegame;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


import com.badlogic.gdx.math.Rectangle;
public class ChemicalSymbol {
    private Vector2 position;
    private Texture texture;
    private Rectangle bounds;
    private String symbol;
    private float speed;

    public ChemicalSymbol(String symbol, float x, float y) {
        this.symbol = symbol;
        position = new Vector2(x, y);
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);  // 50x50 white square
        pixmap.setColor(4, 120, 200, 1);  // Set the color to white
        pixmap.fillRectangle(0, 0, 50, 50);
        texture = new Texture(pixmap);
        pixmap.dispose();
        bounds = new Rectangle((int)x, (int)y, texture.getWidth(), texture.getHeight());
        speed = 65;
    }
    public void init(float x, float y) {
        position = new Vector2(x, y);
        this.bounds.setPosition(position);
        // Reset any other necessary fields
    }
    public void update(float delta) {
        position.y -= speed * delta;
        bounds.setPosition(position);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);  // 50x50 white square
        this.texture.dispose();  // Dispose old texture
        if(symbol.equals("H")){  pixmap.setColor(40, 12, 200, 1);}
        else if(symbol.equals("Cl")){   pixmap.setColor(12, 40, 200, 1);}
        else if(symbol.equals("O")){    pixmap.setColor(12, 12, 10, 1);}
        else if(symbol.equals("Na")) {  pixmap.setColor(40, 12, 100, 1);}
        else {   pixmap.setColor(0, 0, 0, 1);}

        pixmap.fillRectangle(0, 0, 50, 50);
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public String getSymbol() {
        return symbol;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return position.y;
    }

    public float getHeight() {
        return texture.getHeight();
    }

    public void dispose() {
        texture.dispose();
    }
}
