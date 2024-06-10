    package ec.edu.uce.Galaga.view;


    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.Scanner;

    public class StartFrame extends JFrame {
        private JTextField nameField;
        private JButton startButton;
        private String userName;

        public StartFrame() {
            setTitle("Start Game");
            setSize(400, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(3, 1));

            JLabel nameLabel = new JLabel("Enter your name:");
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            add(nameLabel);

            nameField = new JTextField();
            add(nameField);

            startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userName = nameField.getText();
                    if (!userName.isEmpty()) {
                        startGame();
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter your name");
                    }
                }
            });
            add(startButton);
        }


        private void startGame() {
            dispose(); // Cierra la ventana de inicio
            long playerId = registerUser(userName); // Llama al método para registrar al usuario en el backend y obtener el playerId
            if (playerId != -1) {
                GameFrame gameFrame = new GameFrame(userName, playerId); // Pasa el nombre de usuario y el playerId al GameFrame
                gameFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to register user");
            }
        }
        //guardar el usuario
        private long registerUser(String userName) {
            long playerId = -1;
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

                
                if (con.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    ObjectMapper mapper = new ObjectMapper();
                    try (InputStream is = con.getInputStream()) {
                        String responseBody = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
                        JsonNode rootNode = mapper.readTree(responseBody);
                        playerId = rootNode.get("id").asLong();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return playerId;
        }
        }



