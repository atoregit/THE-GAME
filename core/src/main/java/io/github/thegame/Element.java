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
        font = new BitmapFont();
        font.getData().setScale(2f);
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
        elementBox.x = MathUtils.random(0, game.GAME_SCREEN_X - 64);
        elementBox.y = game.GAME_SCREEN_Y;
        elementBox.width = 64;
        elementBox.height = 64;
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
            game.batch.draw(elementTextures.get(elementRect.elementType), elementRect.x - 15, elementRect.y + 15);
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

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            collected[0] = null;
            collected[1] = null;
            collectIndex = 0;
            clear.play();
        }
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

    public void collectLogic(ElementType collectedElement) {
        collected[collectIndex] = collectedElement;
        collectIndex = (collectIndex + 1) % 2; // Track only the last two elements

        if (collectIndex == 0) { // After two elements are collected
            if (ElementType.isCompound(collected[0], collected[1])) {
                game.points++;
                point.play();
                System.out.println("Compound formed: " + collected[0] + " + " + collected[1]);
            } else {
                game.points--;
                wrong.play();
                System.out.println("Invalid compound: " + collected[0] + " + " + collected[1]);
            }

            // Reset the collected array after checking
            collected[0] = null;
            collected[1] = null;
        } else {
            // Play the collect sound
            switch (collectIndex) {
                case 1:
                    collect1.play();
                    break;
                case 0:
                    collect2.play();
                    break;
            }
        }
    }

    public void drawCollectedFruits() {
        float startX = 10;
        float startY = game.GAME_SCREEN_Y - 100;
        for (int i = 0; i < 2; i++) {
            ElementType type = collected[i];
            if (type != null) {
                game.batch.draw(elementTextures.get(type), startX + i * 30, startY, 32, 32);
            }
        }
    }

    private ElementType[] collected = new ElementType[2];
    private int collectIndex = 0;
    private BitmapFont font;
    private long elementLastDropTime;
    private Array<Rectangle> elements;
    private static final int ELEMENT_SIZE = 64;
    private static final int ELEMENT_SPEED = 250;
    private long spawnElementInterval = 500000000L;

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
