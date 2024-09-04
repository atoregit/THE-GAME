package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.List;

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
    private static final String FLAME_PARTICLE = "flamecool.p";
    private static final String SCOUT_PARTICLE = "scout.p";
    public BulletHellScreen(final Main game) {
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
        // Initialize object pools for reuse
        enemyPool = new Pool<BulletEnemy>() {
            @Override
            protected BulletEnemy newObject() {
                return new BulletEnemy(MathUtils.random(0, 480), 800, difficultyMultiplier);
            }
        };

        symbolPool = new Pool<ChemicalSymbol>() {
            @Override
            protected ChemicalSymbol newObject() {
                return new ChemicalSymbol("", MathUtils.random(25, 465), 775);
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

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(37 / 255f, 40 / 255f, 48 / 255f, 1f);
        pix.fillRectangle(0, 0, 1, 1);
        blackbg = new Texture(pix);
        pix.dispose();

        enemySpawnTimer = 0;
        symbolSpawnTimer = 0;
        difficultyIncreaseTimer = 0;
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Load background
        hit = Gdx.audio.newMusic(Gdx.files.internal("sfx/hit.mp3"));
        scout = Gdx.audio.newMusic(Gdx.files.internal("sfx/scout.mp3"));
        getChemical = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect1.wav"));
        setChemical = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitcollect3.wav"));
        wrongChemical = Gdx.audio.newMusic(Gdx.files.internal("sfx/fruitwrong.wav"));
        shoot = Gdx.audio.newMusic(Gdx.files.internal("sfx/shoot.mp3"));
        pauseButtonPosition = new Vector2(camera.viewportWidth - PAUSE_BUTTON_SIZE - 10, camera.viewportHeight - PAUSE_BUTTON_SIZE - 10);
        bgmusic = Gdx.audio.newMusic(Gdx.files.internal("sfx/bulletbg.mp3"));
        bgmusic.setLooping(true);
        bgmusic.play();


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
        if (enemySpawnTimer >= ENEMY_SPAWN_INTERVAL / difficultyMultiplier) {
            spawnEnemy();
            enemySpawnTimer = 0;
        }
    }

    private void handleSymbolSpawning() {
        if (symbolSpawnTimer >= SYMBOL_SPAWN_INTERVAL) {
            spawnSymbol();
            symbolSpawnTimer = 0;
        }
    }
    private void updateTimers(float delta) {
        enemySpawnTimer += delta;
        symbolSpawnTimer += delta;
        difficultyIncreaseTimer += delta;
        times += delta;
    }
    private void drawHearts(SpriteBatch batch){
        for (int i = 0; i < player.getHealth()+1; i++){
            batch.draw(heart, 40 * (i%4) + 10, (camera.viewportHeight - 40) - (40 * ((int)i/4)) , 25, 25);
        }
    }
    @Override
    public void render(float delta) {
        if (!isPaused) {
            updateTimers(delta);
            updateDifficulty(delta);
            handleEnemySpawning();
            handleSymbolSpawning();
            updateEnemies(delta);
            updateSymbols(delta);
            updateBullets(delta);
            updateDamageIndicators(delta);

            removeOffscreenObjects();
            cleanupObjects();
            updateParticles(delta);
            handleStun(delta);

            handleInput(delta);
            player.update(delta);
            handlePlayerFiring(delta);
            checkCollisions();
        }

        Gdx.gl.glClearColor(70/255f,130/255f,180/255f, 0.4f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float controlZoneHeight = camera.viewportHeight / 8;
        float halfWidth = camera.viewportWidth / 2;
        batch.draw(blackbg, 0, 0, camera.viewportWidth, camera.viewportHeight / 5);
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
        player.draw(batch);
        for (BulletDamage indicator : damageIndicators) {
            indicator.draw(batch, font);
        }
        font.setColor(Color.WHITE);
        font.draw(batch, String.valueOf((int)times), camera.viewportWidth/2 - 15, camera.viewportHeight -20);
        font.draw(batch, collectedSymbols.toString(), camera.viewportWidth - 180, camera.viewportHeight - 100);
        drawHearts(batch);
        for (int i = 0; i < powerUps.size(); i++) {
            font.draw(batch, powerUps.get(i), camera.viewportWidth/10 * (i + 1), camera.viewportHeight/6);
        }

        for (BulletEnemy enemy : enemies) enemy.draw(batch);
        for (ChemicalSymbol symbol : symbols) symbol.draw(batch);
        for (Bullet bullet : bullets) bullet.draw(batch);
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

        batch.end();
        cleanupDamageIndicators();

        // Handle pause button touch
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (isPaused) {
                isPaused = false;
            } else if (touchPos.x >= pauseButtonPosition.x && touchPos.x <= pauseButtonPosition.x + PAUSE_BUTTON_SIZE &&
                touchPos.y >= pauseButtonPosition.y && touchPos.y <= pauseButtonPosition.y + PAUSE_BUTTON_SIZE) {
                isPaused = true;
            }
        }


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
            shoot.play();
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
        enemy.init(MathUtils.random(40, 445), 775, difficultyMultiplier); // Initialize with current values
        enemies.add(enemy);
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
            symbol.init(MathUtils.random(70, 405), 775);
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
                    damageIndicators.add(new BulletDamage(
                        enemy.getX() + enemy.getWidth() / 2,
                        enemy.getY() + enemy.getHeight(),
                        -(int)player.getBulletDamage()
                    ));
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
        for (BulletEnemy enemy : enemies){
            if(player.getBounds().overlaps(enemy.getBounds())){
                enemiesToRemove.add(enemy);
                if(player.getHealth() <= 0){
                    dispose();
                    game.setScreen(new MainMenuScreen(game));
                }
                player.setHealth(player.getHealth() - 1);

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
    private void checkCombination() {
        if (collectedSymbols.length() >= 2 && !collectedSymbols.toString().equals("Cl") && !collectedSymbols.toString().equals("Na")) {
            String combo = collectedSymbols.toString();
            boolean validCombo = false;
            switch (combo) {
                case "HH":
                    player.setFireRate(player.getFireRate() / 1.1f);
                    powerUps.add("HH");
                    validCombo = true;
                    break;
                case "HCl":
                case "ClH":
                    powerUps.add("HCl");
                    player.setBulletDamage(player.getBulletDamage() * 1.5f);
                    validCombo = true;
                    break;
                case "HO":
                case "OH":
                    powerUps.add("OH-");
                    player.setSpeed(player.getSpeed() * 0.8f);
                    validCombo = true;
                    break;
                case "HNa":
                case "NaH":
                    powerUps.add("NaH");
                    player.setSpeed(player.getSpeed() * 1.2f);
                    validCombo = true;
                    break;
                case "NaCl":
                case "ClNa":
                    powerUps.add("NaCl");
                    player.setBulletDamage(player.getBulletDamage() * 2f);
                    validCombo = true;
                    break;
                case "NaO":
                case "ONa":
                    powerUps.add("NaO");
                    player.setHealth(player.getHealth() + 1);
                    validCombo = true;
                    break;
            }

            if (!validCombo) {
                wrongChemical.play();
                player.setStunned(true);
                stunTimer = STUN_DURATION;
            }else{
                setChemical.play();
            }
            collectedSymbols.setLength(0); // Reset collected symbols
        }else{
            getChemical.play();
        }
    }
    private void removeOffscreenObjects() {
        for (BulletEnemy enemy : enemies) {
            if (enemy.getY() + enemy.getHeight() < 150) {
                enemiesToRemove.add(enemy);
            }
        }

        for (ChemicalSymbol symbol : symbols) {
            if (symbol.getY() + symbol.getHeight() < 150) {
                symbolsToRemove.add(symbol);
            }
        }

        for (Bullet bullet : bullets) {
            if (bullet.getY() > 775) {
                bulletsToRemove.add(bullet);
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
    }
    private void handleInput(float delta) {
        isTouchingLeft = false;
        isTouchingRight = false;
        isInitialState = false;  // Set to false as soon as we detect any touch

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            float controlZoneHeight = camera.viewportHeight / 8;
            if (touchPos.y < controlZoneHeight) {
                if (touchPos.x < camera.viewportWidth / 2) {
                    player.moveLeft(delta);
                    isTouchingLeft = true;
                } else {
                    player.moveRight(delta);
                    isTouchingRight = true;
                }
            }
        } else {
            isInitialState = true;  // Reset to initial state when not touching
        }
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
    }
}
