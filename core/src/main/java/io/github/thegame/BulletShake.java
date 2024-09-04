package io.github.thegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class BulletShake {
    private float duration;
    private float currentTime;
    private float intensity;
    private Vector3 originalCameraPosition;
    private boolean isShaking;
    private Color overlayColor;
    private ShapeRenderer shapeRenderer;

    public BulletShake() {
        originalCameraPosition = new Vector3();
        isShaking = false;
        overlayColor = new Color(1, 0, 0, 0.05f); // Red with 5% opacity
        shapeRenderer = new ShapeRenderer();
    }

    public void shake(float duration, float intensity) {
        this.duration = duration;
        this.intensity = intensity;
        currentTime = 0;
        isShaking = true;
    }

    public void update(float delta, OrthographicCamera camera) {
        if (isShaking) {
            if (currentTime == 0) {
                originalCameraPosition.set(camera.position);
            }

            currentTime += delta;
            float progress = currentTime / duration;

            if (progress < 1.0f) {
                float currentIntensity = intensity * (1 - progress);
                float offsetX = MathUtils.random(-1f, 1f) * currentIntensity;
                float offsetY = MathUtils.random(-1f, 1f) * currentIntensity;
                camera.position.set(
                    originalCameraPosition.x + offsetX,
                    originalCameraPosition.y + offsetY,
                    originalCameraPosition.z
                );
            } else {
                isShaking = false;
                camera.position.set(originalCameraPosition);
            }
        }
    }



    public boolean isShaking() {
        return isShaking;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
