package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;

public class Bomb {
    final GameScreen game;
    private Animation<TextureRegion> explosionAnimation;
    private float stateTime;
    private boolean isExplosionPlaying;
    private float explosionX, explosionY;

    public Bomb(final GameScreen game) {
        this.game = game;
        loadExplosionAnimation();
    }

    public void create() {
        bomb = new Rectangle();
        bomb.x = MathUtils.random(0, game.GAME_SCREEN_X - 64);
        bomb.y = game.GAME_SCREEN_Y;
        bomb.width = BOMB_SIZE;
        bomb.height = BOMB_SIZE;
        bombLastDropTime = TimeUtils.nanoTime();

        boom = Gdx.audio.newMusic(Gdx.files.internal("sfx/explosion.wav"));

        bombs = new Array<Rectangle>();
        spawnBomb();
    }

    private void loadExplosionAnimation() {
        Texture boomSheet = new Texture(Gdx.files.internal("boom.png"));
        TextureRegion[][] tmp = TextureRegion.split(boomSheet, boomSheet.getWidth() / FRAME_COLS, boomSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] explosionFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                explosionFrames[index++] = tmp[i][j];
            }
        }
        explosionAnimation = new Animation<TextureRegion>(0.05f, explosionFrames);
        stateTime = 0f;
        isExplosionPlaying = false;
    }

    public void spawnBomb() {
        Rectangle bombBox = new Rectangle();
        bombBox.x = MathUtils.random(0, game.GAME_SCREEN_X - 64);
        bombBox.y = game.GAME_SCREEN_Y;
        bombBox.width = BOMB_SIZE;
        bombBox.height = BOMB_SIZE;
        bombs.add(bombBox);
        bombLastDropTime = TimeUtils.nanoTime();
    }

    public void render() {
        if (TimeUtils.nanoTime() - bombLastDropTime > spawnBombInterval) {
            spawnBomb();
        }

        if (isExplosionPlaying) {
            stateTime += Gdx.graphics.getDeltaTime();
            if (explosionAnimation.isAnimationFinished(stateTime)) {
                isExplosionPlaying = false;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Rectangle bomb : bombs) {
            game.batch.draw(game.bombImage, bomb.x, bomb.y);
        }

        if (isExplosionPlaying) {
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(stateTime, false);
            batch.draw(currentFrame, explosionX, explosionY);
        }
    }

    public void move() {
        for (Iterator<Rectangle> iter = bombs.iterator(); iter.hasNext(); ) {
            Rectangle bomb = iter.next();
            bomb.y -= BOMB_SPEED * Gdx.graphics.getDeltaTime();
            if (bomb.y + BOMB_SIZE < 0) iter.remove();
            if (bomb.overlaps(game.player)) {
                game.dropSound.play();
                game.stunPlayer();
                explosionX = bomb.x;
                explosionY = bomb.y;
                stateTime = 0f;
                isExplosionPlaying = true;
                boom.play();
                iter.remove();
            }
        }
    }

    public void dispose() {
        boom.dispose();
        explosionAnimation = null;
    }

    private Rectangle bomb;
    private long bombLastDropTime;
    private Array<Rectangle> bombs;
    public final int BOMB_SIZE = 64;
    private static final int BOMB_SPEED = 250;
    private long spawnBombInterval = 5000000000L;

    private Music boom;

    private static final int FRAME_COLS = 4;
    private static final int FRAME_ROWS = 4;
}
