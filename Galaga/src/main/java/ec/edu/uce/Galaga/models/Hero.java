package ec.edu.uce.Galaga.models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Hero implements Drawable, Movable, Shootable {

    public int[] cord_x = {400, 450, 350};
    public int[] cord_y = {500, 550, 550};
    private int lives = 100; // Contador de vidas
    private int health = 100; // Vida del héroe
int damage;
    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.fillPolygon(cord_x, cord_y, 3);
    }

    @Override
    public void moveUp(int variable) {
        for (int i = 0; i < cord_y.length; i++) {
            cord_y[i] = cord_y[i] - variable;
        }
    }

    @Override
    public void moveDown(int variable) {
        for (int i = 0; i < cord_y.length; i++) {
            cord_y[i] = cord_y[i] + variable;
        }
    }

    @Override
    public void moveLeft(int variable) {
        for (int i = 0; i < cord_x.length; i++) {
            cord_x[i] = cord_x[i] - variable;
        }
    }

    @Override
    public void moveRight(int variable) {
        for (int i = 0; i < cord_x.length; i++) {
            cord_x[i] = cord_x[i] + variable;
        }
    }

    @Override
    public void draw(Graphics graphics, Drawable drawable) {
        // Implementación necesaria
    }

    @Override
    public Bullet shoot() {
        // Disparar desde el vértice superior del triángulo
        int topVertexX = cord_x[0];
        int topVertexY = cord_y[0];
        return new Bullet(topVertexX, topVertexY, Bullet.Direction.UP);
    }

    public boolean checkCollision(Bullet bullet) {
        Polygon heroPolygon = new Polygon(cord_x, cord_y, 3);
        return heroPolygon.contains(bullet.getX(), bullet.getY());
    }

    public void decreaseLife(int opponentDamage) {
        if (lives > 0) {
            lives -= opponentDamage; // Reducir las vidas según el daño recibido
            health -= opponentDamage; // Reducir la vida del héroe según el daño recibido
        }
    }

    public int getLives() {
        return lives;
    }

    public int getHealth() {
        return health;
    }
}
