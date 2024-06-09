    package ec.edu.uce.Galaga.models;

    import java.awt.*;

    public class Line implements Drawable{
        @Override
        public void draw(Graphics graphics) {
            graphics.setColor(Color.red);
            graphics.drawLine(0,420,784,420);
        }

        @Override
        public void draw(Graphics graphics, Drawable drawable) {

        }
    }
