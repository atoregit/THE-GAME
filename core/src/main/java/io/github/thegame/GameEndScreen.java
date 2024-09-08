package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameEndScreen implements Screen {
    final Main game;
    private final Stage stage;
    private OrthographicCamera camera;
    private Sound gameOverSound;
    private Label gameOverLabel;
    private Label pointsLabel;
    private Label specialPointsLabel;
    private Texture background;
    private SpriteBatch batch;
    private float points;
    private float specialPoints;

    public GameEndScreen(final Main game, float points, float specialPoints) {
        this.game = game;
        this.points = points;
        this.specialPoints = specialPoints;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sfx/gameover.wav"));
        gameOverSound.play();
        background = new Texture("menubg.png");
        batch = new SpriteBatch();

        // Setup the game over screen
        setupGameOverScreen();
    }

    private void setupGameOverScreen() {

        Skin skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));
        BitmapFont font = skin.getFont("font");
        font.getData().setScale(6.0f); // Increase the font size for "GAME OVER"

        gameOverLabel = new Label("GAME OVER", skin);
        gameOverLabel.setFontScale(6.0f); // Make the text larger

        pointsLabel = new Label("Points collected - " + (int) points, skin);
        pointsLabel.setFontScale(4.0f); // Make the text smaller

        if (specialPoints != -1) {
            specialPointsLabel = new Label("Materials created - " + (int) specialPoints, skin);
            specialPointsLabel.setFontScale(4.0f); // Make the text smaller
        }

        Table table = new Table();
        table.setFillParent(true);
        table.add(gameOverLabel).center().padBottom(50); // Add some padding to move the label up
        table.row();
        table.add(pointsLabel).center();
        if (specialPoints != -1) {
            table.row();
            table.add(specialPointsLabel).center();
        }

        stage.addActor(table);

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

        batch.begin();
        batch.draw(background, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();


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
