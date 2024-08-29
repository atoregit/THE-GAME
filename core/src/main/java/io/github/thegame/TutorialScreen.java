package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class TutorialScreen implements Screen {
    final Main game;
    private final Stage stage;

    OrthographicCamera camera;

    public TutorialScreen(final Main game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 640);
        backgroundTexture = new Texture(Gdx.files.internal("tutorial.png"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        tutorialMusic = Gdx.audio.newMusic(Gdx.files.internal("scoresbgm.wav"));
    }


    @Override
    public void show() {

        tutorialMusic.setLooping(true);
        tutorialMusic.play();

        Texture exitButtonTexture = new Texture(Gdx.files.internal("back.png"));
        Image exitButtonImage = new Image(exitButtonTexture);
        exitButtonImage.setSize(64, 64);

        Table buttonTable = new Table();
        buttonTable.top().right();
        buttonTable.setFillParent(true);
        buttonTable.add(exitButtonImage).size(50, 50).padTop(10).padRight(10);

        stage.addActor(buttonTable);

        Skin skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));


        exitButtonImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                clickSound.play();
            }
        });

    }

    @Override
    public void render(float delta) {
//        ScreenUtils.clear(0, 0, 0.2f, 1);
//        game.font.getData().setScale(1.5f);
//
//        camera.update();
//        game.batch.setProjectionMatrix(camera.combined);
//
//        game.batch.begin();
//        game.font.draw(game.batch, "Welcome to THE fourth quarter project!!!", 100, 150);
//        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
//        game.batch.end();
//
//        if (Gdx.input.isTouched()) {
//            game.setScreen(new GameScreen(game));
//            dispose();
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            game.setScreen(new Automations());
//            dispose();
//        }



        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();



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

        tutorialMusic.dispose();
    }

    Texture backgroundTexture;
    Sound clickSound;
    Music tutorialMusic;

}
