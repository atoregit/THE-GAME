package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.Scaling;

public class MainMenuScreen implements Screen {
    final Main game;
    private final Stage stage;
    private Table table;
    private Skin skin;
    private Music menuMusic;
    private Sound clickSound;

    // Define constants for your desired virtual screen size
    private static final float VIRTUAL_WIDTH = 480;
    private static final float VIRTUAL_HEIGHT = 640;

    public MainMenuScreen(final Main game) {
        this.game = game;
        // Use ExtendViewport to maintain aspect ratio while extending the shorter dimension
        stage = new Stage(new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menumusic.mp3"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        menuMusic.setLooping(true);
        menuMusic.play();

        skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));

        createUI();
    }

    private void createUI() {
        // Create a root table that fills the screen
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Background
        Texture mainMenuBackground = new Texture(Gdx.files.internal("menubg.png"));
        Image background = new Image(mainMenuBackground);
        background.setScaling(Scaling.stretch);
        background.setFillParent(true);
        root.addActor(background);

        // Create a table for UI elements
        table = new Table();
        table.setFillParent(true);
        root.addActor(table);

        // Logo
        Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
        Image logo = new Image(logoTexture);

        // Buttons
        Texture buttonBackground = new Texture(Gdx.files.internal("button2.png"));
        ImageTextButton newGame = createButton("Play", buttonBackground, () -> {
            game.setScreen(new GameScreen(game));
            clickSound.play();
        });

        ImageTextButton scores = createButton("Scores", buttonBackground, () -> {
            game.setScreen(new ScoreScreen(game));
            clickSound.play();
        });

        ImageTextButton tutorial = createButton("Tutorial", buttonBackground, () -> {
            game.setScreen(new TutorialScreen(game));
            clickSound.play();
        });

        // Add elements to table
        table.add(logo).width(Value.percentWidth(0.7f, table)).height(Value.percentWidth(0.3f, table)).padBottom(40);
        table.row();
        table.add(newGame).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table)).padBottom(20);
        table.row();
        table.add(scores).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table)).padBottom(20);
        table.row();
        table.add(tutorial).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table));
    }

    private ImageTextButton createButton(String text, Texture background, Runnable action) {
        ImageTextButton button = new ImageTextButton(text, skin);
        button.getStyle().up = new Image(background).getDrawable();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                action.run();
            }
        });
        return button;
    }

    @Override
    public void show() {
        // Method intentionally left empty
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Debug rendering to show stage boundaries
        stage.getViewport().apply();
        game.batch.begin();
        game.font.draw(game.batch, "Stage size: " + stage.getWidth() + "x" + stage.getHeight(), 10, 20);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.invalidate();
    }

    @Override
    public void pause() {
        // Method intentionally left empty
    }

    @Override
    public void resume() {
        // Method intentionally left empty
    }

    @Override
    public void hide() {
        // Method intentionally left empty
    }
    private void transitionToScreen(Screen newScreen) {
        // Dispose of the current screen
        dispose();
        // Set and show the new screen
        game.setScreen(newScreen);
    }

    @Override
    public void dispose() {
        stage.dispose();
        menuMusic.dispose();
        clickSound.dispose();
        skin.dispose();
    }
}
