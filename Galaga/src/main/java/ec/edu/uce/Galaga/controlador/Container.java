package ec.edu.uce.Galaga.controlador;

import ec.edu.uce.Galaga.models.Bullet;
import ec.edu.uce.Galaga.models.Hero;
import ec.edu.uce.Galaga.models.Line;
import ec.edu.uce.Galaga.models.Opponents;
import ec.edu.uce.Galaga.models.SuperOpponent;

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
    Random random = new Random();
    Line line = new Line();
    boolean gameOver = false;
    private long lastOpponentShotTime = 0;
    private static final long[] LEVEL_SHOOT_DELAYS = {8000, 4000};
    public int score = 0;
    public int level = 1;
    int SCORE_PER_LEVEL = 25;
    int opponentSpeed = 2;
    int opponentShootFrequency = 5;
    int opponentDamage = 5;

    public SuperOpponent superOpponent;

    public Container() {
        initializeLevel(level);
    }

    private void initializeLevel(int level) {
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
                opponentShootFrequency = 10;
                opponentDamage = 10;
                break;
            case 3:
                superOpponent = new SuperOpponent(300, 50);
                break;
            default:
                break;
        }
    }

    private void addOpponent() {
        boolean positionFree;
        int randomX, randomY;
        do {
            positionFree = true;
            randomX = random.nextInt(SCREEN_WIDTH - OPPONENT_SIZE/2);
            randomY = random.nextInt(200);

            for (Opponents opponent : opponents) {
                if (Math.abs(opponent.getX() - randomX) < OPPONENT_SIZE && Math.abs(opponent.getY() - randomY) < OPPONENT_SIZE) {
                    positionFree = false;
                    break;
                }
            }
        } while (!positionFree);

        opponents.add(new Opponents(randomX, randomY));
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
            if (level == 3 && superOpponent != null) {
                superOpponent.draw(graphics);
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
            try {
                // Movimiento de balas del héroe hacia arriba
                for (Bullet bullet : heroBullets) {
                    bullet.moveUp(5);
                }

                // Movimiento de balas de los oponentes hacia abajo
                for (Bullet bullet : opponentBullets) {
                    bullet.moveDown(5);
                }

                // Movimiento de los oponentes hacia abajo
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
                            if (level == 2) {
                                if (opponent.isDestroyed()) {
                                    opponentIterator.remove();
                                    score += 10;
                                    checkLevelUp();
                                }
                            } else {
                                opponentIterator.remove();
                                score += 5;
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

                // Control de disparo de los oponentes basado en el tiempo
                long currentTime = System.currentTimeMillis();
                long shootDelay = LEVEL_SHOOT_DELAYS[level - 1];
                if (currentTime - lastOpponentShotTime >= shootDelay) {
                    try {
                        ListIterator<Opponents> opponentIterator = opponents.listIterator();
                        while (opponentIterator.hasNext()) {
                            Opponents opponent = opponentIterator.next();
                            opponentBullets.add(opponent.shoot());
                            if (level > 1) {
                                opponentBullets.add(opponent.shootFromLeft());
                            }
                        }
                        lastOpponentShotTime = currentTime;
                    } catch (ConcurrentModificationException e) {
                        System.err.println("ConcurrentModificationException caught: " + e.getMessage());
                    }
                }

                // Verificar colisiones de los oponentes con la línea y eliminar los oponentes destruidos
                Iterator<Opponents> opponentIterator = opponents.iterator();
                while (opponentIterator.hasNext()) {
                    Opponents opponent = opponentIterator.next();
                    if (opponent.getLowestY() >= 420) {
                        opponentIterator.remove();
                        gameOver = true;
                        break;
                    }
                }

                // Manejar el SuperOpponent si estamos en el nivel 3
                if (level == 3 && superOpponent != null) {
                    // Verificar si el SuperOpponent ha sido destruido
                    if (superOpponent.getHealth() <= 0) {
                        superOpponent = null;
                        score += 50; // Aumentar la puntuación por destruir al SuperOpponent
                        checkLevelUp(); // Verificar si se ha alcanzado la puntuación necesaria para pasar de nivel
                    } else {
                        // Si el SuperOpponent todavía está activo, manejar su comportamiento
                        // Verificar si puede disparar y realizar el disparo si es posible
                        long currentTimeSuperOpponent = System.currentTimeMillis();
                        if (superOpponent.canShoot(currentTimeSuperOpponent)) {
                            for (int i = 0; i < 3; i++) {
                                opponentBullets.add(superOpponent.shoot());
                            }
                            superOpponent.resetShootTime(currentTimeSuperOpponent);
                        }

                        // Verificar colisiones entre las balas del héroe y el SuperOpponent
                        Iterator<Bullet> bulletIteratorHero = heroBullets.iterator();
                        while (bulletIteratorHero.hasNext()) {
                            Bullet bullet = bulletIteratorHero.next();
                            if (superOpponent.checkCollision(bullet)) {
                                bulletIteratorHero.remove(); // Eliminar la bala que ha colisionado con el SuperOpponent
                                // Calcular y aplicar el daño al SuperOpponent
                                if (superOpponent.getHealth() > 0) {
                                    int damage = calculateSuperOpponentDamage(superOpponent.getHealth());
                                    superOpponent.decreaseHealth(damage);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Manejar excepciones aquí
                System.err.println("Se ha producido una excepción en el método update: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private int calculateSuperOpponentDamage(int health) {
        if (health < 50) {
            return 15; // Resta 15 puntos si la vida es menor a 50
        } else if (health < 75) {
            return 10; // Resta 10 puntos si la vida está entre 50 y 75
        } else {
            return 5; // Resta 5 puntos si la vida es mayor o igual a 75
        }
    }

    private void checkLevelUp() {
        if (score >= SCORE_PER_LEVEL * level && level < 3) { // Solo aumentar el nivel si el nivel actual es menor que 3
            level++;
            switch (level) {
                case 2:
                    initializeLevel(level);
                    break;
                case 3:
                    if (superOpponent == null) {
                        initializeLevel(level);
                    }
                    break;
                default:
                    break;
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
