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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    final Main game;

    private Player chara;
    private Element element;
    private GameEndScreen endScreen;
    private Boost boost;
    private TimerExtension timeAdd;
    private Texture trashButtonTexture;
    private Rectangle trashButtonBounds;

    private InputProcessor gameInputProcessor;

    public GameScreen(final Main game) {
        this.game = game;
        chara = new Player(this);
        element = new Element(this);
        boost = new Boost(this);
        timeAdd = new TimerExtension(this);
        initComponents();
        setupInputProcessor();
    }

    private void setupInputProcessor() {
        gameInputProcessor = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert touch coordinates to our game world coordinates
                Vector3 touchPos = new Vector3(screenX, screenY, 0);
                camera.unproject(touchPos);

                if (trashButtonBounds.contains(touchPos.x, touchPos.y)) {
                    element.clear();
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
        batch.draw(texture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        boolean isMovingLeft = false;
        boolean isMovingRight = false;

        if (boosted) {
            batch.draw(playerImageBoosted, player.x, player.y, 64, 64);
        } else {
            if (Gdx.input.isTouched()) {
                if (touchStartX == -1) {
                    // Record the initial touch position
                    touchStartX = Gdx.input.getX();
                }

                // Track the current touch position
                touchCurrentX = Gdx.input.getX();

                // Detect if touch movement is left or right
                if (touchCurrentX < touchStartX - 10) { // Swipe left
                    isMovingLeft = true;
                } else if (touchCurrentX > touchStartX + 10) { // Swipe right
                    isMovingRight = true;
                }
            } else {
                // Reset touch tracking when the screen is no longer touched
                touchStartX = -1;
            }

            // Draw the appropriate sprite based on movement
            if (isMovingLeft) {
                batch.draw(playerImageLeft, player.x, player.y, 64, 64); // Moving left
            } else if (isMovingRight) {
                batch.draw(playerImageRight, player.x, player.y, 64, 64); // Moving right
            } else {
                batch.draw(playerImageIdle, player.x, player.y, 64, 64); // Idle
            }
        }

        element.draw();


        boost.update(delta);


        batch.end();




        String pointsText = "" + points;
        float remainingTime = timerDuration - timer;
        updateTimer(Gdx.graphics.getDeltaTime());

        GlyphLayout pointsLayout = new GlyphLayout(font, pointsText);
        float pointsWidth = pointsLayout.width;
        float scoresTextureWidth = scoresTexture.getWidth();
        float pointsX = (scoresTextureWidth - pointsWidth) / 2 + GAME_SCREEN_X * 0.46f;

        batch.begin();
        boost.render();
        boost.move();
        timeAdd.render();
        timeAdd.move();
        batch.end();
        boost.draw(batch);
        timeAdd.draw(batch);

        batch.begin();
        batch.draw(bottomSpriteTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.055f);
        batch.end();

        chara.render();
        chara.processSpeed();
        chara.move();
        element.render();
        element.move();




        batch.begin();
        batch.draw(timerTexture, 10, GAME_SCREEN_Y - timerTexture.getHeight() - 10);
        batch.draw(scoresTexture, GAME_SCREEN_X * 0.46f, GAME_SCREEN_Y - timerTexture.getHeight() - 18);
        element.drawCollectedFruits();
        batch.end();


        batch.begin();
        font.draw(batch, pointsText, pointsX, GAME_SCREEN_Y * 0.95f);
        font.draw(batch, "" + (int) remainingTime, GAME_SCREEN_X * 0.13f, GAME_SCREEN_Y * 0.96f);
        batch.draw(trashButtonTexture, trashButtonBounds.x, trashButtonBounds.y, trashButtonBounds.width, trashButtonBounds.height);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        trashButtonBounds.setPosition(GAME_SCREEN_X - trashButtonBounds.width - 10, 10); // Position at bottom-right corner
    }

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
        playerImageBoosted.dispose();
        dropSound.dispose();
        gameMusic.dispose();
        font.dispose();
        element.dispose();
        timerTexture.dispose();
        fruitsCollectedTexture.dispose();
        trashButtonTexture.dispose(); // Dispose of the trash button texture
        clockSound.dispose();
        bottomSpriteTexture.dispose();
        boost.dispose();
        timeAdd.dispose();
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
    }

    public void updateTimer(float deltaTime) {
        timer += deltaTime;

        if (timer >= 60) {
            element.ELEMENT_SPEED = 300;
        }
        if (timer >= 40) {
            element.ELEMENT_SPEED = 200;
        }
        if (timer >= 20) {
            element.ELEMENT_SPEED = 120;
        }

        if (timer >= 80) {
            if (!clockPlayed) {
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
        playerImageIdle = new Texture(Gdx.files.internal("playerCatch.png"));
        boostImage = new Texture(Gdx.files.internal("boost2x.png"));
        timerExtensionImage = new Texture(Gdx.files.internal("boostTime.png"));
        playerImageRight = new Texture(Gdx.files.internal("playerCatchRight.png"));
        playerImageLeft = new Texture(Gdx.files.internal("playerCatchLeft.png"));
        playerImageBoosted = new Texture(Gdx.files.internal("playerCatchBoosted.png"));
        timerTexture = new Texture(Gdx.files.internal("timer.png"));
        fruitsCollectedTexture = new Texture(Gdx.files.internal("fruitscollected.png"));
        scoresTexture = new Texture(Gdx.files.internal("scores.png"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        bottomSpriteTexture = new Texture(Gdx.files.internal("black_blue_box.png"));
        bottomSpriteBounds = new Rectangle(0, 0, GAME_SCREEN_X, 50);
        dropSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        clockSound = Gdx.audio.newSound(Gdx.files.internal("clock.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        texture = new Texture(Gdx.files.internal("maingamebg.png"));

        gameMusic.setLooping(true);
        gameMusic.play();

        font = new BitmapFont(Gdx.files.internal("pixel.fnt"));
        font.getData().setScale(2f);

        camera = new OrthographicCamera();
        viewport = new StretchViewport(GAME_SCREEN_X, GAME_SCREEN_Y, camera); // Use StretchViewport to handle scaling
        batch = new SpriteBatch();

        chara.createPlayer();
        element.create();
        boost.create();
        timeAdd.create();

        trashButtonTexture = new Texture(Gdx.files.internal("discard.png")); // Load trash button texture
        float trashButtonSize = 64;
        trashButtonBounds = new Rectangle(
            (GAME_SCREEN_X - trashButtonSize) / 2, // Center horizontally
            10, // Adjust vertically as needed
            trashButtonSize,
            trashButtonSize
        );
    }

    public void boostPlayer() {
        boosted = true;
    }

    public void addTime(int seconds) {
        timerDuration += seconds;
    }


    public Texture boostImage;
    public OrthographicCamera camera;
    public Viewport viewport; // Updated viewport for handling stretching
    public Texture dropImage;
    private Texture playerImageIdle;
    private Texture playerImageRight;
    private Texture playerImageLeft;
    private Texture playerImageBoosted;
    public Texture timerExtensionImage;
    private Texture timerTexture;
    private Texture fruitsCollectedTexture;
    private Texture scoresTexture;
    public Sound dropSound;
    public Sound clockSound;
    private Music gameMusic;
    public SpriteBatch batch;
    private Texture bottomSpriteTexture;
    private Rectangle bottomSpriteBounds = new Rectangle();
    public BitmapFont font;
    private Texture texture;
    public int points;
    private float touchStartX = -1;
    private float touchCurrentX = -1;

    public final int GAME_SCREEN_X = 480;
    public final int GAME_SCREEN_Y = 640;

    public Rectangle player;

    private float margin = 0;
    public float stunTimer = 0;
    public float slowTimer = 0;
    public boolean boosted = false;
    public boolean slowed = false;

    public boolean clockPlayed = false;

    private float timer = 0;
    private float timerDuration = 90;

    Sound clickSound;
}

