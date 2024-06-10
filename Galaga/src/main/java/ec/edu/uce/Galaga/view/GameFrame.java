        package ec.edu.uce.Galaga.view;

        import ec.edu.uce.Galaga.controlador.Container;
        import org.json.JSONObject;
        import java.awt.Color;
        import java.awt.Font;
        import java.awt.Graphics;
        import java.awt.event.ActionEvent;
        import java.awt.event.ActionListener;
        import java.awt.event.KeyEvent;
        import java.awt.event.KeyListener;
        import java.io.OutputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.Scanner;
        import javax.swing.JFrame;
        import javax.swing.JPanel;
        import javax.swing.Timer;

        public class GameFrame extends JFrame implements KeyListener {
            private String userName;
            private static final long serialVersionUID = 1L;
            private JPanel contentPane;
            private Container container;
            private long playerId;

            public GameFrame(String userName, long playerId) {
                super();
                this.playerId = playerId; // Registra al jugador y guarda el playerId
                this.userName = userName;
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(800, 650); // Aumentar el tamaño para acomodar la barra de vida
                setResizable(false); // Evitar que el usuario redimensione la ventana
                setLocationRelativeTo(null);
                container = new Container(userName);
                contentPane = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        container.draw(g);
                        drawLives(g);
                        drawHeroHealthBar(g);
                        drawLine(g);
                        drawScore(g);
                        drawLevel(g);
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
            private long registerPlayer(String userName) {
                try {
                    URL url = new URL("http://localhost:8080/api/register");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    String jsonInputString = "{\"userName\": \"" + userName + "\"}";
                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Obtener la respuesta
                    if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                        try (Scanner scanner = new Scanner(con.getInputStream())) {
                            String responseBody = scanner.useDelimiter("\\A").next();
                            // Parsear la respuesta para obtener el playerId
                            JSONObject jsonObject = new JSONObject(responseBody);
                            return jsonObject.getLong("id");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return -1; // En caso de error
            }
            private void drawScore(Graphics g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Score: " + container.getScore(), 10, 550);
            }

            private void drawLevel(Graphics g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Level: " + container.getLevel(), 10, 570);
            }

            private void drawLives(Graphics g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Lives: " + container.getHeroLives(), 10, 590);
            }

            private void drawHeroHealthBar(Graphics g) {
                int heroHealth = container.getHeroHealth();
                int healthBarWidth = 100;
                int healthBarHeight = 10;
                int healthBarX = 10;
                int healthBarY = 600;

                g.setColor(Color.GREEN);
                g.fillRect(healthBarX, healthBarY, heroHealth, healthBarHeight);
                g.setColor(Color.WHITE);
                g.drawRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
            }

            private void drawLine(Graphics g) {
                g.setColor(Color.red);
                g.drawLine(0, 420, 784, 420);
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
                    case KeyEvent.VK_ENTER:
                        container.isPaused = !container.isPaused;
                        if (container.isPaused) {
                            saveGameState();
                        }
                        break;
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            // Modificar el método saveGameState para usar playerId
            private void saveGameState() {
                try {
                    URL url = new URL("http://localhost:8080/api/saveState");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    String jsonInputString = "{\"state\": \"paused\", \"player\": { \"id\": " + playerId + " }}";
                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    con.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
