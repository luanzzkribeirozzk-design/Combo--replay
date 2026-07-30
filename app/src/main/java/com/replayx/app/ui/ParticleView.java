package com.replayx.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.random.Random;

/**
 * ParticleView — View customizada que renderiza partículas animadas no background.
 * Exibe textos "DEV WILL" flutuantes e partículas decorativas.
 * 
 * Package original: com.replayx.app.p005ui.ParticleView
 */
public final class ParticleView extends View {

    private final ArrayList<String> particleTexts;
    private final Paint textPaint;
    private final List<Particle> particles;
    private final Paint particlePaint;
    private boolean isRunning;

    public ParticleView(Context context) {
        this(context, null, 0);
    }

    public ParticleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        particleTexts = new ArrayList<>();
        particleTexts.add("DEV WILL");
        particleTexts.add("BYPASS");
        particleTexts.add("OTIMIZADOR");
        particleTexts.add("SENSI");
        particleTexts.add("FF MAX");
        particleTexts.add("FREE FIRE");

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0x22FFD700); // Semi-transparente dourado
        textPaint.setTextSize(14f);
        textPaint.setTypeface(Typeface.MONOSPACE);

        particles = new ArrayList<>();
        particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        particlePaint.setColor(0x33EAF00); // Verde semi-transparente

        isRunning = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRunning) return;

        int width = getWidth();
        int height = getHeight();

        // Desenhar textos flutuantes
        for (int i = 0; i < particleTexts.size(); i++) {
            String text = particleTexts.get(i);
            float x = (float) (Random.Default.nextInt(width));
            float y = (float) (Random.Default.nextInt(height));
            textPaint.setAlpha(15 + Random.Default.nextInt(25));
            canvas.drawText(text, x, y, textPaint);
        }

        // Desenhar partículas (pontos)
        for (Particle p : particles) {
            particlePaint.setAlpha(p.alpha);
            canvas.drawCircle(p.x, p.y, p.radius, particlePaint);
            updateParticle(p, width, height);
        }

        // Gerar novas partículas aleatoriamente
        if (Random.Default.nextInt(100) < 30 && particles.size() < 30) {
            particles.add(new Particle(
                (float) Random.Default.nextInt(width),
                (float) Random.Default.nextInt(height),
                1f + (float) Random.Default.nextDouble() * 2f,
                10 + Random.Default.nextInt(30)
            ));
        }

        invalidate();
    }

    private void updateParticle(Particle p, int width, int height) {
        p.y -= 0.5f;
        p.x += (float) (Math.sin(p.y * 0.01) * 0.3);
        p.alpha = Math.max(0, p.alpha - 1);

        if (p.y < 0 || p.alpha <= 0) {
            p.y = height;
            p.x = (float) Random.Default.nextInt(width);
            p.alpha = 10 + Random.Default.nextInt(40);
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void start() {
        isRunning = true;
    }

    /**
     * Representa uma partícula visual.
     */
    private static class Particle {
        float x, y;
        float radius;
        int alpha;

        Particle(float x, float y, float radius, int alpha) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.alpha = alpha;
        }
    }
}
