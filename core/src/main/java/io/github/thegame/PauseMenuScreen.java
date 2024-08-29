package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseMenuScreen implements Screen {
    final Main game;
    private GameScreen gameScreen;
    private Stage stage;
    private Skin skin;
    private OrthographicCamera camera;

    public PauseMenuScreen(final Main game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 640);
        backgroundTexture = new Texture(Gdx.files.internal("pause.png"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(480, 640, camera));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));


        Texture buttonTexture = new Texture(Gdx.files.internal("button2.png"));
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.font = skin.getFont("font");

        ImageTextButton resumeButton = new ImageTextButton("Resume", buttonStyle);
        ImageTextButton mainMenuButton = new ImageTextButton("Main Menu", buttonStyle);
        ImageTextButton helpButton = new ImageTextButton("Help", buttonStyle);

        BitmapFont font = skin.getFont("font");
        font.getData().setScale(1.2f);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
                clickSound.play();
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                gameScreen.dispose();
                clickSound.play();
            }
        });

        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TutorialScreen(game));
                gameScreen.dispose();
                clickSound.play();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.defaults().uniformX();
        table.add(resumeButton).size(200, 60).padBottom(20).colspan(2);
        table.row();
        table.add(mainMenuButton).size(200, 60).padBottom(20);
        table.add(helpButton).size(200, 60).padBottom(20);


        table.setWidth(440);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
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
        skin.dispose();
        clickSound.dispose();
        backgroundTexture.dispose();
    }

    public Sound clickSound;
    Texture backgroundTexture;
}
