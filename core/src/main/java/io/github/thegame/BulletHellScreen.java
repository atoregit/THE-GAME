package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Random;

public class BulletHellScreen implements Screen {
    final Main game;
    private Stage stage;
    private BulletPlayer player;
    private Pool<BulletEnemy> enemyPool;
    private Pool<ChemicalSymbol> symbolPool;
    private Pool<Bullet> bulletPool;

    private Array<BulletEnemy> enemies;
    private Array<ChemicalSymbol> symbols;
    private Array<Bullet> bullets;

    private List<String> powerUps = new ArrayList<String>();
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private static final float PARTICLE_DURATION = 0.175f;


    private static final float ENEMY_SPAWN_INTERVAL = 2.4f; // Seconds between enemy spawns
    private static final float SYMBOL_SPAWN_INTERVAL = 10f; // Seconds between symbol spawns
    private float enemySpawnTimer;
    private float symbolSpawnTimer;

    private static final float DIFFICULTY_INCREASE_INTERVAL = 60f; // Seconds between difficulty increases
    private float difficultyIncreaseTimer;
    private float timeSinceLastShot;

    private Array<BulletDamage> damageIndicators;
    private Array<BulletDamage> indicatorsToRemove;

    private Array<BulletEnemy> enemiesToRemove;
    private Array<ChemicalSymbol> symbolsToRemove;
    private Array<Bullet> bulletsToRemove;
    private Pool<ParticleEffect> particlePool;
    private Array<ParticleEffect> activeParticles;

    private BitmapFont font;
    private StringBuilder collectedSymbols;
    private float stunTimer;
    private static final float STUN_DURATION = 0.5f;

    private float difficultyMultiplier = 1.0f;
    private long gameStartTime;
    private float viewportWidth;
    private float viewportHeight;
    private boolean isTouchingLeft = false;
    private boolean isTouchingRight = false;

    private boolean isPaused = false;
    private Texture pauseButtonTexture;
    private Vector2 pauseButtonPosition;
    private static final float PAUSE_BUTTON_SIZE = 50f;
    private Texture transparent;
    private Texture heart;
    private Texture inputHighlight;
    private boolean isInitialState = true;
    private Texture blackbg;
    private Texture mainMenuBackground;
    private float scaleX, scaleY;
    private float times;
    private Music hit;
    private Music scout;
    private Music getChemical;
    private Music setChemical;
    private Music wrongChemical;
    private Music shoot;
    private Music bgmusic;
    private Music dead;
    private Music select;
    private static final String FLAME_PARTICLE = "flamecool.p";
    private static final String SCOUT_PARTICLE = "scout.p";

    private static final float POWER_UP_DURATION = 30f; // Duration in seconds
    private Map<String, Float> activePowerUps;
    private String currentPowerUp = "";
    private BulletShake screenShake;
    private ShapeRenderer shapeRenderer;
    private static final float GLOW_RADIUS = 10f;
    private Texture mainbg;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private float movementSmoothing = 0f; // Adjust this value to change smoothing (0.0 to 1.0)
    private float currentVelocity = 0f;
    private Texture red;
    private int healTimer;
    private Array<EnemyBullet> enemyBullets;
    private Array<EnemyBullet> enemyBulletsToRemove;
    private Pool<EnemyBullet> enemyBulletPool;

    private static final float BOSS_SPAWN_INTERVAL_MIN = 300f;
    private static final float BOSS_SPAWN_INTERVAL_MAX = 360f;
    private float bossSpawnTimer;
    private float nextBossSpawnTime;
    private boolean isBossActive;
    private BulletBoss currentBoss;
    private float bossEntranceTimer = 0f;
    private static final float BOSS_ENTRANCE_DURATION = 5f;
    private static final float BOSS_PAUSE_DURATION = 2f;
    private boolean isBossEntering = false;

    private boolean showSplash = false;
    private float splashTime = 0f;
    private Texture splashTexture;
    private Texture splashRectangleTexture;
    private float splashTextureWidth = 200f; // Adjust as needed
    private float splashTextureHeight = 200f; // Adjust as needed
    private int lastEnemyType = 0;
    private String enemyName;
    private int enemyHealth;
    private int enemySpeed;
    private Music boss;
    private Music opening;
    private Music shootsum;
    private String enemyDescription;
    public BulletHellScreen(final Main game) {
        Random random = new Random();

        this.game = game;
        timeSinceLastShot = 0;
        camera = new OrthographicCamera();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Dynamically adjust camera
        batch = new SpriteBatch();
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);  // 50x50 white square
        pixmap.fillRectangle(0, 0, 30, 30);
        pixmap.setColor(1,1,1, 0.2f);
        pixmap.fill();
        transparent = new Texture(pixmap);
        heart = new Texture(Gdx.files.internal("heart.png"));
        pixmap.dispose();
        stage = new Stage(new ExtendViewport(camera.viewportWidth, camera.viewportHeight));
        player = new BulletPlayer(240f, 150f);
        player.setFireRate(1.5f);
        enemies = new Array<BulletEnemy>();
        symbols = new Array<ChemicalSymbol>();
        bullets = new Array<Bullet>();
        activeParticles = new Array<ParticleEffect>();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("text.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 35;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        times = 0;
        generator.dispose();
        collectedSymbols = new StringBuilder();
        stunTimer = 0;
        powerUps = new ArrayList<String>();
        enemiesToRemove = new Array<BulletEnemy>();
        symbolsToRemove = new Array<ChemicalSymbol>();
        bulletsToRemove = new Array<Bullet>();
        gameStartTime = TimeUtils.millis();
        damageIndicators = new Array<BulletDamage>();
        indicatorsToRemove = new Array<BulletDamage>();
        enemyBullets = new Array<EnemyBullet>();
        enemyBulletsToRemove = new Array<EnemyBullet>();


        // Initialize object pools for reuse
        enemyBulletPool = new Pool<EnemyBullet>() {
            @Override
            protected EnemyBullet newObject() {
                return new EnemyBullet(50, 100);
            }
        };
        enemyPool = new Pool<BulletEnemy>() {
            @Override
            protected BulletEnemy newObject() {
                return new BulletEnemy(MathUtils.random(0, 480), 800, difficultyMultiplier, 1);
            }
        };

        symbolPool = new Pool<ChemicalSymbol>() {
            @Override
            protected ChemicalSymbol newObject() {
                return new ChemicalSymbol("", MathUtils.random(25, 465), 725);
            }
        };

        bulletPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet(player.getBounds().x, player.getBounds().y, 50);
            }
        };

        particlePool = new Pool<ParticleEffect>() {
            @Override
            protected ParticleEffect newObject() {
                ParticleEffect effect = new ParticleEffect();
                return effect;
            }
        };

        Pixmap pausePixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pausePixmap.setColor(Color.WHITE);
        pausePixmap.fillRectangle(0, 0, 64, 64);
        pausePixmap.setColor(Color.BLACK);
        pausePixmap.fillRectangle(16, 8, 12, 48);
        pausePixmap.fillRectangle(36, 8, 12, 48);
        pauseButtonTexture = new Texture(pausePixmap);
        pausePixmap.dispose();
        Pixmap highlightPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        highlightPixmap.setColor(1, 1, 1, 0.3f);  // White with 30% opacity
        highlightPixmap.fill();
        inputHighlight = new Texture(highlightPixmap);
        highlightPixmap.dispose();
        healTimer = 100 + random.nextInt(101);

        blackbg = new Texture(Gdx.files.internal("black_blue_box.png"));


        enemySpawnTimer = 0;
        symbolSpawnTimer = 0;
        difficultyIncreaseTimer = 0;
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Load background
        dead = Gdx.audio.newMusic(Gdx.files.internal("sfx/damage.wav"));
        hit = Gdx.audio.newMusic(Gdx.files.internal("sfx/hit.mp3"));
        scout = Gdx.audio.newMusic(Gdx.files.internal("sfx/scout.mp3"));
        select = Gdx.audio.newMusic(Gdx.files.internal("sfx/select.wav"));
        getChemical = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect1.wav"));
        setChemical = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect3.wav"));
        wrongChemical = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitwrong.wav"));
        shoot = Gdx.audio.newMusic(Gdx.files.internal("sfx/single.wav"));
        shootsum = Gdx.audio.newMusic(Gdx.files.internal("sfx/fastfirerate.wav"));
        opening = Gdx.audio.newMusic(Gdx.files.internal("sfx/opening.wav"));
        boss = Gdx.audio.newMusic(Gdx.files.internal("sfx/boss.wav"));
        pauseButtonPosition = new Vector2(camera.viewportWidth - PAUSE_BUTTON_SIZE - 10, camera.viewportHeight - PAUSE_BUTTON_SIZE - 10);
        bgmusic = Gdx.audio.newMusic(Gdx.files.internal("sfx/bulletbg.mp3"));
        bgmusic.setLooping(true);
        bgmusic.play();
        mainbg =  new Texture(Gdx.files.internal("backgroundShooters.png"));
        Image background = new Image(mainbg);
        background.setScaling(Scaling.stretch);
        background.setFillParent(true);
        root.addActor(background);
        // Create other UI elements

        activePowerUps = new HashMap<>();
        shapeRenderer = new ShapeRenderer();
        screenShake = new BulletShake();
        Pixmap sa= new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        sa.setColor(1, 0, 0, 0.4f);  // White with 30% opacity
        sa.fill();
        red = new Texture(sa);
        sa.dispose();
        resetBossSpawnTimer();
        isBossActive = false;
    }
    private void resetBossSpawnTimer() {
        bossSpawnTimer = 0;
        nextBossSpawnTime = MathUtils.random(BOSS_SPAWN_INTERVAL_MIN, BOSS_SPAWN_INTERVAL_MAX);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }
    private void updateDifficulty(float delta) {
        if (difficultyIncreaseTimer >= DIFFICULTY_INCREASE_INTERVAL) {
            difficultyMultiplier += 0.1f;
            difficultyIncreaseTimer = 0;
        }
    }

    private void handleEnemySpawning() {
        if (!isBossActive && enemySpawnTimer >= ENEMY_SPAWN_INTERVAL / difficultyMultiplier) {
            spawnEnemy();
            enemySpawnTimer = 0;
        }
    }

    private void handleSymbolSpawning() {
        if(difficultyMultiplier - 4 == 2){
            if (symbolSpawnTimer >= SYMBOL_SPAWN_INTERVAL / (difficultyMultiplier -4) ) {
                spawnSymbol();
                symbolSpawnTimer = 0;
            }
        }else{
            if (symbolSpawnTimer >= SYMBOL_SPAWN_INTERVAL) {
                spawnSymbol();
                symbolSpawnTimer = 0;
            }
        }

    }
    private void handleBossSpawning(float delta) {
        if (!isBossActive && bossSpawnTimer >= nextBossSpawnTime) {
            spawnBoss();
            isBossActive = true;
            bossSpawnTimer = 0;
        }
    }
    private void spawnBoss() {
        currentBoss = new BulletBoss(MathUtils.random(300), 600, difficultyMultiplier);
        isBossEntering = true;
        bossEntranceTimer = 0f;
        screenShake.shake(5f, 5f); // Shake the screen when the boss spawns
    }
    private void updateBoss(float delta) {
        if (currentBoss != null) {
            if (isBossEntering) {
                bossEntranceTimer += delta;
                if (bossEntranceTimer >= BOSS_ENTRANCE_DURATION + BOSS_PAUSE_DURATION) {
                    isBossEntering = false;
                }
            } else {
                currentBoss.update(delta);
                if (currentBoss.canShoot()) {
                    spawnBossBullet();
                    currentBoss.resetShootTimer(); // Reset the boss's shoot timer
                }
            }
        }
    }

    private void spawnBossBullet() {
        EnemyBullet bullet = enemyBulletPool.obtain();
        for(int i = 0; i <5; i++){
            bullet.init(MathUtils.random(100,300), currentBoss.getY());
            enemyBullets.add(bullet);
        }
    }
    private void updateTimers(float delta) {
        enemySpawnTimer += delta;
        symbolSpawnTimer += delta;
        difficultyIncreaseTimer += delta;
        times += delta;
        if (!isBossActive) {
            bossSpawnTimer += delta;
        }
    }
    private void drawHearts(SpriteBatch batch){
        for (int i = 0; i < player.getHealth(); i++){
            batch.draw(heart, 40 * (i%4) + 10, (camera.viewportHeight - 50) - (50 * (i/4)) , 25, 25);
        }
    }
    private void updatePowerUps(float delta) {
        Iterator<Map.Entry<String, Float>> iterator = activePowerUps.entrySet().iterator();
        String mostRecentPowerUp = "";
        float mostRecentTime = Float.MAX_VALUE;

        while (iterator.hasNext()) {
            Map.Entry<String, Float> entry = iterator.next();
            float remainingTime = entry.getValue() - delta;
            if (remainingTime <= 0) {
                removePowerUp(entry.getKey());
                iterator.remove();
            } else {
                entry.setValue(remainingTime);
                if (remainingTime < mostRecentTime) {
                    mostRecentTime = remainingTime;
                    mostRecentPowerUp = entry.getKey();
                }
            }
        }

        currentPowerUp = mostRecentPowerUp;  // Update the current power-up
    }


    private void drawWrappedText(SpriteBatch batch, BitmapFont font, String text, float x, float y) {
        float textWidth = font.draw(batch, text, 0, 0).width;
        float screenWidth = camera.viewportWidth;

        if (x < 0) {
            // Draw on the right side of the screen
            font.draw(batch, text, screenWidth + x, y);
            // Draw the beginning on the left side if it's still visible
            if (x + textWidth > 0) {
                font.draw(batch, text, x, y);
            }
        } else if (x + textWidth > screenWidth) {
            // Draw on the left side of the screen
            font.draw(batch, text, x - screenWidth, y);
            // Draw the end on the right side if it's still visible
            if (x < screenWidth) {
                font.draw(batch, text, x, y);
            }
        } else {
            // Normal drawing when not at the edge
            font.draw(batch, text, x, y);
        }
    }
    @Override
    public void render(float delta) {
        if (!isPaused) {
            updateTimers(delta);
            updateDifficulty(delta);
            updatePowerUps(delta);

            if (!isBossActive) {
                handleEnemySpawning();
            }
            handleSymbolSpawning();
            handleBossSpawning(delta);

            updateEnemies(delta);
            updateSymbols(delta);
            updateBullets(delta);
            updateDamageIndicators(delta);
            updateEnemyBullets(delta);
            if (isBossActive) {
                updateBoss(delta);
            }
            removeOffscreenObjects();
            cleanupObjects();
            updateParticles(delta);
            handleStun(delta);

            handleInput(delta);
            player.update(delta);
            handlePlayerFiring(delta);
            checkCollisions();
        }
        player.updateGlow(delta);
        Gdx.gl.glClearColor(70/255f,130/255f,180/255f, 0.4f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        screenShake.update(delta, camera);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (isBossActive && currentBoss != null) {
            if (isBossEntering) {
                float progress = Math.min(bossEntranceTimer / BOSS_ENTRANCE_DURATION, 1f);
                float y = camera.viewportHeight + 100 - (progress * (camera.viewportHeight -300));
                currentBoss.setPosition(currentBoss.getX(), y);
                currentBoss.draw(batch);
            } else {
                currentBoss.draw(batch);
            }
        }
        if(!isBossActive){
            drawSplash(batch, delta);
        }
        float controlZoneHeight = camera.viewportHeight / 8;
        float halfWidth = camera.viewportWidth / 2;
        batch.draw(blackbg, 0, 0, camera.viewportWidth, camera.viewportHeight / 5);
        for (BulletEnemy enemy : enemies) enemy.draw(batch);
        if (isInitialState) {
            // Draw both left and right highlight areas
            batch.draw(inputHighlight, 0, 0, halfWidth, controlZoneHeight);
            batch.draw(inputHighlight, halfWidth, 0, halfWidth, controlZoneHeight);
        } else if (!isTouchingLeft) {
            // Draw only left highlight area
            batch.draw(inputHighlight, 0, 0, halfWidth, controlZoneHeight);
        } else if (!isTouchingRight) {
            // Draw only right highlight area
            batch.draw(inputHighlight, halfWidth, 0, halfWidth, controlZoneHeight);
        }
        if(isTouchingRight){

            player.draw(batch, 3);
        }else if(isTouchingLeft){
            player.draw(batch,2);
        }else{
            player.draw(batch, 1);
        }
        for (BulletDamage indicator : damageIndicators) {
            indicator.draw(batch, font);
        }
        font.setColor(Color.WHITE);
        font.draw(batch, String.valueOf((int)times), camera.viewportWidth/2 - 15, camera.viewportHeight -20);
        drawWrappedText(batch, font, collectedSymbols.toString(), player.getX() + 75, player.getY() + 50);
        drawHearts(batch);
        for (int i = 0; i < powerUps.size(); i++) {
            String powerUp = powerUps.get(i);
            float remainingTime = activePowerUps.get(powerUp);
            font.draw(batch, powerUp + " (" + (int)remainingTime + "s)", camera.viewportWidth/5 * (i + 1), camera.viewportHeight/6);
        }


        for (ChemicalSymbol symbol : symbols) symbol.draw(batch);
        for (Bullet bullet : bullets) bullet.draw(batch);
        for (EnemyBullet bullet : enemyBullets) {
            bullet.draw(batch);
        }
        for (ParticleEffect particle : activeParticles) particle.draw(batch);


        // Draw pause button
        batch.draw(pauseButtonTexture, pauseButtonPosition.x, pauseButtonPosition.y, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);
        if (isPaused) {
            batch.draw(transparent, 0, 0, camera.viewportWidth, camera.viewportHeight);
            font.setColor(Color.WHITE);
            font.draw(batch, "PAUSED", camera.viewportWidth / 2 - 50, camera.viewportHeight/2 + 300);
            font.draw(batch, "Tap anywhere to continue", camera.viewportWidth / 2 - 120, camera.viewportHeight/2 + 250);
            font.draw(batch, "CHEMICAL CONCOCTIONS", camera.viewportWidth / 2 - 120, camera.viewportHeight / 2 + 100);
            font.draw(batch, "H + H  = ++fireRate", camera.viewportWidth / 2 - 105, camera.viewportHeight / 2  + 60);
            font.draw(batch, "H + O  = --moveSpeed", camera.viewportWidth / 2 - 105, camera.viewportHeight / 2  + 20);
            font.draw(batch, "H + Cl = +damage", camera.viewportWidth / 2 - 105, camera.viewportHeight / 2 -20);
            font.draw(batch, "Na + O = ++hp", camera.viewportWidth / 2 - 105, camera.viewportHeight / 2 - 60);
            font.draw(batch, "Na + H = ++moveSpeed", camera.viewportWidth / 2 - 105, camera.viewportHeight / 2 - 100);
            font.draw(batch, "Na + Cl = 2xdamage", camera.viewportWidth / 2 - 105, camera.viewportHeight / 2 - 140);

        }

        if(screenShake.isShaking()){
            batch.draw(red, 0, 0, camera.viewportWidth, camera.viewportHeight);
        }
        if(times % healTimer == 0){
            player.heal();
        }
        batch.end();
        cleanupDamageIndicators();

        // Handle pause button touch
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (isPaused) {
                isPaused = false;
                select.play();
            } else if (touchPos.x >= pauseButtonPosition.x && touchPos.x <= pauseButtonPosition.x + PAUSE_BUTTON_SIZE &&
                touchPos.y >= pauseButtonPosition.y && touchPos.y <= pauseButtonPosition.y + PAUSE_BUTTON_SIZE) {
                isPaused = true;
                select.play();
            }
        }


    }
    public void drawSplash(SpriteBatch batch, float delta) {
        if (showSplash) {
            float splashX = camera.viewportWidth / 2 - splashTextureWidth / 2;
            float splashY = camera.viewportHeight / 2 - splashTextureHeight / 2;

            float animationDuration = 4f;
            float hoverDuration = 3f;
            float exitDuration = 1f;
            float fadeDuration = 0.5f;
            float fadeOutDuration = exitDuration;
            float elapsedTime = splashTime;

            float fadeInAlpha = MathUtils.clamp(elapsedTime / fadeDuration, 0f, 0.5f);

            float fadeOutAlpha = 1f;
            if (elapsedTime > hoverDuration) {
                float exitElapsed = elapsedTime - hoverDuration;
                fadeOutAlpha = MathUtils.clamp(1 - (exitElapsed / exitDuration), 0f, 1f);
            }

            batch.setColor(0f, 0f, 0f, fadeInAlpha * fadeOutAlpha);
            batch.draw(splashRectangleTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);

            batch.setColor(1f, 1f, 1f, 1f);

            if (elapsedTime <= hoverDuration) {
                float hoverOffset = (float) Math.sin((elapsedTime / hoverDuration) * Math.PI) * 20;
                batch.draw(splashTexture, splashX, splashY + hoverOffset, splashTextureWidth, splashTextureHeight);
                opening.play();
                // Draw enemy stats
                font.draw(batch, "New Enemy Encountered!", camera.viewportWidth / 2 - 100, camera.viewportHeight - 200);
                font.draw(batch, enemyName, splashX + splashTextureWidth + 20, splashY + splashTextureHeight);
                font.draw(batch, "Health: " + enemyHealth, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - 30);
                font.draw(batch, "Speed: " + enemySpeed, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - 60);
                font.draw(batch, enemyDescription, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - 90, 200, 1, true);
            } else if (elapsedTime <= animationDuration) {
                float exitElapsed = elapsedTime - hoverDuration;
                float exitOffset = (exitElapsed / exitDuration) * (camera.viewportHeight / 2);
                batch.draw(splashTexture, splashX, splashY - exitOffset, splashTextureWidth, splashTextureHeight);

                // Draw enemy stats with exit animation
                font.draw(batch, "New Enemy Encountered!", camera.viewportWidth / 2 - 100, camera.viewportHeight - 50 - exitOffset);
                font.draw(batch, enemyName, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - exitOffset);
                font.draw(batch, "Health: " + enemyHealth, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - 30 - exitOffset);
                font.draw(batch, "Speed: " + enemySpeed, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - 60 - exitOffset);
                font.draw(batch, enemyDescription, splashX + splashTextureWidth + 20, splashY + splashTextureHeight - 90 - exitOffset, 200, 1, true);
            } else {
                showSplash = false;
            }

            splashTime += delta;

            batch.setColor(1f, 1f, 1f, 1f);
        }
    }
    private void removePowerUp(String combo) {
        switch (combo) {
            case "HH":
                player.setFireRate(player.getFireRate() * 2f);
                break;
            case "HCl":
            case "ClH":
                player.setBulletDamage(player.getBulletDamage() / 3f);
                break;
            case "HO":
            case "OH":
                player.setSpeed(player.getSpeed() / 1.5f);
                break;
            case "HNa":
            case "NaH":
                player.setSpeed(player.getSpeed() / 2f);
                break;
            case "NaCl":
            case "ClNa":
                player.setBulletDamage(player.getBulletDamage() / 5f);
                break;
            case "NaO":
            case "ONa":
                player.setInvincible(false);
                break;
        }
        powerUps.remove(combo);
    }
    private void updateParticles(float delta) {
        for (int i = activeParticles.size - 1; i >= 0; i--) {
            ParticleEffect particle = activeParticles.get(i);
            particle.update(delta);

            // Check if the particle has been playing for more than PARTICLE_DURATION
            if (particle.getEmitters().first().getDuration().getLowMax() > PARTICLE_DURATION) {
                particle.allowCompletion(); // Allow the particle effect to complete
                if (particle.isComplete()) {
                    activeParticles.removeIndex(i);
                    particlePool.free(particle); // Return the particle to the pool
                }
            }
        }
    }
    private void updateEnemyBullets(float delta) {
        for (EnemyBullet bullet : enemyBullets) {
            bullet.update(delta);
            if (bullet.getY() < 220) {
                enemyBulletsToRemove.add(bullet);
            }
        }

        for (BulletEnemy enemy : enemies) {
            if(enemy.canShoot() && enemy.getType() == 1 || enemy.getType() == 5){
                EnemyBullet bullet = enemyBulletPool.obtain();
                bullet.init(enemy.getX() + enemy.getWidth() / 2, enemy.getY());
                enemyBullets.add(bullet);
                enemy.resetShootTimer();
            }
        }
    }
    private void handleStun(float delta) {
        if (stunTimer > 0) {
            stunTimer -= delta;
            if (stunTimer <= 0) {
                player.setStunned(false);
            }
        }
    }
    private void handlePlayerFiring(float delta) {
        timeSinceLastShot += delta;
        if (timeSinceLastShot >= player.getFireRate()) {
            spawnPlayerBullet();
            if(powerUps.contains("HH")){
                shootsum.play();
            }else {
                shoot.play();
            }
            timeSinceLastShot = 0;
        }
    }
    private void updateDamageIndicators(float delta) {
        for (BulletDamage indicator : damageIndicators) {
            indicator.update(delta);
            if (indicator.isExpired()) {
                indicatorsToRemove.add(indicator);
            }
        }
    }

    private void cleanupDamageIndicators() {
        for (BulletDamage indicator : indicatorsToRemove) {
            damageIndicators.removeValue(indicator, true);
        }
        indicatorsToRemove.clear();
    }
    private void spawnPlayerBullet() {
        Bullet bullet = bulletPool.obtain();
        bullet.setPosition(player.getX() + player.getWidth() / 2 - bullet.getWidth() / 2,
            player.getY() + player.getHeight());
        bullet.setTexture(currentPowerUp);
        bullets.add(bullet);
    }
    @Override
    public void resize(int width, int height) {
        float aspectRatio = 9f / 16f;
        viewportWidth = 480; // Base width for scaling
        viewportHeight = viewportWidth / aspectRatio;
        camera.setToOrtho(false, viewportWidth, viewportHeight);
        camera.update();
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

    private void spawnEnemy() {
        BulletEnemy enemy = enemyPool.obtain();
        int enemytype = 0;
        if(times > 0 & times < 30){
            enemytype = 1;
            enemyName = "DEFAULT";
            enemyHealth = 50;
            enemySpeed = 140;
            enemyDescription = "";
        }
        else if(times > 30 & times < 60){
            enemytype = MathUtils.random(1, 2);
            enemyName = "FAST BOI";
            enemyHealth = 25;
            enemySpeed = 230;
            enemyDescription = "";
        }
        else if(times > 60& times < 90){
            enemytype = MathUtils.random(1, 3);
            enemyName = "TANK";
            enemyHealth = 150;
            enemySpeed = 70;
            enemyDescription = "";
        }
        else if(times > 90& times < 120){
            enemytype = MathUtils.random(1, 4);
            enemyName = "SHOOTAH";
            enemyHealth = 50;
            enemySpeed = 140;
            enemyDescription = "CAN SHOOT";

        }
        else if(times > 120 & times < 180){
            enemytype =MathUtils.random(1, 5);
            enemyName = "SHOOTAH'S FRIEND";
            enemyHealth = 50;
            enemySpeed = 140;
            enemyDescription = "CAN SHOOT";
        }else if(times > 180 & times < 210){
            enemytype =MathUtils.random(1, 6);
            enemyName = "WATER BOTTLE";
            enemyHealth = 50;
            enemySpeed = 140;
            enemyDescription = "N/A";
        }

        if (enemytype > lastEnemyType) {
            lastEnemyType = enemytype;
            showEnemySplash(enemytype);
        }

        enemy.init(MathUtils.random(40, 445), 725, difficultyMultiplier, enemytype);
        enemies.add(enemy);
    }
    private void showEnemySplash(int enemyType) {
        showSplash = true;
        splashTime = 0f;
        // Load the appropriate texture for the enemy type
        splashTexture = new Texture(Gdx.files.internal("enemy/enemy" + enemyType + ".png"));
        splashRectangleTexture =  new Texture(Gdx.files.internal("enemy/enemy" + enemyType + ".png"));
    }
    private void spawnSymbol() {
        float random = MathUtils.random(1f);
        String symbolType = "";

        if (random <= 0.5f) {
            symbolType = "H";
        } else if (random <= 0.6f) {
            symbolType = "Cl";
        } else if (random <= 0.72f) {
            symbolType = "O";
        } else if (random <= 0.9f) {
            symbolType = "Na";
        }

        if (!symbolType.isEmpty()) {
            ChemicalSymbol symbol = symbolPool.obtain();
            symbol.init(MathUtils.random(70, 405), 725);
            symbol.setSymbol(symbolType);
            symbols.add(symbol);
        }
    }

    private void updateEnemies(float delta) {
        for (BulletEnemy enemy : enemies) {
            enemy.update(delta);
        }
    }

    private void updateSymbols(float delta) {
        for (ChemicalSymbol symbol : symbols) {
            symbol.update(delta);
        }
    }

    private void updateBullets(float delta) {
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
    }

    private void checkCollisions() {
        for (ChemicalSymbol symbol : symbols) {
            if (player.getBounds().overlaps(symbol.getBounds())) {
                collectedSymbols.append(symbol.getSymbol());
                checkCombination();

                symbolsToRemove.add(symbol);
            }
        }

        for (Bullet bullet : bullets) {
            for (BulletEnemy enemy : enemies) {
                if (bullet.getBounds().overlaps(enemy.getBounds())) {
                    enemy.hit(player.getBulletDamage());

                    spawnParticleEffect(enemy.getX(), enemy.getY(), SCOUT_PARTICLE);
                    bulletsToRemove.add(bullet);

                    if (enemy.getHealth() <= 0) {
                        hit.play();
                        spawnParticleEffect(enemy.getX(), enemy.getY(), FLAME_PARTICLE);
                        enemiesToRemove.add(enemy);
                    }else{
                        scout.play();
                    }
                    break;
                }
            }
        }
        enemyBulletsToRemove.clear(); // Clear the removal list before checking collisions

        for (EnemyBullet bullet : enemyBullets) {
            if (player.getBounds().overlaps(bullet.getBounds())) {
                if (!player.getInvincible()) {
                    player.setHealth(player.getHealth() - 1);
                    screenShake.shake(0.5f, 5f);
                    dead.play();
                } else {
                    player.startGlowing();
                }
                enemyBulletsToRemove.add(bullet);

                if (player.getHealth() <= 0) {
                    dispose();
                    game.setScreen(new GameEndScreen(game));
                    return; // Exit the method to prevent further processing
                }
            }
        }

        if (isBossActive && currentBoss != null && !isBossEntering) {
            for (Bullet bullet : bullets) {
                if (bullet.getBounds().overlaps(currentBoss.getBounds())) {
                    currentBoss.hit(player.getBulletDamage());
                    bulletsToRemove.add(bullet);

                    if (currentBoss.getHealth() <= 0) {
                        hit.play();
                        spawnParticleEffect(currentBoss.getX(), currentBoss.getY(), FLAME_PARTICLE);
                        isBossActive = false;
                        currentBoss = null;
                        resetBossSpawnTimer();
                    } else {
                        scout.play();
                    }
                    break;
                }
            }

            if (currentBoss != null && player.getBounds().overlaps(currentBoss.getBounds())) {
                if (player.getInvincible()) {
                    player.startGlowing();
                } else {
                    player.setHealth(player.getHealth() - 1); // Boss deals more damage
                    screenShake.shake(0.5f, 5f);
                    dead.play();
                }

                if (player.getHealth() <= 0) {
                    dispose();
                    game.setScreen(new GameEndScreen(game));
                    return;
                }
            }
        }
        enemyBullets.removeAll(enemyBulletsToRemove, true);
        for (BulletEnemy enemy : enemies){
            if(player.getBounds().overlaps(enemy.getBounds())){
                enemiesToRemove.add(enemy);
                if(player.getHealth() <= 0){
                    dispose();
                    game.setScreen(new MainMenuScreen(game));
                }
                if(player.getInvincible()){
                    player.startGlowing();
                }else{
                    player.setHealth(player.getHealth() - 1);
                    screenShake.shake(0.5f, 5f);
                    dead.play();
                }

                return;
            }
        }

    }
    private void spawnParticleEffect(float x, float y, String particleType) {
        ParticleEffect particle = particlePool.obtain();
        particle.load(Gdx.files.internal("sfx/" + particleType), Gdx.files.internal("sfx"));
        particle.setPosition(x, y);
        particle.reset();
        particle.start();
        activeParticles.add(particle);
    }
    private void applyPowerUp(String combo) {
        switch (combo) {
            case "HH":
                player.setFireRate(player.getFireRate() / 2f); // Much faster fire rate
                break;
            case "HCl":
            case "ClH":
                player.setBulletDamage(player.getBulletDamage() * 3f); // Triple damage
                break;
            case "HO":
            case "OH":
                player.setSpeed(player.getSpeed() * 1.5f); // 50% speed increase
                break;
            case "HNa":
            case "NaH":
                player.setSpeed(player.getSpeed() * 2f); // Double speed
                break;
            case "NaCl":
            case "ClNa":
                player.setBulletDamage(player.getBulletDamage() * 5f); // 5x damage
                break;
            case "NaO":
            case "ONa":
                player.setInvincible(true); // Temporary invincibility
                break;
        }
        activePowerUps.put(combo, POWER_UP_DURATION);
        powerUps.add(combo);
    }
    private void checkCombination() {
        if (collectedSymbols.length() >= 2 && !collectedSymbols.toString().equals("Cl") && !collectedSymbols.toString().equals("Na")) {
            String combo = collectedSymbols.toString();
            boolean validCombo = false;

            if (activePowerUps.containsKey(combo)) {
                // Refresh the duration if the power-up is already active
                activePowerUps.put(combo, POWER_UP_DURATION);
                validCombo = true;
            } else {
                switch (combo) {
                    case "HH":
                    case "HCl":
                    case "ClH":
                    case "HO":
                    case "OH":
                    case "HNa":
                    case "NaH":
                    case "NaCl":
                    case "ClNa":
                    case "NaO":
                    case "ONa":
                        applyPowerUp(combo);
                        validCombo = true;
                        break;
                }
            }

            if (!validCombo) {
                wrongChemical.play();
                screenShake.shake(0.5f, 3f);
                player.setStunned(true);
                stunTimer = STUN_DURATION;
                damageIndicators.add(new BulletDamage(
                    player.getX() + player.getWidth() / 2,
                    player.getY() + player.getHeight(),
                    -1
                ));
            } else {
                damageIndicators.add(new BulletDamage(
                    player.getX() + player.getWidth() / 2,
                    player.getY() + player.getHeight(),
                    +1
                ));
                setChemical.play();
            }
            collectedSymbols.setLength(0); // Reset collected symbols
        } else {
            getChemical.play();
        }
    }
    private void removeOffscreenObjects() {
        for (BulletEnemy enemy : enemies) {
            if (enemy.getY() + enemy.getHeight() < 220) {
                enemiesToRemove.add(enemy);
            }
        }

        for (ChemicalSymbol symbol : symbols) {
            if (symbol.getY() + symbol.getHeight() < 220) {
                symbolsToRemove.add(symbol);
            }
        }

        for (Bullet bullet : bullets) {
            if (bullet.getY() > 775) {
                bulletsToRemove.add(bullet);
            }
        }
        for (EnemyBullet bullet : enemyBullets){
            if (bullet.getY() > 220) {
                enemyBulletsToRemove.add(bullet);
            }
        }
    }
    private void cleanupObjects() {
        for (BulletEnemy enemy : enemiesToRemove) {
            enemies.removeValue(enemy, true);
            enemyPool.free(enemy);
        }
        enemiesToRemove.clear();

        for (ChemicalSymbol symbol : symbolsToRemove) {
            symbols.removeValue(symbol, true);
            symbolPool.free(symbol);
        }
        symbolsToRemove.clear();

        for (Bullet bullet : bulletsToRemove) {
            bullets.removeValue(bullet, true);
            bulletPool.free(bullet);
        }
        bulletsToRemove.clear();
        for (EnemyBullet bullet : enemyBulletsToRemove) {
            enemyBullets.removeValue(bullet, true);
            enemyBulletPool.free(bullet);
        }
        enemyBulletsToRemove.clear();
    }
    private void handleInput(float delta) {
        boolean isMovingLeft = false;
        boolean isMovingRight = false;
        isInitialState = false;

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            float controlZoneHeight = camera.viewportHeight / 8;
            if (touchPos.y < controlZoneHeight) {
                if (touchPos.x < camera.viewportWidth / 2) {
                    isMovingLeft = true;
                } else {
                    isMovingRight = true;
                }
            }
        } else {
            isInitialState = true;
        }

        // Handle movement
        if (isMovingLeft && !isMovingRight) {
            player.moveLeft(delta);
        } else if (isMovingRight && !isMovingLeft) {
            player.moveRight(delta);
        } else {
            player.stopMoving();
        }

        // Update touch indicators
        isTouchingLeft = isMovingLeft;
        isTouchingRight = isMovingRight;

        // Update player
        player.update(delta);
    }
    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        font.dispose();
        damageIndicators.clear();
        indicatorsToRemove.clear();
        for (ParticleEffect particle : activeParticles) {
            particle.dispose();
        }
        for (BulletEnemy enemy : enemies) enemy.dispose();
        for (ChemicalSymbol symbol : symbols) symbol.dispose();
        for (Bullet bullet : bullets) bullet.dispose();
        enemyPool.clear();
        symbolPool.clear();
        powerUps.clear();
        bulletPool.clear();
        inputHighlight.dispose();
        hit.dispose();
        blackbg.dispose();
        transparent.dispose();
        heart.dispose();
        enemiesToRemove.clear();
        scout.dispose();
        hit.dispose();
        getChemical.dispose();
        setChemical.dispose();
        wrongChemical.dispose();
        shoot.dispose();
        bgmusic.dispose();
        shapeRenderer.dispose();
        mainbg.dispose();
        screenShake.dispose();
        dead.dispose();
        select.dispose();
        enemyBulletPool.clear();
        for (EnemyBullet bullet : enemyBullets) {
            bullet.dispose(); // Make sure to implement a dispose method in EnemyBullet if needed
        }
        if (currentBoss != null) {
            currentBoss.dispose();
        }
        if (splashTexture != null) {
            splashTexture.dispose();
        }
        if (splashRectangleTexture != null) {
            splashRectangleTexture.dispose();
        }
        opening.dispose();
        boss.dispose();
        shootsum.dispose();
    }
}
