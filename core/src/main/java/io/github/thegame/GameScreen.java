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

    public void endGame() {
        gameMusic.stop();
        game.setScreen(new GameEndScreen(game, points, specialPoints));
    }

    private void pauseGame() {
        game.setScreen(new PauseMenuScreen(game, this));
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

                if (helpButtonBounds.contains(touchPos.x, touchPos.y)) {
                    game.pause();
                    pauseGame();
                }


                return false;
            }
        };
    }

    @Override
    public void render(float delta) {
        element.update(delta);

        ScreenUtils.clear(1, 1, 1, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(texture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());



        element.draw();
        boost.update(delta);

        if (boosted) {
            batch.draw(playerImageBoosted, playerImageBoostedX, player.y, 150, 100);
        } else {
            batch.draw(playerImageIdle, playerImageIdleX, player.y, 150, 100);
        }

        batch.draw(playerImage, player.x, player.y, 72, 64);

        // Update playerImageBoostedX and playerImageIdleX with a smaller delay
        playerImageBoostedX += (player.x - 100 - playerImageBoostedX) * 0.4f;
        playerImageIdleX += (player.x - 100 - playerImageIdleX) * 0.4f;

        batch.draw(playerImage, player.x, player.y, 72, 64);


        batch.end();

        batch.begin();
        batch.draw(helpTexture, helpButtonBounds.x, helpButtonBounds.y, helpButtonBounds.width, helpButtonBounds.height);
        batch.end();


        String pointsText = "" + (int)points;
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



        chara.render();
        chara.processSpeed();
        chara.move();
        element.render();
        element.move();




        batch.begin();
        batch.draw(timerTexture, 10, GAME_SCREEN_Y - timerTexture.getHeight() - 10);
        batch.draw(scoresTexture, GAME_SCREEN_X * 0.46f, GAME_SCREEN_Y - 50 -  18, 70, 50);
        batch.end();

        batch.begin();

        batch.end();
        batch.begin();

        batch.draw(bottomSpriteTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.055f);
        batch.end();



        batch.begin();
        element.drawCollectedFruits();
        font.draw(batch, pointsText, pointsX, GAME_SCREEN_Y * 0.95f);
        font.draw(batch, "" + (int) remainingTime, GAME_SCREEN_X * 0.13f, GAME_SCREEN_Y * 0.96f);
        batch.draw(trashButtonTexture, trashButtonBounds.x, trashButtonBounds.y, trashButtonBounds.width, trashButtonBounds.height);
        element.drawSplash(batch);
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
        if (playerImageIdle != null) {
            playerImageIdle.dispose();
        }
        if (playerImageBoosted != null) {
            playerImageBoosted.dispose();
        }
        if (timerTexture != null) {
            timerTexture.dispose();
        }
        if (fruitsCollectedTexture != null) {
            fruitsCollectedTexture.dispose();
        }
        if (trashButtonTexture != null) {
            trashButtonTexture.dispose(); // Dispose of the trash button texture
        }
        if (clockSound != null) {
            clockSound.dispose();
        }
        if (bottomSpriteTexture != null) {
            bottomSpriteTexture.dispose();
        }
        if (boost != null) {
            boost.dispose();
        }
        if (timeAdd != null) {
            timeAdd.dispose();
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (clickSound != null) {
            clickSound.dispose();
        }
        if (dropSound != null) {
            dropSound.dispose();
        }
        if (gameMusic != null) {
            gameMusic.dispose();
        }
        if (font != null) {
            font.dispose();
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

        if (timerDuration-timer == 10) {
            if (!clockPlayed) {
                clockSound.play();
                clockPlayed = true;
            }
        }

        if (timer >= timerDuration) {
            gameMusic.stop();
            endGame();
        }
    }

    public void initComponents() {
        playerImageIdle = new Texture(Gdx.files.internal("scientist.png"));
        boostImage = new Texture(Gdx.files.internal("boost2x.png"));
        timerExtensionImage = new Texture(Gdx.files.internal("boostTime.png"));
        playerImage= new Texture(Gdx.files.internal("flaskWater.png"));
        playerImageBoosted = new Texture(Gdx.files.internal("scientistBoosted.png"));
        timerTexture = new Texture(Gdx.files.internal("timer.png"));
        fruitsCollectedTexture = new Texture(Gdx.files.internal("fruitscollected.png"));
        scoresTexture = new Texture(Gdx.files.internal("scores.png"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        bottomSpriteTexture = new Texture(Gdx.files.internal("bottomBoxCatchers.png"));
        bottomSpriteBounds = new Rectangle(0, 0, GAME_SCREEN_X, 50);
        dropSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        clockSound = Gdx.audio.newSound(Gdx.files.internal("clock.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("catchersBg.wav"));
        gameMusic.setVolume(0.6f);

        texture = new Texture(Gdx.files.internal("backgroundCatchers.png"));

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



        helpTexture = new Texture(Gdx.files.internal("help.png"));
        float helpButtonWidth = 45; // Increase the width
        float helpButtonHeight = 32; // Increase the height
        helpButtonBounds = new Rectangle(GAME_SCREEN_X - helpButtonWidth - 10, GAME_SCREEN_Y - helpButtonHeight - 10, helpButtonWidth, helpButtonHeight);

        trashButtonTexture = new Texture(Gdx.files.internal("discard.png")); // Load trash button texture
        float trashButtonSize = 64;
        trashButtonBounds = new Rectangle(
            (GAME_SCREEN_X - trashButtonSize) / 2, // Center horizontally
            10, // Adjust vertically as needed
            trashButtonSize,
            trashButtonSize
        );

        playerImageBoostedX = player.x - 120;
        playerImageIdleX = player.x - 120;
    }

    public void boostPlayer() {
        boosted = true;
    }

    public void addTime(int seconds) {
        timerDuration += seconds;
    }

    public void parallaxMovement() {

    }


    public Texture boostImage;
    public OrthographicCamera camera;
    public Viewport viewport; // Updated viewport for handling stretching
    private Texture playerImageIdle;
    private Texture playerImage;
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
    public float points = 0;
    public float specialPoints;
    private float touchStartX = -1;
    private float touchCurrentX = -1;
    public float displayPoints;
    public Texture helpTexture;
    public final int GAME_SCREEN_X = 480;
    public final int GAME_SCREEN_Y = 640;

    public Rectangle player;

    private float margin = 0;
    public float stunTimer = 0;
    public float slowTimer = 0;
    public boolean boosted = false;
    public boolean slowed = false;
    private Texture backTexture;
    private Rectangle backButtonBounds;
    public boolean clockPlayed = false;
    private Rectangle helpButtonBounds;
    private float timer = 0;
    private float timerDuration = 90;
    private float playerImageBoostedX;
    private float playerImageIdleX;
    Sound clickSound;
}

