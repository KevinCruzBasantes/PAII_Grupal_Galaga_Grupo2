package ec.edu.uce.Galaga.controlador;

import ec.edu.uce.Galaga.models.Bullet;
import ec.edu.uce.Galaga.models.Hero;
import ec.edu.uce.Galaga.models.Line;
import ec.edu.uce.Galaga.models.Opponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.*;


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
    // Variable para rastrear el tiempo transcurrido desde el último disparo de los oponentes
    private long lastOpponentShotTime = 0;
    private static final long[] LEVEL_SHOOT_DELAYS = {4000, 2000}; // Tiempo de disparo para cada nivel en milisegundos

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
        int randomX, randomY; // Coordenadas aleatorias tanto en X como en Y
        do {
            positionFree = true;
            randomX = random.nextInt(SCREEN_WIDTH - OPPONENT_SIZE/2);
            randomY = random.nextInt(200); // Por ejemplo, en el rango superior de la pantalla

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

                            // Reduce una vida al enemigo si es nivel 2, si su vida llega a cero, elimínalo de la lista
                            if (level == 2) {
                                if (opponent.isDestroyed()) {
                                    opponentIterator.remove();
                                    // Incrementa la puntuación si el enemigo es eliminado
                                    score += 10; // Suma 10 puntos por enemigo eliminado en nivel 2
                                    checkLevelUp(); // Verifica si es necesario pasar al siguiente nivel
                                }
                            } else {
                                // Si es nivel 1, elimina al enemigo directamente
                                opponentIterator.remove();
                                // Incrementa la puntuación si el enemigo es eliminado
                                score += 5; // Suma 5 puntos por enemigo eliminado en nivel 1
                                checkLevelUp(); // Verifica si es necesario pasar al siguiente nivel
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
                        // Los oponentes disparan una bala
                        ListIterator<Opponents> opponentIterator = opponents.listIterator();
                        while (opponentIterator.hasNext()) {
                            Opponents opponent = opponentIterator.next();
                            opponentBullets.add(opponent.shoot());
                            if (level > 1) {
                                opponentBullets.add(opponent.shootFromLeft());
                            }
                        }
                        // Reiniciar el tiempo para el próximo disparo
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
            } catch (Exception e) {
                // Manejo de la excepción
                System.err.println("Se ha producido una excepción en el método update: " + e.getMessage());
                e.printStackTrace();
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
