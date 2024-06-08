package ec.edu.uce.Galaga.models;

import java.awt.*;

public interface Drawable {

    public void draw(Graphics graphics);
    public void draw(Graphics graphics, Drawable drawable);

}