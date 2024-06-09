package ec.edu.uce.Galaga.models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class SuperOpponent extends Opponents {

    private int health = 100;
    private long lastShootTime = 0;
    private static final long SHOOT_INTERVAL = 4000; // 4 segundos
    private int lives = 3;

    public SuperOpponent(int x, int y) {
        super(x, y);
        this.lives = 3; // SuperOpponent también tiene vidas, se pueden usar para otras mecánicas si se desea
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.fillPolygon(cord_x, cord_y, 5);

        // Dibujar barra de vida
        graphics.setColor(Color.GREEN);
        graphics.fillRect(cord_x[0], cord_y[0] - 10, health, 5);
    }

    @Override
    public Bullet shoot() {
        return new Bullet(cord_x[3], cord_y[3] + 10, Bullet.Direction.DOWN);
    }

    public boolean canShoot(long currentTime) {
        return currentTime - lastShootTime >= SHOOT_INTERVAL;
    }

    public void resetShootTime(long currentTime) {
        lastShootTime = currentTime;
    }

    public void decreaseHealth(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public int getHealth() {
        return health;
    }

    // Método para obtener la coordenada X de un punto específico
    public int getCordX(int index) {
        return cord_x[index];
    }

    // Método para obtener la coordenada Y de un punto específico
    public int getCordY(int index) {
        return cord_y[index];
    }
}