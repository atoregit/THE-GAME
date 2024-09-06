package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class TimerExtension {
    final GameScreen game;

    private boolean extended;
    private long extensionStartTime;
    private final long extensionDuration = 10000L; // 10 seconds
    private final long notifyDuration = 3000L; // 3 seconds
    private BitmapFont font;

    public TimerExtension(final GameScreen game) {
        this.game = game;
        font = new BitmapFont(Gdx.files.internal("pixel.fnt"));
        font.getData().setScale(2.5f);
    }

    public void create() {
        timerExtension = new Rectangle();
        timerExtension.x = MathUtils.random(0, game.GAME_SCREEN_X - 64);
        timerExtension.y = game.GAME_SCREEN_Y;
        timerExtension.width = EXTENSION_SIZE;
        timerExtension.height = EXTENSION_SIZE;
        extensionLastDropTime = TimeUtils.nanoTime();

        extensionSound = Gdx.audio.newMusic(Gdx.files.internal("hit.wav"));

        extensions = new Array<>();
        spawnExtension();
    }

    public void spawnExtension() {
        if (MathUtils.randomBoolean(0.3f)) {
            Rectangle extensionBox = new Rectangle();
            extensionBox.x = MathUtils.random(0, game.GAME_SCREEN_X - EXTENSION_SIZE);
            extensionBox.y = game.GAME_SCREEN_Y;
            extensionBox.width = EXTENSION_SIZE;
            extensionBox.height = EXTENSION_SIZE;
            extensions.add(extensionBox);
            extensionLastDropTime = TimeUtils.nanoTime();
        }
    }

    public void render() {
        if (TimeUtils.nanoTime() - extensionLastDropTime > spawnExtensionInterval) {
            spawnExtension();
        }
    }

    public void draw(SpriteBatch batch) {
        batch.begin(); // Start sprite batch
        for (Rectangle extension : extensions) {
            batch.draw(game.timerExtensionImage, extension.x, extension.y);
        }

        // Draw extension notification if active
        if (extended) {
            long elapsedTime = TimeUtils.nanoTime() - extensionStartTime;
            if (elapsedTime < notifyDuration * 1000000L) {
                font.setColor(new Color(0.3f, 0.8f, 0.3f, 1)); // Softer green
                font.draw(batch, "Time Extended!", game.GAME_SCREEN_X / 2 - 100, game.GAME_SCREEN_Y / 2);
            }
        }
        batch.end(); // End sprite batch
    }

    public void move() {
        for (Iterator<Rectangle> iter = extensions.iterator(); iter.hasNext(); ) {
            Rectangle extension = iter.next();
            extension.y -= EXTENSION_SPEED * Gdx.graphics.getDeltaTime();
            if (extension.y + EXTENSION_SIZE < 0) iter.remove();
            if (extension.overlaps(game.player)) {
                game.dropSound.play();
                startTimer(); // Always update the start time regardless of the current state
                game.addTime(10); // Add 10 seconds to the game timer
                extensionSound.play();
                iter.remove();
            }
        }
    }

    public void startTimer() {
        // Update the start time to extend the time regardless of the `extended` state
        extensionStartTime = TimeUtils.nanoTime();
        extended = true; // Set the `extended` flag to true, but keep extending
    }

    public void update(float delta) {
        if (extended) {
            long elapsedTime = TimeUtils.nanoTime() - extensionStartTime;
            if (elapsedTime > extensionDuration * 1000000L) {
                extended = false;
            }
        }
    }

    public void dispose() {
        extensionSound.dispose();
        font.dispose();
    }

    private Rectangle timerExtension;
    private long extensionLastDropTime;
    private Array<Rectangle> extensions;
    public final int EXTENSION_SIZE = 64;
    private static final int EXTENSION_SPEED = 250;
    private long spawnExtensionInterval = 20000000000L;
    private Music extensionSound;
}
