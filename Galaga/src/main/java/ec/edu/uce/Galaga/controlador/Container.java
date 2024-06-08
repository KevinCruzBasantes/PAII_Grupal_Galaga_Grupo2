package ec.edu.uce.Galaga.controlador;

import ec.edu.uce.Galaga.models.Bullet;
import ec.edu.uce.Galaga.models.Hero;
import ec.edu.uce.Galaga.models.Line;
import ec.edu.uce.Galaga.models.Opponents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Container {

    final int SCREEN_WIDTH = 700;
    final int SCREEN_HEIGHT = 600; // Cambiar para coincidir con el tamaño del frame
    final int OPPONENT_SIZE = 100; // Tamaño aproximado del enemigo para evitar superposición
    Hero hero = new Hero();
    List<Opponents> opponents = new ArrayList<>();
    List<Bullet> heroBullets = new ArrayList<>();
    List<Bullet> opponentBullets = new ArrayList<>();
    Random random = new Random();
    Line line = new Line();
    boolean gameOver = false;

    public Container() {
        for (int i = 0; i < 5; i++) {
            addOpponent();
        }
    }

    private void addOpponent() {
        boolean positionFree;
        int randomX, fixedY = 0; // En el borde superior
        do {
            positionFree = true;
            randomX = random.nextInt(SCREEN_WIDTH);

            for (Opponents opponent : opponents) {
                if (Math.abs(opponent.getX() - randomX) < OPPONENT_SIZE) {
                    positionFree = false;
                    break;
                }
            }
        } while (!positionFree);

        opponents.add(new Opponents(randomX, fixedY));
    }

    public void draw(Graphics graphics) {
        if (!gameOver) {
            hero.draw(graphics);
            for (Opponents opponent : opponents) {
                opponent.draw(graphics);
            }
            for (Bullet bullet : heroBullets) {
                bullet.draw(graphics);
            }
            for (Bullet bullet : opponentBullets) {
                bullet.draw(graphics);
            }
            line.draw(graphics);
        } else {
            drawGameOver(graphics);
        }
    }

    private void drawGameOver(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.setFont(new Font("Arial", Font.BOLD, 50));
        graphics.drawString("Game Over", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2);
    }

    public void moveLeft(int variable) {
        hero.moveLeft(variable);
    }

    public void moveRight(int variable) {
        hero.moveRight(variable);
    }

    public void moveDown(int variable) {
        for (Opponents opponent : opponents) {
            opponent.moveDown(variable);
        }
    }

    public void drawShoot(Graphics graphics) {
        Bullet bullet = hero.shoot();
        heroBullets.add(bullet);
    }

    public void update() {
        if (!gameOver) {
            // Mover balas del héroe hacia arriba
            for (Bullet bullet : heroBullets) {
                bullet.moveUp(5);
            }

            // Mover balas de los oponentes hacia abajo
            for (Bullet bullet : opponentBullets) {
                bullet.moveDown(5);
            }

            // Mover enemigos hacia abajo
            moveDown(2);  // Ajusta la velocidad según sea necesario

            // Verificar colisiones de balas del héroe con los oponentes
            Iterator<Bullet> bulletIterator = heroBullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                Iterator<Opponents> opponentIterator = opponents.iterator();
                while (opponentIterator.hasNext()) {
                    Opponents opponent = opponentIterator.next();
                    if (opponent.checkCollision(bullet)) {
                        bulletIterator.remove();
                        opponentIterator.remove();
                        break;
                    }
                }
            }

            // Verificar colisiones de balas de los oponentes con el héroe
            Iterator<Bullet> opponentBulletIterator = opponentBullets.iterator();
            while (opponentBulletIterator.hasNext()) {
                Bullet bullet = opponentBulletIterator.next();
                if (hero.checkCollision(bullet)) {
                    opponentBulletIterator.remove();
                    hero.decreaseLife();
                    break;
                }
            }

            // Los oponentes disparan de forma aleatoria
            if (random.nextInt(100) < 5) { // Probabilidad del 5% de disparar
                for (Opponents opponent : opponents) {
                    opponentBullets.add(opponent.shoot());
                }
            }

            // Verificar colisiones de los oponentes con la línea
            for (Opponents opponent : opponents) {
                if (opponent.getLowestY() >= 420) {
                    gameOver = true;
                    break;
                }
            }
        }
    }

    public int getHeroLives() {
        return hero.getLives();
    }
}
