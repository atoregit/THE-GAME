package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        splashRectangleTexture = new Texture(Gdx.files.internal("bg.png"));
        showSplash = false;
        splashTime = 0;

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

    public void drawSplash(SpriteBatch batch) {
        if (showSplash) {
            float splashX = game.GAME_SCREEN_X / 2 - splashTextureWidth/ 2;
            float splashY = game.GAME_SCREEN_Y / 2 - splashTextureHeight/ 2;

            // Define animation parameters
            float animationDuration = 4f; // Total duration for splash (increased by 1 second)
            float hoverDuration = 1f; // Duration before exit
            float exitDuration = 1f; // Duration of exit animation
            float fadeDuration = 0.5f; // Duration for fade in
            float fadeOutDuration = exitDuration; // Duration for fade out, same as exit animation
            float elapsedTime = splashTime;

            // Fade-in effect for the background rectangle
            float fadeInAlpha = MathUtils.clamp(elapsedTime / fadeDuration, 0f, 0.5f); // Fade in to 50% opacity

            // Fade-out effect for the background rectangle at the same time as exit animation
            float fadeOutAlpha = 1f;
            if (elapsedTime > hoverDuration) {
                float exitElapsed = elapsedTime - hoverDuration;
                fadeOutAlpha = MathUtils.clamp(1 - (exitElapsed / exitDuration), 0f, 1f);
            }

            // Draw the background rectangle with fade-in and fade-out effects
            batch.setColor(0f, 0f, 0f, fadeInAlpha * fadeOutAlpha);
            batch.draw(splashRectangleTexture, 0, 0, game.GAME_SCREEN_X, game.GAME_SCREEN_Y);

            // Reset color for splash texture drawing
            batch.setColor(1f, 1f, 1f, 1f);

            // Hover animation (First part of splash)
            if (elapsedTime <= hoverDuration) {
                float hoverOffset = (float) Math.sin((elapsedTime / hoverDuration) * Math.PI) * 20; // Simple hover effect
                batch.draw(splashTexture, splashX, splashY + hoverOffset, splashTextureWidth, splashTextureHeight);
            } else if (elapsedTime <= animationDuration) {
                // Exit animation (Last part of splash)
                float exitElapsed = elapsedTime - hoverDuration;
                float exitOffset = (exitElapsed / exitDuration) * (game.GAME_SCREEN_Y / 2); // Move upwards
                float exitAlpha = MathUtils.clamp(1 - (exitElapsed / exitDuration), 0f, 1f); // Fade out during exit
                batch.setColor(1f, 1f, 1f, exitAlpha);
                batch.draw(splashTexture, splashX, splashY - exitOffset, splashTextureWidth, splashTextureHeight);
            } else {
                // End of splash animation
                showSplash = false; // Stop showing splash
            }

            // Increment splash time
            splashTime += Gdx.graphics.getDeltaTime();

            // Reset color for further drawing
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }




    public void update(float delta) {
        if (showSplash) {
            splashTime += delta;
            if (splashTime > 2) { // Splash duration of 2 seconds
                showSplash = false;
                splashTime = 0;
            }
        }
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
            collected[1] = null;
        }

        collected[collectIndex] = collectedElement;
        collectIndex = (collectIndex + 1) % 2; // Track only the last two elements

        if (collectIndex == 0) { // After two elements are collected
            String compoundKey = collected[0].name() + "_" + collected[1].name();
            if (ElementType.isCompound(collected[0], collected[1])) {
                int points = isEligibleForSplash(compoundKey) ? 2 : 1;
                game.points += game.boosted ? points * 2 : points;

                point.play();
                System.out.println("Compound formed: " + collected[0] + " + " + collected[1]);

                // Store the compound name and show splash screen for eligible compounds
                lastCompoundName = ElementType.compounds.get(compoundKey);
                if (lastCompoundName != null && isEligibleForSplash(compoundKey)) {
                    showSplash = true;
                    currentCompound = lastCompoundName;
                }
            } else {
                wrong.play();
                System.out.println("Invalid compound: " + collected[0] + " + " + collected[1]);
            }
        } else {
            collect1.play();
        }
    }

    private boolean isEligibleForSplash(String compoundKey) {
        String spriteName = "";
        String[] elements = compoundKey.split("_");
        if (elements.length == 2) {
            if ((elements[0].equals("OXYGEN") && elements[1].equals("HYDROGEN")) || (elements[0].equals("HYDROGEN") && elements[1].equals("OXYGEN"))) { // Water (H₂O)
                spriteName = "water";
            } else if (elements[0].equals("HYDROGEN") && elements[1].equals("HYDROGEN")) { // Hydrogen (H₂)
                spriteName = "fuelCell";
            } else if ((elements[0].equals("HYDROGEN") && elements[1].equals("CHLORINE")) || (elements[0].equals("CHLORINE") && elements[1].equals("HYDROGEN"))) { // Hydrochloric Acid (HCl)
                spriteName = "cleaningAgent";
            } else if ((elements[0].equals("SODIUM") && elements[1].equals("CHLORINE")) || (elements[0].equals("CHLORINE") && elements[1].equals("SODIUM"))) { // Sodium Chloride (NaCl)
                spriteName = "salt";
            } else if ((elements[0].equals("CALCIUM") && elements[1].equals("OXYGEN")) || (elements[0].equals("OXYGEN") && elements[1].equals("CALCIUM"))) { // Calcium Oxide (CaO)
                spriteName = "cement";
            } else if ((elements[0].equals("LITHIUM") && elements[1].equals("OXYGEN")) || (elements[0].equals("OXYGEN") && elements[1].equals("LITHIUM"))) { // Lithium Oxide (Li₂O)
                spriteName = "battery";
            } else if ((elements[0].equals("ALUMINUM") && elements[1].equals("OXYGEN")) || (elements[0].equals("OXYGEN") && elements[1].equals("ALUMINUM"))) { // Aluminum Oxide (Al₂O₃)
                spriteName = "cement";
            } else if ((elements[0].equals("SODIUM") && elements[1].equals("BROMINE")) || (elements[0].equals("BROMINE") && elements[1].equals("SODIUM"))) { // Sodium Bromide (NaBr)
                spriteName = "sedative";
            } else if (elements[0].equals("NITROGEN") && elements[1].equals("NITROGEN")) { // Nitrogen Gas (N₂)
                spriteName = "balloons";
            } else if (elements[0].equals("BROMINE") && elements[1].equals("BROMINE")) { // Bromine Gas (Br₂)
                spriteName = "fireExtinguisher";
            } else if ((elements[0].equals("NITROGEN") && elements[1].equals("HYDROGEN")) || (elements[0].equals("HYDROGEN") && elements[1].equals("NITROGEN"))) { // Ammonia (NH₃)
                spriteName = "fertilizer";
            } else {
                return false;
            }
        } else {
            return false;
        }

        splashTexture = new Texture(Gdx.files.internal("splash/" + spriteName + ".png"));
        return true;
    }


// Help


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
    private long spawnElementInterval = 1300000000L;

    private Music collect1;
    private Music collect2;
    private Music collect3;
    private Music point;
    private Music clear;
    private Music wrong;

    private float splashTextureWidth = 400;
    private float splashTextureHeight = 200;


    private Texture splashTexture;
    private Texture splashRectangleTexture;
    private boolean showSplash;
    private float splashTime;
    private String currentCompound;

    private static class ElementRectangle extends Rectangle {
        int elementValue;
        ElementType elementType;
    }
}
