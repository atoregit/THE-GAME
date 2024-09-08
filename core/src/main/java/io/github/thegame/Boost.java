package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

import java.util.Iterator;

public class Boost {
    final GameScreen game;

    private boolean boostEnded;
    private long boostStartTime;
    private long boostEndTime;
    private final long boostDuration = 10000L; // 10 seconds
    private final long notifyDuration = 3000L; // 3 seconds
    private final long endMessageDuration = 3000L; // 3 seconds
    private BitmapFont font;
    private BitmapFont endFont;
    private Music boom;
    private Music boom2;
    private Texture boostedSprite;
    private Texture boostEndedSprite;

    public Boost(final GameScreen game) {
        this.game = game;
        font = new BitmapFont(Gdx.files.internal("pixel.fnt"));
        font.getData().setScale(2.5f);
        endFont = new BitmapFont(Gdx.files.internal("pixel.fnt"));
        endFont.getData().setScale(2.5f);
        boostedSprite = new Texture("boosted.png");
        boostEndedSprite = new Texture("boostEnded.png");
    }

    public void create() {
        boost = new Rectangle();
        boost.x = MathUtils.random(0, game.GAME_SCREEN_X - 64);
        boost.y = game.GAME_SCREEN_Y;
        boost.width = BOMB_SIZE;
        boost.height = BOMB_SIZE;
        boostLastDropTime = TimeUtils.nanoTime();

        boom = Gdx.audio.newMusic(Gdx.files.internal("sfx/boost.wav"));
        boom2 = Gdx.audio.newMusic(Gdx.files.internal("sfx/boostEnd.wav"));
        boosts = new Array<>();
    }

    public void spawnBoost() {
        // Only spawn a new boost if no boost is currently active
        if (!game.boosted && MathUtils.randomBoolean()) {
            Rectangle bombBox = new Rectangle();
            bombBox.x = MathUtils.random(0, game.GAME_SCREEN_X - BOMB_SIZE);
            bombBox.y = game.GAME_SCREEN_Y;
            bombBox.width = BOMB_SIZE;
            bombBox.height = BOMB_SIZE;
            boosts.add(bombBox);
            boostLastDropTime = TimeUtils.nanoTime();
        }
    }

    public void render() {
        if (TimeUtils.nanoTime() - boostLastDropTime > spawnBoostInterval) {
            spawnBoost();
        }
    }

    public void draw(SpriteBatch batch) {
        batch.begin(); // Start sprite batch

        for (Rectangle bomb : boosts) {
            batch.draw(game.boostImage, bomb.x, bomb.y);
        }

        // Draw boost notification if active
        if (game.boosted) {
            long elapsedTime = TimeUtils.nanoTime() - boostStartTime;
            if (elapsedTime < 1000000000L) { // 1 second hold
                batch.setColor(1.2f, 1.2f, 1.2f, 1); // Increase brightness and contrast
                batch.draw(boostedSprite, game.GAME_SCREEN_X / 2 - 350 / 2, game.GAME_SCREEN_Y / 2 - 35, 350, 70);
                batch.setColor(1, 1, 1, 1); // Reset color
            } else if (elapsedTime < 2000000000L) { // 1 second flicker
                if ((elapsedTime % 200000000) < 100000000) {
                    batch.setColor(1, 1, 1, 0.5f); // Flicker effect (semi-transparent)
                } else {
                    batch.setColor(1, 1, 1, 1); // Full opacity
                }
                batch.draw(boostedSprite, game.GAME_SCREEN_X / 2 - 350 / 2, game.GAME_SCREEN_Y / 2 - 35, 350, 70);
            }
        }

        // Draw end of boost message if the boost has ended
        if (boostEnded) {
            boom2.play();
            long elapsedTime = TimeUtils.nanoTime() - boostEndTime;
            if (elapsedTime < 1000000000L) { // 1 second hold
                batch.setColor(1, 1, 1, 1); // Full opacity
                batch.draw(boostEndedSprite, game.GAME_SCREEN_X / 2 - 350 / 2, game.GAME_SCREEN_Y / 2 - 35, 350, 70);
            } else if (elapsedTime < 2000000000L) { // 1 second flicker
                if ((elapsedTime % 200000000) < 100000000) {
                    batch.setColor(1, 1, 1, 0.5f); // Flicker effect (semi-transparent)
                } else {
                    batch.setColor(1, 1, 1, 1); // Full opacity
                }
                batch.draw(boostEndedSprite, game.GAME_SCREEN_X / 2 - 350 / 2, game.GAME_SCREEN_Y / 2 - 35, 350, 70);
            } else {
                boostEnded = false; // Reset flag after flicker duration
            }
        }

        batch.end(); // End sprite batch
    }



    public void move() {
        for (Iterator<Rectangle> iter = boosts.iterator(); iter.hasNext(); ) {
            Rectangle bomb = iter.next();
            bomb.y -= BOOST_SPEED * Gdx.graphics.getDeltaTime();
            if (bomb.y + BOMB_SIZE < 0) iter.remove();
            if (bomb.overlaps(game.player)) {
                game.dropSound.play();
                if (!game.boosted) { // Only apply boost if not already boosted
                    startTimer();

                    game.boostPlayer();
                    boom.play();
                }
                iter.remove();
            }
        }
    }

    public void startTimer() {
        game.boosted = true;
        boostEnded = false; // Reset end message flag when boost starts
        boostStartTime = TimeUtils.nanoTime();
    }

    public void endBoost() {
        game.boosted = false;
        boostEnded = true;
        boostEndTime = TimeUtils.nanoTime();
    }

    public void update(float delta) {
        if (game.boosted) {
            long elapsedTime = TimeUtils.nanoTime() - boostStartTime;
            if (elapsedTime > boostDuration * 1000000L) {
                endBoost(); // Call endBoost method to handle boost end
            }
        }
    }

    public void dispose() {
        boom.dispose();
        font.dispose();
        endFont.dispose();
    }

    private Rectangle boost;
    private long boostLastDropTime;
    private Array<Rectangle> boosts;
    public final int BOMB_SIZE = 64;
    private static final int BOOST_SPEED = 250;
    private long spawnBoostInterval = 15000000000L;
}
