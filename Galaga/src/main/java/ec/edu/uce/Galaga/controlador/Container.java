    package ec.edu.uce.Galaga.controlador;

    import ec.edu.uce.Galaga.models.*;
    import java.awt.Color;
    import java.awt.Font;
    import java.awt.Graphics;
    import java.util.*;

    public class Container {

        final int SCREEN_WIDTH = 700;
        final int SCREEN_HEIGHT = 600; // Cambiar para coincidir con el tamaño del frame
        final int OPPONENT_SIZE = 100; // Tamaño aproximado del enemigo para evitar superposición
        public Hero hero;
        List<Opponents> opponents = new ArrayList<>();
        List<Bullet> heroBullets = new ArrayList<>();
        List<Bullet> opponentBullets = new ArrayList<>();
        Random random = new Random();
        Random random2 = new Random();
        Line line = new Line();
        boolean gameOver = false;

        // Variables para la gestión del nivel y la puntuación

        public int level = 1;
        int SCORE_PER_LEVEL = 25; // Puntuación para pasar al siguiente nivel
        int opponentSpeed = 2; // Velocidad inicial de los oponentes
        int opponentDamage = 5; // Daño inicial de los oponentes
        public boolean isPaused = false;//Pausar
        Boss boss;

        public Container(String userName) {
           hero = new Hero(userName); // Crear un nuevo jugador con el nombre de usuario
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
                    opponentDamage = 15;
                    break;

                default:

                    break;
            }
        }
        private void initializeBoss() {
            boss = new Boss(300, 100, 100); // Crear al jefe en una posición específica
        }
        private void addOpponent(int lives) {
            boolean positionFree;
            int randomX, randomY; // Coordenadas aleatorias tanto en X como en Y
            do {
                positionFree = true;
                randomX = random.nextInt(SCREEN_WIDTH - OPPONENT_SIZE / 2);
                randomY = random.nextInt(200); //

                for (Opponents opponent : opponents) {
                    if (Math.abs(opponent.getX() - randomX) < OPPONENT_SIZE && Math.abs(opponent.getY() - randomY) < OPPONENT_SIZE) {
                        positionFree = false;
                        break;
                    }
                }
            } while (!positionFree);

            opponents.add(new Opponents(randomX, randomY, lives)); // Añadir enemigo con el número de vidas especificado
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
                if (boss != null) {
                    boss.draw(graphics); // dibujar el jefe

                }

                if (isPaused) {
                    drawPaused(graphics); // Mostrar mensaje de pausa
                }
            } else {
                drawGameOver(graphics);
            }
        }
        private void drawPaused(Graphics graphics) {
            graphics.setColor(Color.YELLOW);
            graphics.setFont(new Font("Arial", Font.BOLD, 50));
            graphics.drawString("Paused", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2);
        }

        private void drawGameOver(Graphics graphics) {
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Arial", Font.BOLD, 50));
            graphics.drawString("Juego Terminado", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2);
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
            if (isPaused || gameOver) {
                return; //  juego está en pausa o  ha terminado
            }

            // Mover balas del héroe hacia arriba
            for (Bullet bullet : heroBullets) {
                bullet.moveUp(20);
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

                        if (opponent.isDestroyed()) {
                            opponentIterator.remove();
                            hero.increaseScore((level == 2) ? 10 : 5); // Incrementar el puntaje del jugador
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
                        hero.increaseScore((level==3)?15:15);  // Sumar puntos al score por impactar al jefe
                        break;
                    }
                }
            }

            // Control de disparo de los oponentes basado en la probabilidad
            for (Opponents opponent : opponents) {
                if (random.nextDouble() < 0.01) { // Probabilidad de 1% de disparar
                    opponentBullets.add(opponent.shoot());
                    if (level > 1) {
                        opponentBullets.add(opponent.shootFromLeft());
                    }
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

        private void checkLevelUp() {
            if (hero.getScore() >= SCORE_PER_LEVEL * level) {
                if (level == 1 && opponents.isEmpty()) {
                    level++;
                    initializeLevel(level);
                } else if (level == 2 && opponents.isEmpty()) {
                    level++;
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
        public int getScore() {
            return hero.getScore();
        }

        public int getLevel() {
            return level;
        }

    }
