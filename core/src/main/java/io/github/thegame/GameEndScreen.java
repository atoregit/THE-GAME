package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameEndScreen implements Screen {
    final Main game;
    private int finalPoints;
    private final Stage stage;
    private String playerName;
    private OrthographicCamera camera;
    private Sound clickSound;
    private Sound gameOver;
    private Texture calicoTexture;
    private Image calicoImage;

    public GameEndScreen(final Main game, int points) {
        this.game = game;
        finalPoints = points;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("sfx/gameover.wav"));
        calicoTexture = new Texture(Gdx.files.internal("calico.png"));
        calicoImage = new Image(calicoTexture);
    }

    @Override
    public void show() {
        gameOver.play();

        System.out.println(finalPoints);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));


        BitmapFont font = skin.getFont("font");
        font.getData().setScale(1.3f);

        Label affirmation = new Label("Good job!", skin);
        Label pointsLabel = new Label("You have a score of:", skin);
        Label points = new Label("" + finalPoints, skin);

        Label nameLabel = new Label("Enter your name here:", skin);
        TextField enterName = new TextField("", skin);
        enterName.setFocusTraversal(false);
        stage.setKeyboardFocus(enterName);


        ImageTextButton saveScore = new ImageTextButton("Save", skin);
        saveScore.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playerName = enterName.getText();
                saveData(finalPoints, playerName);
                System.out.println("Player name: " + playerName);
                clickSound.play();
            }
        });

        ImageTextButton newGame = new ImageTextButton("Back to menu", skin);
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        table.defaults().uniformX().center();
        table.row().pad(10, 0, 10, 0);
        table.add(affirmation).colspan(2).center();
        table.row().pad(10, 0, 10, 0);
        table.add(pointsLabel).colspan(2).center();
        table.row().pad(10, 0, 10, 0);
        table.add(points).colspan(2).center();
        table.row().pad(20, 0, 10, 0);
        table.add(nameLabel).padRight(10).padLeft(30).center();
        table.add(enterName).padLeft(10).center();

        table.row().pad(10, 0, 10, 0);
        table.add(saveScore).colspan(2).center();
        table.row().pad(10, 0, 10, 0);
        table.add(newGame).colspan(2).center();

        calicoImage.setPosition((800 - calicoImage.getWidth()) / 2, 10);
        stage.addActor(calicoImage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void saveData(int points, String name) {
        try {
            FileHandle scorefile = Gdx.files.local("scores.txt");
            scorefile.writeString(name + " " + points + "\n", true);
            Gdx.app.log("ScoreManager", "Data saved successfully.");
        } catch (Exception e) {
            Gdx.app.error("ScoreManager", "Error saving data", e);
        }
    }

    @Override
    public void resize(int i, int i1) {
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
        gameOver.dispose();
        calicoTexture.dispose();
    }
}
