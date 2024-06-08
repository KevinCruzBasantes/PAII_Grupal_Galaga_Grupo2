package ec.edu.uce.Galaga.models;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet implements Drawable, Movable {

    private int x, y;
    private Direction direction;

    public enum Direction {
        UP, DOWN
    }

    public Bullet(int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    @Override
    public void moveUp(int variable) {
        y -= variable;
    }

    @Override
    public void moveDown(int variable) {
        y += variable;
    }

    @Override
    public void moveLeft(int variable) {
        // No implementado
    }

    @Override
    public void moveRight(int variable) {
        // No implementado
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.fillOval(x, y, 10, 10);
    }

    @Override
    public void draw(Graphics graphics, Drawable drawable) {
        // No implementado
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 10, 10);
    }
}