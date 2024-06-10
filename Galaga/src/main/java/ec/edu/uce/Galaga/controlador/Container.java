package ec.edu.uce.Galaga.controlador;

import ec.edu.uce.Galaga.models.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.*;

public class Container {

    final int SCREEN_WIDTH = 700;
    final int SCREEN_HEIGHT = 600;
    final int OPPONENT_SIZE = 100;
    Hero hero = new Hero();
    List<Opponents> opponents = new ArrayList<>();
    List<Bullet> heroBullets = new ArrayList<>();
    List<Bullet> opponentBullets = new ArrayList<>();
    List<Bullet> bossBullets = new ArrayList<>();
    Random random = new Random();
    Random random2 = new Random();
    Line line = new Line();
    boolean gameOver = false;
    boolean youWIn = false;

    public int score = 0;
    public int level = 1;
    int SCORE_PER_LEVEL = 25;
    int opponentSpeed = 2;
    int opponentShootFrequency = 5;
    int opponentDamage = 5;
    Boss boss;

    public Container() {
        initializeLevel(level);
    }

    private void initializeLevel(int level) {
        switch (level) {
            case 1:
                for (int i = 0; i < 5; i++) {
                    addOpponent(1);
                }
                opponentDamage = 5;
                break;
            case 2:
                for (int i = 0; i < 10; i++) {
                    addOpponent(3);
                }
                opponentDamage = 10;
                break;
            case 3: // Aparecerá el jefe en el nivel 3
                initializeBoss();
                break;
            // Otros niveles si es necesario
            default:
                // Nivel no implementado
                break;
        }
    }

    private void addOpponent(int lives) {
        boolean positionFree;
        int randomX, randomY;
        do {
            positionFree = true;
            randomX = random.nextInt(SCREEN_WIDTH - OPPONENT_SIZE / 2);
            randomY = random.nextInt(200);

            for (Opponents opponent : opponents) {
                if (Math.abs(opponent.getX() - randomX) < OPPONENT_SIZE && Math.abs(opponent.getY() - randomY) < OPPONENT_SIZE) {
                    positionFree = false;
                    break;
                }
            }
        } while (!positionFree);

        opponents.add(new Opponents(randomX, randomY, lives));
    }

    private void initializeBoss() {
        boss = new Boss(300, 100, 100); // Crear al jefe en una posición específica
    }

    public void draw(Graphics graphics) {
        if (!gameOver && !youWIn) {
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
            if (boss != null) {
                boss.draw(graphics); // Aquí se debe dibujar el jefe

            }

        } else {
            drawGameOver(graphics);
        }
    }

    private void drawGameOver(Graphics graphics) {
        graphics.setColor(Color.BLUE);
        graphics.setFont(new Font("Arial", Font.BOLD, 50));
        graphics.drawString("Juego Terminado :)", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2);
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
                bullet.moveUp(20);
            }

            // Mover balas de los oponentes hacia abajo
            for (Bullet bullet : opponentBullets) {
                bullet.moveDown(5);
            }

            // Mover enemigos hacia abajo
            moveDown(opponentSpeed);

            // Verificar colisiones de balas del héroe con los oponentes
            Iterator<Bullet> bulletIterator = heroBullets.iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                Iterator<Opponents> opponentIterator = opponents.iterator();
                while (opponentIterator.hasNext()) {
                    Opponents opponent = opponentIterator.next();
                    if (opponent.checkCollision(bullet)) {
                        bulletIterator.remove();

                        if (opponent.isDestroyed()) {
                            opponentIterator.remove();
                            score += (level == 2) ? 10 : 5;
                            checkLevelUp();
                        }

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

            // Verificar colisiones de balas del héroe con el jefe
            if (boss != null) {
                if(random2.nextDouble()<0.03) {
                    opponentBullets.add(boss.shoot());
                    opponentBullets.add(boss.shootFromLeft());
                    opponentBullets.add(boss.shootFromRight());
                }
                Iterator<Bullet> heroBulletIterator = heroBullets.iterator();

                while (heroBulletIterator.hasNext()) {
                    Bullet bullet = heroBulletIterator.next();
                    if (boss.checkCollision(bullet)) {
                        heroBulletIterator.remove();
                        boss.decreaseHealth(hero.getHealth()); // Reducir la vida del jefe según el daño de la bala
                        score += 15; // Sumar puntos al score por impactar al jefe
                        break;
                    }
                }
            }

            // Control de disparo de los oponentes basado en la probabilidad
            for (Opponents opponent : opponents) {
                if (random.nextDouble() < 0.02) {
                    opponentBullets.add(opponent.shoot());
                    if (level > 1) {
                        opponentBullets.add(opponent.shootFromLeft());
                    }
                }
            }
            Iterator<Opponents> opponentIterator = opponents.iterator();
            while (opponentIterator.hasNext()) {
                Opponents opponent = opponentIterator.next();
                if (opponent.getLowestY() >= 420) {
                    opponentIterator.remove();
                    gameOver = true;
                    break;
                }
            }
            if (hero.getHealth() <= 0 || hero.getLives() <= 0) {
                gameOver = true;
            }
            // Verificar colisión del jefe con la línea
            if (boss != null && boss.getLowestY() >= 420) {
                gameOver = true;
            }

            // Verificar si el héroe derrota al jefe
            if (boss != null && boss.isDestroyed()) {
                boss = null; // Eliminar al jefe si ha sido derrotado
                checkLevelUp(); // Comprobar si es necesario avanzar de nivel
            }

            // Verificar si el jefe derrota al héroe
            if (boss != null && boss.getHealth() <= 0) {
                gameOver = true;
            }
        }
    }

    private void checkLevelUp() {
        if (score >= SCORE_PER_LEVEL * level) {
            if (level == 1 && opponents.isEmpty()) {
                level++;
                initializeLevel(level);
            } else if (level == 2 && opponents.isEmpty()) {
                level++;
                initializeLevel(level);
            }
            // Puedes agregar más niveles aquí si es necesario
        }
    }

    public Boss getBoss() {
        return boss;
    }

    public int getHeroLives() {
        return hero.getLives();
    }

    public int getHeroHealth() {
        return hero.getHealth();
    }
}