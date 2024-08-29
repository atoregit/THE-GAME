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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    final Main game;
    private final Stage stage;

    public MainMenuScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menumusic.mp3"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        menuMusic.setLooping(true);
        menuMusic.play();
    }

    @Override
    public void show() {
        Texture buttonBackground = new Texture(Gdx.files.internal("button2.png")); // Background for buttons

        Texture mainMenuBackground = new Texture(Gdx.files.internal("menubg.png"));
        Image background = new Image(mainMenuBackground);
        background.setSize(480, 640);
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));

        Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));

        ImageTextButton newGame = new ImageTextButton("Play", skin);
        newGame.getStyle().up = new Image(buttonBackground).getDrawable();
        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
                clickSound.play();
                dispose();
            }
        });

        ImageTextButton scores = new ImageTextButton("Scores", skin);
        scores.getStyle().up = new Image(buttonBackground).getDrawable();
        scores.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ScoreScreen(game));
                clickSound.play();
                dispose();

            }
        });

        ImageTextButton tutorial = new ImageTextButton("Tutorial", skin);
        tutorial.getStyle().up = new Image(buttonBackground).getDrawable();
        tutorial.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new TutorialScreen(game));
                clickSound.play();
                dispose();
            }
        });

        table.row().pad(50, 0, 40, 0);
        table.add(logo).width(Value.percentWidth(0.7f, table)).height(Value.percentWidth(0.3f, table));

        table.defaults().fillX().uniformX().height(50);
        table.row().pad(10, 0, 10, 0);
        table.add(newGame);
        table.row().pad(10, 0, 10, 0);
        table.add(scores);
        table.row().pad(10, 0, 10, 0);
        table.add(tutorial);
        table.row().pad(10, 0, 10, 0);
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
        menuMusic.dispose();
    }

    private Music menuMusic;
    private Sound clickSound;
}
