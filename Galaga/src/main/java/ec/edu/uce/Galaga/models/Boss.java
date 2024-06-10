    package ec.edu.uce.Galaga.models;

    import java.awt.*;

    public class Boss extends Opponents {
        private int health = 100; // Vida del jefe
        private int damage = 15; // Daño que hace el jefe al héroe

        public int getDamage() {
            return damage;
        }

        public Boss(int randomX, int randomY, int health) {
            super(randomX, randomY, 1000);

            this.cord_x = new int[]{randomX, randomX + 200, randomX + 200, randomX + 100, randomX};
            this.cord_y = new int[]{randomY, randomY, randomY + 100, randomY + 50, randomY + 100};
            this.health = health;
        }



        @Override
        public void draw(Graphics graphics) {
            graphics.setColor(Color.RED);
            graphics.fillPolygon(cord_x, cord_y, 5);

            // Dibujar la barra de vida del jefe justo encima de él
            int barWidth = 100;
            int barHeight = 10;
            int barX = cord_x[0];
            int barY = cord_y[0] - 15;
            int healthWidth = (int) ((double) health / 100 * barWidth);


            graphics.setColor(Color.GREEN);
            graphics.fillRect(barX, barY, healthWidth, barHeight);
            graphics.setColor(Color.WHITE);
            graphics.drawRect(barX, barY, barWidth, barHeight);

            // Dibujar el número de vida al lado de la barra del jefe
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("Arial", Font.BOLD, 12));
            graphics.drawString(health + "/100", barX + barWidth + 5, barY + barHeight);


        }

        @Override
        public boolean checkCollision(Bullet bullet) {
            boolean collision = super.checkCollision(bullet); // Verificar colisión con el jefe
            if (collision) {
                health -= bullet.getDamage(); // Reducir la vida del jefe según el daño de la bala
            }
            return collision;
        }


        public void decreaseHealth(int heroHealth) {
            if (heroHealth >75) {
                health -= 15;
            } else if (heroHealth < 75 && heroHealth>50) {
                health -= 10;
            } else if(heroHealth < 50){
                health -= 5;
            }

        }


        public int getHealth() {
            return health;
        }
    }