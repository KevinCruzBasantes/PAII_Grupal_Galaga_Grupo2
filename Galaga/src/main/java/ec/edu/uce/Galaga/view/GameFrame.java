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
        setSize(800, 600);

        container = new Container();
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                container.draw(g);
                drawLives(g);
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
        g.drawString("Lives: " + container.getHeroLives() + " | Score: " + container.getScore(), 10, 20);
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
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    container.drawShoot(getGraphics());
                    if (container.getLevel() == 2) {
                        // Lógica para el nivel 2
                    } else if (container.getLevel() == 3) {
                        // Lógica para el nivel 3
                    }
                }
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
