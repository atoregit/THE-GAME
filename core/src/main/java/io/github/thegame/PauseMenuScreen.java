package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class PauseMenuScreen implements Screen {
    final Main game;
    private GameScreen gameScreen;
    private Stage stage;
    private OrthographicCamera camera;
    private Sound clickSound;
    private Texture backgroundTexture;

    public PauseMenuScreen(final Main game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        camera = new OrthographicCamera();
        backgroundTexture = new Texture(Gdx.files.internal("pauseMenu.png"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
    }

    @Override
    public void show() {
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        Gdx.input.setInputProcessor(stage);

        Texture exitTexture = new Texture(Gdx.files.internal("exit.png"));
        Texture backTexture = new Texture(Gdx.files.internal("back.png"));

// Create a new texture that is 5 times bigger than the original texture
        Texture exitTextureBig = new Texture(Gdx.files.internal("exit.png"));
        exitTextureBig.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Pixmap pixmap = new Pixmap(exitTexture.getWidth() * 2, exitTexture.getHeight() * 2, Pixmap.Format.RGBA8888);
        Pixmap originalPixmap = new Pixmap(Gdx.files.internal("exit.png"));
        pixmap.drawPixmap(originalPixmap, 0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(), 0, 0, pixmap.getWidth(), pixmap.getHeight());
        exitTextureBig = new Texture(pixmap);
        pixmap.dispose();
        originalPixmap.dispose();

// Create a new texture that is 5 times bigger than the original texture
        Texture backTextureBig = new Texture(Gdx.files.internal("back.png"));
        backTextureBig.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Pixmap pixmapBack = new Pixmap(backTexture.getWidth() * 2, backTexture.getHeight() * 2, Pixmap.Format.RGBA8888);
        Pixmap originalPixmapBack = new Pixmap(Gdx.files.internal("back.png"));
        pixmapBack.drawPixmap(originalPixmapBack, 0, 0, originalPixmapBack.getWidth(), originalPixmapBack.getHeight(), 0, 0, pixmapBack.getWidth(), pixmapBack.getHeight());
        backTextureBig = new Texture(pixmapBack);
        pixmapBack.dispose();
        originalPixmapBack.dispose();

// Create the ImageButton with the new texture
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitTextureBig)));
        exitButton.setPosition(Gdx.graphics.getWidth() - exitTextureBig.getWidth() - 20, Gdx.graphics.getHeight() - exitTextureBig.getHeight() - 20);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gameScreen.endGame();
                clickSound.play();
            }
        });

        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backTextureBig)));
        backButton.setPosition(20, Gdx.graphics.getHeight() - backTextureBig.getHeight() - 20);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
                clickSound.play();
            }
        });

        stage.addActor(exitButton);
        stage.addActor(backButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        clickSound.dispose();
        backgroundTexture.dispose();
    }
}
