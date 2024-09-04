package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Element {
    final GameScreen game;
    private Map<ElementType, Texture> elementTextures;

    public Element(final GameScreen game) {
        this.game = game;
        this.elementTextures = new HashMap<>();
        for (ElementType type : ElementType.values()) {
            elementTextures.put(type, new Texture(Gdx.files.internal(type.spritePath)));
        }
    }

    public void create() {
        elementLastDropTime = TimeUtils.nanoTime();

        // Load the pixel font
        font = new BitmapFont(Gdx.files.internal("pixel.fnt"));
        font.getData().setScale(1.5f);

        elements = new Array<>();

        collect1 = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect1.wav"));
        collect2 = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect2.wav"));
        collect3 = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect3.wav"));
        point = Gdx.audio.newMusic(Gdx.files.internal("sfx/point.wav"));
        clear = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitclear.wav"));
        wrong = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitwrong.wav"));

        spawnFruit();
    }


    public void spawnFruit() {
        ElementRectangle elementBox = new ElementRectangle();
        elementBox.x = MathUtils.random(0, game.GAME_SCREEN_X - ELEMENT_SIZE);
        elementBox.y = game.GAME_SCREEN_Y;
        elementBox.width = ELEMENT_SIZE;
        elementBox.height = ELEMENT_SIZE;
        generateFruitValue(elementBox);
        elements.add(elementBox);
        elementLastDropTime = TimeUtils.nanoTime();
    }

    public void render() {
        if (TimeUtils.nanoTime() - elementLastDropTime > spawnElementInterval) {
            spawnFruit();
        }
    }

    public void draw() {
        for (Rectangle fruit : elements) {
            ElementRectangle elementRect = (ElementRectangle) fruit;
            Texture texture = elementTextures.get(elementRect.elementType);

            // Draw the texture at the correct position
            game.batch.draw(texture, elementRect.x, elementRect.y, ELEMENT_SIZE, ELEMENT_SIZE);
        }
    }

    public void move() {
        for (Iterator<Rectangle> iter = elements.iterator(); iter.hasNext(); ) {
            Rectangle element = iter.next();
            element.y -= ELEMENT_SPEED * Gdx.graphics.getDeltaTime();
            if (element.y + ELEMENT_SIZE < 0) iter.remove();

            if (element.overlaps(game.player)) {
                ElementRectangle collidedElement = (ElementRectangle) element;
                System.out.println("Collected element: " + collidedElement.elementType);
                collectLogic(collidedElement.elementType);
                iter.remove();
            }
        }

    }

    public void clear() {
        collected[0] = null;
        collected[1] = null;
        collectIndex = 0;
        clear.play();
    }

    public void dispose() {
        for (Texture texture : elementTextures.values()) {
            texture.dispose();
        }
        collect1.dispose();
        collect2.dispose();
        collect3.dispose();
        point.dispose();
        clear.dispose();
        wrong.dispose();
    }

    public void generateFruitValue(ElementRectangle element) {
        ElementType[] types = ElementType.values();
        ElementType type = types[MathUtils.random(types.length - 1)];
        element.elementType = type;
        element.elementValue = type.value;
    }

    private String lastCompoundName = null;  // Store the last valid compound name

    public void collectLogic(ElementType collectedElement) {
        // Clear the compound name when a new element is collected
        lastCompoundName = null;

        if (collectIndex == 0 && collected[1] != null) {
            // Reset the collected array after checking
            collected[1] = null;
        }

        collected[collectIndex] = collectedElement;
        collectIndex = (collectIndex + 1) % 2; // Track only the last two elements

        if (collectIndex == 0) { // After two elements are collected
            if (ElementType.isCompound(collected[0], collected[1])) {
                if(game.boosted) {
                    game.points+=2;
                } else if (!game.boosted) {
                    game.points++;
                } else {
                    game.points++;
                }

                point.play();
                System.out.println("Compound formed: " + collected[0] + " + " + collected[1]);

                // Store the compound name
                lastCompoundName = ElementType.compounds.get(collected[0].name() + "_" + collected[1].name());
            } else {
                wrong.play();
                System.out.println("Invalid compound: " + collected[0] + " + " + collected[1]);
            }
        } else {
            // Play the collect sound
            collect1.play();
        }
    }


    public void drawCollectedFruits() {
        float startX = 150;
        float startY = game.GAME_SCREEN_Y - 580;

        for (int i = 0; i < 2; i++) {
            ElementType type = collected[i];
            if (type != null) {
                if (collectIndex == 0 && ElementType.isCompound(collected[0], collected[1])) {
                    // Restore original color if the combination is correct
                    game.batch.setColor(1f, 1f, 1f, 1f);  // Set color to normal
                } else {
                    // Apply monochrome effect by averaging RGB values
                    game.batch.setColor(0.33f, 0.33f, 0.33f, 0.8f);  // Monochrome (grey)
                }
                game.batch.draw(elementTextures.get(type), startX + i * 100, startY, ELEMENT_SIZE * 1.3f, ELEMENT_SIZE);
            }
        }

        // Reset the batch color to default after drawing
        game.batch.setColor(1f, 1f, 1f, 1f);

        // Draw the compound name below the collected elements if a valid compound was formed
        drawCompoundName();
    }


    public void drawCompoundName() {
        if (lastCompoundName != null) {
            // Set the position where the compound name will be drawn
            float nameX = 100;  // Align with collected elements
            float nameY = game.GAME_SCREEN_Y - 600;  // Position below collected elements

            // Use the pixel font to draw the compound name
            font.draw(game.batch, lastCompoundName, nameX, nameY);
        }
    }



    private void resetCollected() {
        collected[0] = null;
        collected[1] = null;
        collectIndex = 0;
    }

    private ElementType[] collected = new ElementType[2];
    private int collectIndex = 0;
    private BitmapFont font;
    private long elementLastDropTime;
    private Array<Rectangle> elements;
    private static final int ELEMENT_SIZE = 64; // Size for displaying elements
    public static int ELEMENT_SPEED = 100;
    private long spawnElementInterval = 1500000000L;

    private Music collect1;
    private Music collect2;
    private Music collect3;
    private Music point;
    private Music clear;
    private Music wrong;

    private static class ElementRectangle extends Rectangle {
        int elementValue;
        ElementType elementType;
    }
}
