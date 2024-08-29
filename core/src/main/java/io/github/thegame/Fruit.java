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

public class Fruit {
    final GameScreen game;
    private Map<FruitType, Texture> fruitTextures;

    public Fruit(final GameScreen game) {
        this.game = game;
        this.fruitTextures = new HashMap<>();
        for (FruitType type : FruitType.values()) {
            fruitTextures.put(type, new Texture(Gdx.files.internal(type.spritePath)));
        }
    }

    public void create() {
        fruitLastDropTime = TimeUtils.nanoTime();
        font = new BitmapFont();
        font.getData().setScale(2f);
        fruits = new Array<>();

        collect1 = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect1.wav"));
        collect2 = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect2.wav"));
        collect3 = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect3.wav"));
        point = Gdx.audio.newMusic(Gdx.files.internal("sfx/point.wav"));
        fruitclear = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitclear.wav"));
        fruitwrong = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitwrong.wav"));

        spawnFruit();
    }

    public void spawnFruit() {
        FruitRectangle fruitBox = new FruitRectangle();
        fruitBox.x = MathUtils.random(0, game.GAME_SCREEN_X - 64);
        fruitBox.y = game.GAME_SCREEN_Y;
        fruitBox.width = 64;
        fruitBox.height = 64;
        generateFruitValue(fruitBox);
        fruits.add(fruitBox);
        fruitLastDropTime = TimeUtils.nanoTime();
    }

    public void render() {
        if (TimeUtils.nanoTime() - fruitLastDropTime > spawnFruitInterval) {
            spawnFruit();
        }
    }

    public void draw() {
        for (Rectangle fruit : fruits) {
            FruitRectangle fruitRect = (FruitRectangle) fruit;
            game.batch.draw(fruitTextures.get(fruitRect.fruitType), fruitRect.x-15, fruitRect.y+15);
        }
    }

    public void move() {
        for (Iterator<Rectangle> iter = fruits.iterator(); iter.hasNext(); ) {
            Rectangle fruit = iter.next();
            fruit.y -= FRUIT_SPEED * Gdx.graphics.getDeltaTime();
            if (fruit.y + FRUIT_SIZE < 0) iter.remove();
            if (fruit.overlaps(game.player)) {
                FruitRectangle collidedFruit = (FruitRectangle) fruit;
                int fruitValue = collidedFruit.fruitValue;
                System.out.println("Collided with fruit value: " + fruitValue);
                collectLogic(fruitValue);
                iter.remove();
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            collected[0] = 0;
            collected[1] = 0;
            collected[2] = 0;
            remainingFruitSum = 0;
            collectIndex = 0;
            fruitclear.play();
        }
    }

    public void dispose() {
        for (Texture texture : fruitTextures.values()) {
            texture.dispose();
        }
        collect1.dispose();
        collect2.dispose();
        collect3.dispose();
        point.dispose();
        fruitclear.dispose();
        fruitwrong.dispose();
    }

    public void generateFruitValue(FruitRectangle fruit) {
        FruitType[] types = FruitType.values();
        FruitType type = types[MathUtils.random(types.length - 1)];
        fruit.fruitType = type;
        fruit.fruitValue = type.value;
    }

    public void collectLogic(int value) {
        collected[collectIndex] = value;
        collectIndex = (collectIndex + 1) % 3;
        switch (collectIndex) {
            case 1:
                collect1.play();
                break;
            case 2:
                collect2.play();
                break;
            case 0: // Reset to collect1 after the third collect
                collect3.play();
                break;
        }

        if (collectIndex == 0) { // This means three fruits have been collected
            int sum = collected[0] + collected[1] + collected[2];
            if (sum == 180) {
                game.points++;
                point.play();
            } else {
                game.points--;
                fruitwrong.play();
            }
            // Reset the collected array and index
            collected[0] = 0;
            collected[1] = 0;
            collected[2] = 0;
            collectIndex = 0;
        }

        lastFruitSum = collected[0] + collected[1] + collected[2];
        remainingFruitSum = collected[1] + collected[2];
    }

    public void drawCollectedFruits() {
        float startX = 10;
        float startY = game.GAME_SCREEN_Y-100;
        for (int i = 0; i < 3; i++) {
            FruitType type = FruitType.getByValue(collected[i]);
            if (type != null) {
                game.batch.draw(fruitTextures.get(type), startX + i * 30, startY, 32, 32);
            }
        }
    }

    public int[] collected = new int[3];
    private int collectIndex = 0;
    public int lastFruitSum;
    public int remainingFruitSum;
    private BitmapFont font;
    private long fruitLastDropTime;
    private Array<Rectangle> fruits;
    private static final int FRUIT_SIZE = 64;
    private static final int FRUIT_SPEED = 250;
    private long spawnFruitInterval = 500000000L;

    private Music collect1;
    private Music collect2;
    private Music collect3;
    private Music point;
    private Music fruitclear;
    private Music fruitwrong;

    private static class FruitRectangle extends Rectangle {
        int fruitValue;
        FruitType fruitType;
    }
}
