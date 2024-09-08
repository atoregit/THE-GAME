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

public class GameTutorial implements Screen {
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
    private static final float TRANSITION_DURATION = 0.25f;
    private Texture mainMenuBackground; // Store background texture for disposal
    private Music left;
    private Image blackBlueBox;
    public GameTutorial(final Main game) {
        this.game = game;

        stage = new Stage(new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menumusic.mp3"));
        menuMusic.setLooping(true);
        menuMusic.play();
        left = Gdx.audio.newMusic(Gdx.files.internal("sfx/left.wav"));
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

        Texture leftArrowTexture = new Texture(Gdx.files.internal("upArrow.png"));

        ImageTextButton leftButton = createButton("", leftArrowTexture, () -> {
            transitionToScreenUp(new GameIntro(game));
            left.play();
        });

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);


        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);



        parameter.size = 80;
        parameter.color = Color.WHITE;
        BitmapFont catchFont = generator.generateFont(parameter);
        Label.LabelStyle catchLabelStyle = new Label.LabelStyle(catchFont, Color.WHITE);
        Label catchLabel = new Label("CATCH", catchLabelStyle);

        // Create instructions label with smaller font
        parameter.size =30;
        BitmapFont instructionsFont = generator.generateFont(parameter);
        Label.LabelStyle instructionsLabelStyle = new Label.LabelStyle(instructionsFont, Color.WHITE);
        Label instructionsLabel = new Label(
            "1. Create the target item by forming the correct chemical compound.\n" +
                "2. Tap left or right to move the catcher and collect falling elements.\n" +
                "3. Combine elements in the right order. Discard wrong ones with the trash button.\n" +
                "4. Earn points for each correct compound and get a new target item.\n" +
                "5. Form as many items as possible to get the highest score before time's up!",
            instructionsLabelStyle
        );
        instructionsLabel.setWrap(true);

        // Create a container for centered content
        Table contentTable = new Table();
        contentTable.add(catchLabel).padBottom(20).row();
        contentTable.add(instructionsLabel).width(VIRTUAL_WIDTH * 0.8f).row();
        contentTable.row();
        contentTable.add(leftButton).padTop(20);
        // Add the content table to the main table
        table.add(contentTable).expand().fill().center();
        table.row();

        generator.dispose();


    }

    private ImageTextButton createButton(String text, Texture background, Runnable action) {
        ImageTextButton button = new ImageTextButton(text, skin);
        button.getStyle().up = new Image(background).getDrawable();
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return button;
    }

    @Override
    public void show() {
        // Method intentionally left empty
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
        blackBlueBox.toFront();
        Texture placeholder = new Texture(Gdx.files.internal("menubg.png"));
        Image placeholderImage = new Image(placeholder);
        placeholderImage.setScaling(Scaling.stretch);
        placeholderImage.setFillParent(true);
        incomingTable.add(placeholderImage);

        incomingTable.setPosition(0, -stage.getHeight());
        incomingTable.addAction(Actions.moveBy(0, stage.getHeight(), TRANSITION_DURATION, Interpolation.sine));
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
            blackBlueBox.toFront();
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





    @Override
    public void dispose() {
        stage.dispose();
        menuMusic.dispose();
        skin.dispose();
        mainMenuBackground.dispose(); // Dispose background texture
        blackBlueBox.remove();
        left.dispose();
    }
}
