package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ScoreScreen implements Screen {
    final Main game;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Texture backgroundTexture;

    private String[] playerNames = new String[5];
    private int[] scores = new int[5];

    public ScoreScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 640);
        backgroundTexture = new Texture(Gdx.files.internal("scorescreen.png"));
        scoreMusic = Gdx.audio.newMusic(Gdx.files.internal("scoresbgm.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
    }

    @Override
    public void show() {

        scoreMusic.setLooping(true);
        scoreMusic.play();

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));
        BitmapFont font = skin.getFont("font");
        font.getData().setScale(1.2f);

        Label[] scoreNames = new Label[5];
        Label[] scoreValues = new Label[5];

        Texture exitButtonTexture = new Texture(Gdx.files.internal("back.png"));
        Image exitButtonImage = new Image(exitButtonTexture);
        exitButtonImage.setSize(64, 64);

        for (int i = 0; i < 5; i++) {
            scoreNames[i] = new Label("text here", skin);
            scoreValues[i] = new Label("text here", skin);
        }


        exitButtonImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                clickSound.play();
            }
        });

        loadScores();

        table.defaults().space(2);
        table.row().pad(10, 0, 10, 0);

        for (int i = 0; i < 5; i++) {
            table.row().pad(10, 0, 10, 0);
            table.add(scoreNames[i]).padRight(20);
            table.add(scoreValues[i]).center();
        }

        table.row().pad(10, 0, 10, 0);


        for (int i = 0; i < 5; i++) {
            scoreNames[i].setText(playerNames[i]);
            scoreValues[i].setText(String.valueOf(scores[i]));
        }

        Table buttonTable = new Table();
        buttonTable.top().right(); // Align the button to the top right corner
        buttonTable.setFillParent(true);
        buttonTable.add(exitButtonImage).size(50, 50).padTop(10).padRight(10);
        stage.addActor(buttonTable);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void loadScores() {
        try {
            FileHandle file = Gdx.files.local("scores.txt");
            if (file.exists()) {
                String[] lines = file.readString().trim().split("\\r?\\n");

                Map<Integer, String> scoreMap = new TreeMap<>(Collections.reverseOrder());

                for (String line : lines) {
                    int lastSpaceIndex = line.lastIndexOf(' ');
                    if (lastSpaceIndex != -1) {
                        String playerName = line.substring(0, lastSpaceIndex);
                        String scoreStr = line.substring(lastSpaceIndex + 1);
                        if (scoreStr.matches("-?\\d+")) {
                            int score = Integer.parseInt(scoreStr);
                            scoreMap.put(score, playerName);
                        } else {
                            Gdx.app.error("ScoreManager", "Invalid score format in line: " + line);
                            for (int i = 0; i < 5; i++) {
                                playerNames[i] = "No scores yet";
                                scores[i] = 0;
                            }
                        }
                    } else {
                        Gdx.app.error("ScoreManager", "Invalid format in line: " + line);
                        for (int i = 0; i < 5; i++) {
                            playerNames[i] = "No scores yet";
                            scores[i] = 0;
                        }
                    }
                }

                int index = 0;
                for (Map.Entry<Integer, String> entry : scoreMap.entrySet()) {
                    if (index >= 5) break;
                    playerNames[index] = entry.getValue();
                    scores[index] = entry.getKey();
                    index++;
                }
            } else {
                Gdx.app.log("ScoreManager", "File does not exist.");
                for (int i = 0; i < 5; i++) {
                    playerNames[i] = "No scores yet";
                    scores[i] = 0;
                }
            }
        } catch (Exception e) {
            Gdx.app.error("ScoreManager", "Error loading data", e);
        }
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        scoreMusic.dispose();
        backgroundTexture.dispose();

    }
    public Music scoreMusic;
    Sound clickSound;
}
