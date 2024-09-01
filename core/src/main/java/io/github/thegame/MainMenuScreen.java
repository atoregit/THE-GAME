package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
    private boolean isTransitioning = false;
    private Screen nextScreen;
    private static final float VIRTUAL_WIDTH = 480;
    private static final float VIRTUAL_HEIGHT = 640;
    private static final float TRANSITION_DURATION = 2f;
    public MainMenuScreen(final Main game) {
        this.game = game;

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

        table = new Table();
        table.setFillParent(true);
        root.addActor(table);

        Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
        Image logo = new Image(logoTexture);

        Texture buttonBackground = new Texture(Gdx.files.internal("button2.png"));
        ImageTextButton newGame = createButton("Play", buttonBackground, () -> {
            transitionToScreen(new GameScreen(game));
            clickSound.play();
        });

        ImageTextButton scores = createButton("Scores", buttonBackground, () -> {
            game.setScreen(new ScoreScreen(game));
            clickSound.play();
        });

        ImageTextButton tutorial = createButton("Tutorial", buttonBackground, () -> {
            transitionToScreen(new TutorialScreen(game));
            clickSound.play();
        });

        ImageTextButton bullet = createButton("Bullet", buttonBackground, () -> {
            transitionToScreen(new BulletHellScreen(game));
            clickSound.play();
        });

        // Add elements to table
        table.add(logo).width(Value.percentWidth(0.7f, table)).height(Value.percentWidth(0.3f, table)).padBottom(40);
        table.row();
        table.add(newGame).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table)).padBottom(20);
        table.row();
        table.add(scores).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table)).padBottom(20);
        table.row();
        table.add(tutorial).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table)).padBottom(20);
        table.row();
        table.add(bullet).width(Value.percentWidth(0.6f, table)).height(Value.percentHeight(0.1f, table));
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

        if (!isTransitioning) {
            stage.getViewport().apply();
        }

        if (!isTransitioning && nextScreen != null) {
            Screen currentScreen = game.getScreen();
            game.setScreen(nextScreen);
            nextScreen = null;

            if (currentScreen != null) {
                currentScreen.dispose();
            }
        }
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
        if (isTransitioning) return;
        isTransitioning = true;
        nextScreen = newScreen;

        table.addAction(Actions.sequence(
            Actions.moveBy(-stage.getWidth(), 0, TRANSITION_DURATION, Interpolation.sine),
            Actions.run(() -> {
                isTransitioning = false;
                // The actual screen transition will happen in the render method
            })
        ));

        // Create a new table for the incoming screen
        Table incomingTable = new Table();
        incomingTable.setFillParent(true);
        stage.addActor(incomingTable);

        // Add a placeholder texture or color to represent the incoming screen
        Texture placeholder = new Texture(Gdx.files.internal("placeholder.png"));
        Image placeholderImage = new Image(placeholder);
        placeholderImage.setScaling(Scaling.stretch);
        placeholderImage.setFillParent(true);
        incomingTable.add(placeholderImage);

        // Animate the incoming table
        incomingTable.setPosition(stage.getWidth(), 0);
        incomingTable.addAction(Actions.moveBy(-stage.getWidth(), 0, TRANSITION_DURATION, Interpolation.sine));
    }

    @Override
    public void dispose() {
        stage.dispose();
        menuMusic.dispose();
        clickSound.dispose();
        skin.dispose();
    }
}
