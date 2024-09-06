package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameEndScreen implements Screen {
    final Main game;
    private final Stage stage;
    private OrthographicCamera camera;
    private Sound gameOverSound;
    private Label gameOverLabel;

    public GameEndScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sfx/gameover.wav"));

        // Setup the game over screen
        setupGameOverScreen();
    }

    private void setupGameOverScreen() {
        gameOverSound.play();

        Skin skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));
        BitmapFont font = skin.getFont("font");
        font.getData().setScale(2.0f); // Increase the font size for "GAME OVER"

        gameOverLabel = new Label("GAME OVER", skin);
        gameOverLabel.setFontScale(3.0f); // Make the text larger

        Table table = new Table();
        table.setFillParent(true);
        table.add(gameOverLabel).center();

        stage.addActor(table);

        gameOverSound.play();

        // Schedule a transition to the MainMenuScreen after 10 seconds
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }, 4);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        gameOverSound.dispose();
    }
    public void show() {

    }
}
