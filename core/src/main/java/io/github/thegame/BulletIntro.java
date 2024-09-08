package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BulletIntro implements Screen {
    final Main game;
    private final Stage stage;
    private Table table;
    private Skin skin;
    private Music menuMusic, left, right, play;
    private Sound clickSound;
    private boolean isTransitioning = false;
    private Screen nextScreen;
    private static final float VIRTUAL_WIDTH = 480;
    private static final float VIRTUAL_HEIGHT = 640;
    private static final float TRANSITION_DURATION = 0.25f;
    private Texture mainMenuBackground; // Store background texture for disposal
    private float topCatcherScore;
    private TextButton startButton;


    private Image blackBlueBox;
    public BulletIntro(final Main game) {
        this.game = game;

        stage = new Stage(new ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sfx/shootersMenu.wav"));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("sfx/click.wav"));
        menuMusic.setVolume(0.6f);
        menuMusic.setLooping(true);
        menuMusic.play();
        left = Gdx.audio.newMusic(Gdx.files.internal("sfx/left.wav"));
        right = Gdx.audio.newMusic(Gdx.files.internal("sfx/right.wav"));
        play = Gdx.audio.newMusic(Gdx.files.internal("sfx/play.wav"));
        skin = new Skin(Gdx.files.internal("skin/terra-mother-ui.json"));

        createUI();
    }

    private void createUI() {
        // Create a root table that fills the screen
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Load background texture
        mainMenuBackground = new Texture(Gdx.files.internal("backgroundShooters.png"));

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
        table.add(logo).width(Value.percentWidth(0.8f, table)).height(Value.percentWidth(0.25f, table)).padBottom(100);
        table.row();

        Texture leftArrowTexture = new Texture(Gdx.files.internal("leftArrow.png"));
        Texture rightArrowTexture = new Texture(Gdx.files.internal("rightArrow.png"));
        Texture help = new Texture(Gdx.files.internal("help.png"));
        Drawable leftDraw = new TextureRegionDrawable(leftArrowTexture);
        Drawable rightDraw = new TextureRegionDrawable(rightArrowTexture);
        Drawable helpDraw =  new TextureRegionDrawable(help);
        ImageButton leftButton =new ImageButton(leftDraw);
        ImageButton rightButton = new ImageButton(rightDraw);
        ImageButton helpButton = new ImageButton(helpDraw);

        rightButton.setSize(40, 120);
        leftButton.setSize(40, 120);
        helpButton.setSize(60, 120);
        leftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                left.play();
                transitionToScreenLeft(new MainMenuScreen(game));
            }
        });
        rightButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                right.play();
                transitionToScreenRight(new GameIntro(game));
            }
        });
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                left.play();
                transitionToScreenDown(new BulletTutorial(game));
            }
        });
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);


        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Table bottomTable = new Table();

        bottomTable.add(leftButton);

        List<Float> topScores = getTopScores();
        Table labelTable = new Table();
        if (topScores.size() > 0) {
            labelTable.add(new Label("TOP SCORE: " + (int) ((Number) topScores.get(0)).floatValue(), labelStyle)).row();
        }
        if (topScores.size() > 1) {
            labelTable.add(new Label("2ND - " + (int) ((Number) topScores.get(1)).floatValue(), labelStyle)).row();
        }
        if (topScores.size() > 2) {
            labelTable.add(new Label("3RD - " + (int) ((Number) topScores.get(2)).floatValue(), labelStyle)).row();
        }

        bottomTable.add(labelTable).padLeft(80).padRight(80);

        bottomTable.add(rightButton);

        parameter.size = 80;
        parameter.color = Color.WHITE;
        BitmapFont fontz = generator.generateFont(parameter);
        table.add(bottomTable).padBottom(100);
        table.row();
        table.add(helpButton).center().padTop(10);
        table.row();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = fontz;
        buttonStyle.fontColor = Color.WHITE;



        // Create button
        startButton = new TextButton("START GAME", buttonStyle);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                play.play();
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



    @Override
    public void show() {
        // Method intentionally left empty
    }

    public float getTopCatcherScore() {
        // Read scores from file
        try {
            String filename = "scores1.txt";
            FileHandle file = Gdx.files.local(filename);
            if (!file.exists()) {
                // If the file does not exist, return 0
                return 0;
            }
            String scoresStr = file.readString().trim();
            if (scoresStr.isEmpty()) {
                // If the file is empty, return 0
                return 0;
            }
            String[] scoresArray = scoresStr.split("\\r?\\n");
            List<Float> scoresList = new ArrayList<>();
            for (String scoreStr : scoresArray) {
                try {
                    float score = Float.parseFloat(scoreStr);
                    scoresList.add(score);
                } catch (NumberFormatException e) {
                    // Handle invalid input
                    Gdx.app.log("Error", "Invalid score value: " + scoreStr);
                }
            }
            // Sort the list in descending order
            Collections.sort(scoresList, Collections.reverseOrder());
            // Return the top score
            return scoresList.isEmpty() ? 0 : scoresList.get(0);
        } catch (Exception e) {
            Gdx.app.log("Error", "Failed to read scores: " + e.getMessage());
            return 0;
        }
    }

    public List<Float> getTopScores() {
        // Read scores from file
        try {
            String filename = "scores2.txt";
            FileHandle file = Gdx.files.local(filename);
            if (!file.exists()) {
                // If the file does not exist, return an empty list
                return new ArrayList<>();
            }
            String scoresStr = file.readString().trim();
            if (scoresStr.isEmpty()) {
                // If the file is empty, return an empty list
                return new ArrayList<>();
            }
            String[] scoresArray = scoresStr.split("\\r?\\n");
            List<Float> scoresList = new ArrayList<>();
            for (String scoreStr : scoresArray) {
                try {
                    float score = Float.parseFloat(scoreStr);
                    scoresList.add(score);
                } catch (NumberFormatException e) {
                    // Handle invalid input
                    Gdx.app.log("Error", "Invalid score value: " + scoreStr);
                }
            }
            // Sort the list in descending order
            Collections.sort(scoresList, Collections.reverseOrder());
            // Return the top 3 scores
            return scoresList.subList(0, Math.min(3, scoresList.size()));
        } catch (Exception e) {
            Gdx.app.log("Error", "Failed to read scores: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Image dimImage;

    public void render(float delta) {
        topCatcherScore = getTopCatcherScore();
        Gdx.gl.glClearColor(38/255f,40/255f,43/255f, 0.4f);// Clear screen with black color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the color buffer
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        if (topCatcherScore < 30) {
            // Add a label to display the message on the start button area
            if (dimImage == null) {
                dimImage = new Image(new Texture(Gdx.files.internal("blackgrad.png")));
                dimImage.setColor(0, 0, 0, 1);
                dimImage.setSize(VIRTUAL_WIDTH, stage.getHeight() * 0.4f); // 15% of screen height
                dimImage.setPosition(0, 0); // Position it at the bottom of the screen
                stage.addActor(dimImage);
            }
            if (scoreLabel == null) {
                scoreLabel = new Label("You need to score at least 30 points\nin Catcher to unlock this game!", labelStyle);
                scoreLabel.setAlignment(Align.center);
                float x = Gdx.graphics.getWidth() / 4;
                float y = Gdx.graphics.getHeight() * 0.03f;
                scoreLabel.setPosition(x - scoreLabel.getWidth() / 2 - 25, y);
                scoreLabel.setFontScale(1f); // Scale down the font to make it smaller
                stage.addActor(scoreLabel);
            }


            // Make the play button inactive
            startButton.setDisabled(true);
        } else {
            // Reset the screen to normal
            if (scoreLabel != null) {
                scoreLabel.remove();
                scoreLabel = null;
            }

            // Remove the dim image
            if (dimImage != null) {
                dimImage.remove();
                dimImage = null;
            }

            // Make the play button active
            startButton.setDisabled(false);
        }

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
        blackBlueBox.toFront();
        Texture placeholder = new Texture(Gdx.files.internal("menubg.png"));
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
        blackBlueBox.toFront();
        Texture placeholder = new Texture(Gdx.files.internal("menubg.png"));
        Image placeholderImage = new Image(placeholder);
        placeholderImage.setScaling(Scaling.stretch);
        placeholderImage.setFillParent(true);
        incomingTable.add(placeholderImage);

        incomingTable.setPosition(0, -stage.getHeight());
        incomingTable.addAction(Actions.moveBy(0, stage.getHeight(), TRANSITION_DURATION, Interpolation.sine));
    }
    private void transitionToScreenDown(Screen newScreen) {
        if (isTransitioning) return;
        isTransitioning = true;
        nextScreen = newScreen;
        blackBlueBox.toFront();
        table.addAction(Actions.sequence(
            Actions.moveBy(0, -stage.getHeight(), TRANSITION_DURATION, Interpolation.sine),
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

        incomingTable.setPosition(0, stage.getHeight());
        incomingTable.addAction(Actions.moveBy(0, -stage.getHeight(), TRANSITION_DURATION, Interpolation.sine));
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
        blackBlueBox.toFront();
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
        left.dispose();
        right.dispose();
        play.dispose();
    }
    private Label scoreLabel;
    private Label.LabelStyle labelStyle;

}
