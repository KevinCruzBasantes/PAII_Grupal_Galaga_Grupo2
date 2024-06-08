package ec.edu.uce.Galaga.models;

import java.awt.*;

public class Opponents implements Drawable, Movable, Shootable {

    int[] cord_x = new int[5];
    int[] cord_y = new int[5];

    public Opponents(int randomX, int randomY) {
        cord_x[0] = randomX;
        cord_x[1] = randomX + 100;
        cord_x[2] = randomX + 100;
        cord_x[3] = randomX + 50;
        cord_x[4] = randomX;

        cord_y[0] = randomY;
        cord_y[1] = randomY;
        cord_y[2] = randomY + 50;
        cord_y[3] = randomY + 25;
        cord_y[4] = randomY + 50;
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(cord_x, cord_y, 5);
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
        return new Bullet(cord_x[3], cord_y[3] + 10, Bullet.Direction.DOWN);
    }

    public boolean checkCollision(Bullet bullet) {
        Rectangle opponentBounds = new Rectangle(cord_x[0], cord_y[0], cord_x[1] - cord_x[0], cord_y[2] - cord_y[0]);
        return opponentBounds.intersects(bullet.getBounds());
    }

    public int getLowestY() {
        int lowestY = cord_y[0];
        for (int y : cord_y) {
            if (y > lowestY) {
                lowestY = y;
            }
        }
        return lowestY;
    }

    public int getX() {
        return cord_x[0];
    }
}
