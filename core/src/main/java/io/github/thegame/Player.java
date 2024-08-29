package io.github.thegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Player {
    final GameScreen game;

    public Player(final GameScreen game) {
        this.game = game;
    }

    public void createPlayer() {
        game.player = new Rectangle();
        game.player.x = 480 / 2f - PLAYER_SIZE / 2f; // center the player horizontally
        game.player.y = 20; // bottom left corner of the player is 20 pixels above the bottom screen edge
        game.player.width = PLAYER_SIZE;
        game.player.height = PLAYER_SIZE;
    }

    public void render() {

    }



    public void move() {

        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.camera.unproject(touchPos);
            game.player.x = touchPos.x - 64 / 2;
        }




        // make sure the player stays within the screen bounds
        if(game.player.x < 0) game.player.x = 0;
        if(game.player.x > game.GAME_SCREEN_X - PLAYER_SIZE) game.player.x = game.GAME_SCREEN_X - PLAYER_SIZE;
    }


    public void processSpeed() {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && !game.stunned) {
            speed = PLAYER_SPEED_BOOST;
        }
        else if (game.stunned) {
            speed = PLAYER_SPEED_STUNNED;
        }
        else if (game.slowed) {
            speed = PLAYER_SPEED_SLOWED;
        }
        else {
            speed = PLAYER_SPEED_DEFAULT;
        }
    }


    private static final int PLAYER_SIZE = 32;
    private static final int PLAYER_SPEED_BOOST = 800;
    private static final int PLAYER_SPEED_DEFAULT = 500;
    public final int STUN_DURATION = 3;
    public final int SLOW_DURATION = 3;
    public int PLAYER_SPEED_STUNNED = 0;
    public int PLAYER_SPEED_SLOWED = 20;
    private int speed = PLAYER_SPEED_DEFAULT;


}
