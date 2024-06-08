package ec.edu.uce.Galaga.controlador;

import ec.edu.uce.Galaga.models.Bullet;
import ec.edu.uce.Galaga.models.Hero;
import ec.edu.uce.Galaga.models.Line;
import ec.edu.uce.Galaga.models.Opponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    // Variables para la gestión del nivel y la puntuación
    public int score = 0;
    public int level = 1;
     int SCORE_PER_LEVEL = 25; // Puntuación para pasar al siguiente nivel
    int opponentSpeed = 2; // Velocidad inicial de los oponentes
    int opponentShootFrequency = 5; // Frecuencia inicial de disparo de los oponentes
    int opponentDamage = 5; // Daño inicial de los oponentes

    public Container() {
        initializeLevel(level);
    }

    private void initializeLevel(int level) {
        //opponents.clear();
        //heroBullets.clear();
        //opponentBullets.clear();
        //gameOver = false;

        switch (level) {
            case 1:
                for (int i = 0; i < 5; i++) {
                    addOpponent();
                }
                opponentShootFrequency = 5;
                opponentDamage = 5;
                break;
            case 2:
                for (int i = 0; i < 10; i++) {
                    addOpponent();
                }
                opponentShootFrequency = 3;
                opponentDamage = 10;
                break;
            // Otros niveles si es necesario
            default:
                // Nivel no implementado
                break;
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
            moveDown(opponentSpeed);  // Ajusta la velocidad según sea necesario

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
                        score += 5; // Incrementar la puntuación
                        checkLevelUp();
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
                    hero.decreaseLife(opponentDamage);
                    break;
                }
            }

            // Los oponentes disparan de forma aleatoria con mayor frecuencia
            if (random.nextInt(100) < opponentShootFrequency) { // Probabilidad de disparar
                for (Opponents opponent : opponents) {
                    opponentBullets.add(opponent.shoot());
                    if (level > 1) {
                        opponentBullets.add(opponent.shootFromLeft());
                        opponentBullets.add(opponent.shootFromRight());
                    }
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


    private void checkLevelUp() {
        if (score >= SCORE_PER_LEVEL * level) {
            level++;
            if (level == 2) {
                initializeLevel(level);
            }
        }
    }

    public int getHeroLives() {
        return hero.getLives();
    }

    public int getHeroHealth() {
        return hero.getHealth();
    }
}