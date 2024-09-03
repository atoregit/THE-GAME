package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Main game;

    private Player chara;
    private Element element;
    private Bomb bomb;
    private GameEndScreen endScreen;

    private Texture exitButtonTexture;
    private Rectangle exitButtonBounds;

    private InputProcessor gameInputProcessor;
    public GameScreen(final Main game) {
        this.game = game;
        chara = new Player(this);
        element = new Element(this);
        bomb = new Bomb(this);
        initComponents();
        setupInputProcessor();
    }
    private void setupInputProcessor() {
        gameInputProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    game.setScreen(new PauseMenuScreen(game, GameScreen.this));
                    clickSound.play();
                    game.pause();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert touch coordinates to our game world coordinates
                Vector3 touchPos = new Vector3(screenX, screenY, 0);
                camera.unproject(touchPos);

                if (exitButtonBounds.contains(touchPos.x, touchPos.y)) {
                    game.setScreen(new PauseMenuScreen(game, GameScreen.this));
                    clickSound.play();
                    game.pause();
                    return true;
                }
                return false;
            }
        };
    }
    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseMenuScreen(game, this));
            clickSound.play();
            game.pause();
            return;
        }

        ScreenUtils.clear(1, 1, 1, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(texture, 0, margin, GAME_SCREEN_X, GAME_SCREEN_Y);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !stunned) {
            batch.draw(playerImageLeft, player.x, player.y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !stunned) {
            batch.draw(playerImageRight, player.x, player.y);
        } else if (stunned) {
            batch.draw(playerImageStun, player.x, player.y);
        } else {
            batch.draw(playerImageIdle, player.x, player.y);
        }

        element.draw();
        bomb.draw(batch);

        // Draw the exit button
        batch.draw(exitButtonTexture, exitButtonBounds.x, exitButtonBounds.y, exitButtonBounds.width, exitButtonBounds.height);

        batch.end();
        String pointsText = "" + points;
        float remainingTime = timerDuration - timer;
        updateTimer(Gdx.graphics.getDeltaTime());

        GlyphLayout pointsLayout = new GlyphLayout(font, pointsText);
        float pointsWidth = pointsLayout.width;
        float scoresTextureWidth = scoresTexture.getWidth();
        float pointsX = (scoresTextureWidth - pointsWidth) / 2 + GAME_SCREEN_X * 0.46f;

        chara.render();
        chara.processSpeed();
        chara.move();
        element.render();
        element.move();
        bomb.render();
        bomb.move();

        batch.begin();
        batch.draw(timerTexture, 10, GAME_SCREEN_Y - timerTexture.getHeight() - 10);
        batch.draw(scoresTexture, GAME_SCREEN_X     * 0.46f, GAME_SCREEN_Y - timerTexture.getHeight() - 18);
        element.drawCollectedFruits();
        batch.end();

        batch.begin();
        font.draw(batch, pointsText, pointsX, GAME_SCREEN_Y * 0.95f);
        font.draw(batch, "" + (int) remainingTime, GAME_SCREEN_X * 0.13f, GAME_SCREEN_Y * 0.96f);
        batch.end();


        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Convert y
            if (exitButtonBounds.contains(touchX, touchY)) {
                game.setScreen(new PauseMenuScreen(game, this));
                clickSound.play();
                game.pause();
            }
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {
        gameMusic.play();
        Gdx.input.setInputProcessor(gameInputProcessor);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        dropImage.dispose();
        playerImageIdle.dispose();
        playerImageLeft.dispose();
        playerImageRight.dispose();
        playerImageStun.dispose();
        bombImage.dispose();
        dropSound.dispose();
        gameMusic.dispose();
        font.dispose();
        element.dispose();
        bomb.dispose();
        timerTexture.dispose();
        fruitsCollectedTexture.dispose();
        exitButtonTexture.dispose();
        clockSound.dispose();

        if (batch != null) {
            batch.dispose();
            batch = null;
        }
    }

    public void updateTimer(float deltaTime) {
        if (stunned) {
            stunTimer += deltaTime;
            if (stunTimer >= chara.STUN_DURATION) {
                stunned = false;
                stunTimer = 0;
            }
        }

        if (slowed) {
            slowTimer += deltaTime;
            if (stunTimer >= chara.SLOW_DURATION) {
                slowed = false;
                slowTimer = 0;
            }
        }

        timer += deltaTime;

        if(timer >= 50) {
            if(!clockPlayed) {
                clockSound.play();
                clockPlayed = true;
            }

        }

        if (timer >= timerDuration) {
            gameMusic.stop();
            game.setScreen(new GameEndScreen(game, points));
        }
    }

    public void initComponents() {
        dropImage = new Texture(Gdx.files.internal("melon.png"));
        bombImage = new Texture(Gdx.files.internal("bomb.png"));
        playerImageIdle = new Texture(Gdx.files.internal("idle.png"));
        playerImageRight = new Texture(Gdx.files.internal("runright.png"));
        playerImageLeft = new Texture(Gdx.files.internal("runleft.png"));
        playerImageStun = new Texture(Gdx.files.internal("stun.png"));
        timerTexture = new Texture(Gdx.files.internal("timer.png"));
        fruitsCollectedTexture = new Texture(Gdx.files.internal("fruitscollected.png"));
        scoresTexture = new Texture(Gdx.files.internal("scores.png"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));

        dropSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        clockSound = Gdx.audio.newSound(Gdx.files.internal("clock.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        texture = new Texture(Gdx.files.internal("maingamebg.png"));

        gameMusic.setLooping(true);
        gameMusic.play();

        font = new BitmapFont(Gdx.files.internal("pixel.fnt"));
        font.getData().setScale(2f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_SCREEN_X, GAME_SCREEN_Y);
        batch = new SpriteBatch();

        chara.createPlayer();
        element.create();
        bomb.create();


        exitButtonTexture = new Texture(Gdx.files.internal("exit.png"));
        float exitButtonSize = 32; //
        exitButtonBounds = new Rectangle(GAME_SCREEN_X - exitButtonSize - 10, GAME_SCREEN_Y - exitButtonSize - 10, exitButtonSize, exitButtonSize);
    }

    public void stunPlayer() {
        stunned = true;
    }

    public OrthographicCamera camera;
    public Texture dropImage;
    private Texture playerImageIdle;
    private Texture playerImageRight;
    private Texture playerImageLeft;
    private Texture playerImageStun;
    private Texture timerTexture;
    private Texture fruitsCollectedTexture;
    private Texture scoresTexture;
    public Texture bombImage;
    public Sound dropSound;
    public Sound clockSound;
    private Music gameMusic;
    public SpriteBatch batch;

    public BitmapFont font;
    private Texture texture;
    public int points;

    public final int GAME_SCREEN_X = 480;
    public final int GAME_SCREEN_Y = 640;

    public Rectangle player;

    private float margin = 0;
    public float stunTimer = 0;
    public float slowTimer = 0;
    public boolean stunned = false;
    public boolean slowed = false;

    public boolean clockPlayed = false;

    private float timer = 0;
    private float timerDuration = 60;

    Sound clickSound;
}
