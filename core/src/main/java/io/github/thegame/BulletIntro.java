package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.Scaling;

public class BulletIntro implements Screen {
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
    private static final float TRANSITION_DURATION = 0.5f;
    private Texture mainMenuBackground; // Store background texture for disposal

    private Image blackBlueBox;
    public BulletIntro(final Main game) {
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

        // Load background texture
        mainMenuBackground = new Texture(Gdx.files.internal("menubg.png"));

        // Set background image
        Image background = new Image(mainMenuBackground);
        background.setScaling(Scaling.stretch);
        background.setFillParent(true);
        root.addActor(background);
        // Create other UI elements
        table = new Table();
        table.setFillParent(true);
        root.addActor(table);

        blackBlueBox = new Image(new Texture(Gdx.files.internal("black_blue_box.png"))); // Load a texture or create a solid color
        blackBlueBox.setSize(VIRTUAL_WIDTH, stage.getHeight() * 0.15f); // 15% of screen height
        blackBlueBox.setPosition(0, 0); // Position it at the bottom of the screen
        blackBlueBox.setColor(1, 1, 1, 0.8f); // Set alpha to 0.5f for 50% transparency
        stage.addActor(blackBlueBox); // Add it to the stage so it's always visible


        Texture logoTexture = new Texture(Gdx.files.internal("shootersLogo.png"));
        Image logo = new Image(logoTexture);
        table.add(logo).width(Value.percentWidth(0.8f, table)).height(Value.percentWidth(0.25f, table)).padBottom(200);
        table.row();

        Texture leftArrowTexture = new Texture(Gdx.files.internal("leftArrow.png"));
        Texture rightArrowTexture = new Texture(Gdx.files.internal("rightArrow.png"));

        ImageTextButton rightButton = createButton("", rightArrowTexture, () -> {
            transitionToScreenRight(new GameIntro(game));
            clickSound.play();
        });
        ImageTextButton leftButton = createButton("", leftArrowTexture, () -> {
            transitionToScreenLeft(new MainMenuScreen(game));
            clickSound.play();
        });

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);


        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Table bottomTable = new Table();

        bottomTable.add(leftButton).width(35).height(75);

        Table labelTable = new Table();
        labelTable.add(new Label("YOU: ", labelStyle)).row();
        labelTable.add(new Label("YOU: ", labelStyle)).row();
        labelTable.add(new Label("YOU: ", labelStyle));

        bottomTable.add(labelTable).padLeft(60).padRight(60);

        bottomTable.add(rightButton).width(35).height(75);

        parameter.size = 80;
        parameter.color = Color.WHITE;
        BitmapFont fontz = generator.generateFont(parameter);
        table.add(bottomTable).padBottom(20);
        table.row();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = fontz;
        buttonStyle.fontColor = Color.WHITE;



        // Create button
        TextButton startButton = new TextButton("START GAME", buttonStyle);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clickSound.play();
                transitionToScreenUp(new BulletHellScreen(game));
            }
        });

        // Add button to a separate table for positioning
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        buttonTable.bottom();
        buttonTable.add(startButton).width(VIRTUAL_WIDTH).height(VIRTUAL_HEIGHT * 0.15f).padBottom(40);

        // Add button table to stage
        stage.addActor(buttonTable);

        generator.dispose();

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
        Gdx.gl.glClearColor(38/255f,40/255f,43/255f, 0.4f);// Clear screen with black color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the color buffer
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (!isTransitioning) {
            stage.getViewport().apply();
        }

        if (!isTransitioning && nextScreen != null) {
            Screen currentScreen = game.getScreen();
            game.setScreen(nextScreen);
            nextScreen = null;
            blackBlueBox.toFront();
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

    private void transitionToScreenRight(Screen newScreen) {
        if (isTransitioning) return;
        isTransitioning = true;
        nextScreen = newScreen;
        blackBlueBox.toFront();
        table.addAction(Actions.sequence(
            Actions.moveBy(-stage.getWidth(), 0, TRANSITION_DURATION, Interpolation.sine),
            Actions.run(() -> {
                isTransitioning = false;
                // The actual screen transition will happen in the render method
            })
        ));

        Table incomingTable = new Table();
        incomingTable.setFillParent(true);
        stage.addActor(incomingTable);

        Texture placeholder = new Texture(Gdx.files.internal("background.png"));
        Image placeholderImage = new Image(placeholder);
        placeholderImage.setScaling(Scaling.stretch);
        placeholderImage.setFillParent(true);
        incomingTable.add(placeholderImage);

        incomingTable.setPosition(stage.getWidth(), 0);
        incomingTable.addAction(Actions.moveBy(-stage.getWidth(), 0, TRANSITION_DURATION, Interpolation.sine));
    }
    private void transitionToScreenUp(Screen newScreen) {
        if (isTransitioning) return;
        isTransitioning = true;
        nextScreen = newScreen;
        blackBlueBox.toFront();
        table.addAction(Actions.sequence(
            Actions.moveBy(0, stage.getHeight(), TRANSITION_DURATION, Interpolation.sine),
            Actions.run(() -> {
                isTransitioning = false;
                // The actual screen transition will happen in the render method
            })
        ));

        Table incomingTable = new Table();
        incomingTable.setFillParent(true);
        stage.addActor(incomingTable);

        Texture placeholder = new Texture(Gdx.files.internal("menubg.png"));
        Image placeholderImage = new Image(placeholder);
        placeholderImage.setScaling(Scaling.stretch);
        placeholderImage.setFillParent(true);
        incomingTable.add(placeholderImage);

        incomingTable.setPosition(0, -stage.getHeight());
        incomingTable.addAction(Actions.moveBy(0, stage.getHeight(), TRANSITION_DURATION, Interpolation.sine));
    }

    private void transitionToScreenLeft(Screen newScreen) {
        if (isTransitioning) return;
        isTransitioning = true;
        nextScreen = newScreen;
        blackBlueBox.toFront();
        table.addAction(Actions.sequence(
            Actions.moveBy(stage.getWidth(), 0, TRANSITION_DURATION, Interpolation.sine),
            Actions.run(() -> {
                isTransitioning = false;
                // The actual screen transition will happen in the render method
            })
        ));

        Table incomingTable = new Table();
        incomingTable.setFillParent(true);
        stage.addActor(incomingTable);

        Texture placeholder = new Texture(Gdx.files.internal("menubg.png"));
        Image placeholderImage = new Image(placeholder);
        placeholderImage.setScaling(Scaling.stretch);
        placeholderImage.setFillParent(true);
        incomingTable.add(placeholderImage);

        incomingTable.setPosition(-stage.getWidth(), 0);
        incomingTable.addAction(Actions.moveBy(stage.getWidth(), 0, TRANSITION_DURATION, Interpolation.sine));
    }

    @Override
    public void dispose() {
        stage.dispose();
        menuMusic.dispose();
        clickSound.dispose();
        skin.dispose();
        mainMenuBackground.dispose(); // Dispose background texture
        blackBlueBox.remove();
    }
}
