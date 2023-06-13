import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LoginVol {
    private JFrame frame;
    private JTextField textFieldEmail;
    private JPasswordField passwordFieldSenha;
    private DatabaseConnector databaseConnector;
    private Connection connection;

    public LoginVol() {
        initialize();
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();}

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Login como Voluntário");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/logomini.png"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        // Carregar imagem de plano de fundo
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background.png"));
            BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImage);
            frame.setContentPane(backgroundPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        try {
            BufferedImage logoImage = ImageIO.read(new File("resources/logo.png"));
            Image resizedLogoImage = logoImage.getScaledInstance(200, 120, Image.SCALE_SMOOTH);
            ImageIcon resizedLogoIcon = new ImageIcon(resizedLogoImage);
            JLabel labelLogo = new JLabel(resizedLogoIcon);
            labelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            contentPanel.add(labelLogo, constraints);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel labelTitulo = new JLabel("Faça login como Voluntário:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);

        JLabel lblEmail = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        contentPanel.add(lblEmail, constraints);

        textFieldEmail = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 2;
        contentPanel.add(textFieldEmail, constraints);

        JLabel lblSenha = new JLabel("Senha:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        contentPanel.add(lblSenha, constraints);

        passwordFieldSenha = new JPasswordField(15);
        constraints.gridx = 1;
        constraints.gridy = 3;
        contentPanel.add(passwordFieldSenha, constraints);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnLogin, constraints);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Fecha a janela atual
                new MainInterface();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnVoltar, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        frame.add(contentPanel, constraints);

        frame.pack();
        frame.setResizable(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void realizarLogin() {
        String email = textFieldEmail.getText();
        String senha = new String(passwordFieldSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM voluntario WHERE email = ? AND senha = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, senha);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int idVolLogado = resultSet.getInt("idVol");

                // Armazenar o ID do vol logado na classe UserData
                UserData userData = UserData.getInstance();
                userData.setIdVolLogado(idVolLogado);

                JOptionPane.showMessageDialog(frame, "Login realizado com sucesso!");
                fecharJanela(); // Fecha a janela de login

                // Abrir a interface vol
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new InterfaceVol();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(frame, "Email ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao realizar login: " + ex.getMessage());
        }
    }

    private void fecharJanela() {
        synchronized (frame) {
            frame.notify();
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginVol();
            }
        });
    }
}

class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;

    public BackgroundPanel(BufferedImage image) {
        this.backgroundImage = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
