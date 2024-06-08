package ec.edu.uce.Galaga.view;

import ec.edu.uce.Galaga.controlador.Container;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameFrame extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Container container;

    public GameFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650); // Aumentar el tamaño para acomodar la barra de vida
        setResizable(false); // Evitar que el usuario redimensione la ventana

        container = new Container();
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                container.draw(g);
                drawLives(g);
                drawHeroHealthBar(g);
                drawLine(g); // Dibujar la línea roja al final para que esté encima de la barra de vida
            }
        };
        contentPane.setBackground(Color.black);
        setContentPane(contentPane);
        addKeyListener(this);

        Timer timer = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                container.update();
                repaint();
            }
        });
        timer.start();
    }

    private void drawLives(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Lives: " + container.getHeroLives(), 10, 570); // Posicionado debajo de la línea roja
    }

    private void drawHeroHealthBar(Graphics g) {
        int heroHealth = container.getHeroHealth();
        int healthBarWidth = 100; // Ancho de la barra de vida
        int healthBarHeight = 10; // Alto de la barra de vida
        int healthBarX = 10; // Coordenada X de la barra de vida
        int healthBarY = 580; // Coordenada Y de la barra de vida (debajo de la línea roja)

        g.setColor(Color.GREEN);
        g.fillRect(healthBarX, healthBarY, heroHealth, healthBarHeight); // Barra verde de vida
        g.setColor(Color.WHITE);
        g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight); // Borde blanco de la barra de vida
    }

    private void drawLine(Graphics g) {
        g.setColor(Color.red);
        g.drawLine(0, 420, 784, 420); // Dibujar la línea roja en la posición deseada
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                container.moveLeft(10);
                break;
            case KeyEvent.VK_RIGHT:
                container.moveRight(10);
                break;
            case KeyEvent.VK_SPACE:
                container.drawShoot(getGraphics());
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No implementado
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No implementado
    }
}
