import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class CadastroVol {
    private JFrame frame;
    private JTextField textFieldNome;
    private JTextField textFieldIdade;
    private JTextField textFieldCPF;
    private JTextField textFieldTelefone;
    private JTextField textFieldEmail;
    private JPasswordField passwordFieldSenha;
    private DatabaseConnector databaseConnector;
    private Connection connection;

    public CadastroVol() {
        initialize();
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();}

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Cadastro como Voluntário");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("resources/logomini.png")); // Definir o caminho do logo do seu aplicativo
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
            BufferedImage logoImage = ImageIO.read(new File("resources/logo.png")); // Defina o caminho para o seu logo
            Image resizedLogoImage = logoImage.getScaledInstance(200, 120, Image.SCALE_SMOOTH); // Defina o tamanho desejado
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

        JLabel labelTitulo = new JLabel("Cadastre-se como Voluntário:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);

        JLabel lblNome = new JLabel("Nome:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        contentPanel.add(lblNome, constraints);

        textFieldNome = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 2;
        contentPanel.add(textFieldNome, constraints);

        JLabel lblIdade = new JLabel("Idade:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        contentPanel.add(lblIdade, constraints);

        textFieldIdade = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 3;
        contentPanel.add(textFieldIdade, constraints);

        JLabel lblCPF = new JLabel("CPF:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        contentPanel.add(lblCPF, constraints);

        textFieldCPF = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 4;
        contentPanel.add(textFieldCPF, constraints);

        JLabel lblTelefone = new JLabel("Telefone:");
        constraints.gridx = 0;
        constraints.gridy = 5;
        contentPanel.add(lblTelefone, constraints);

        textFieldTelefone = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 5;
        contentPanel.add(textFieldTelefone, constraints);

        JLabel lblEmail = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 6;
        contentPanel.add(lblEmail, constraints);

        textFieldEmail = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 6;
        contentPanel.add(textFieldEmail, constraints);

        JLabel lblSenha = new JLabel("Senha:");
        constraints.gridx = 0;
        constraints.gridy = 7;
        contentPanel.add(lblSenha, constraints);

        passwordFieldSenha = new JPasswordField(15);
        constraints.gridx = 1;
        constraints.gridy = 7;
        contentPanel.add(passwordFieldSenha, constraints);

        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarVol();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 8;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnCadastrar, constraints);

        JButton btnMainInterface = new JButton("Voltar");
        btnMainInterface.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Fechar a janela atual
                new MainInterface(); // Abrir a interface principal
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 9;
        contentPanel.add(btnMainInterface, constraints);

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

    private void cadastrarVol() {
        String nome = textFieldNome.getText();
        String idade = textFieldIdade.getText();
        String cpf = textFieldCPF.getText();
        String telefone = textFieldTelefone.getText();
        String email = textFieldEmail.getText();
        String senha = new String(passwordFieldSenha.getPassword());

        // Verificar se algum campo está vazio
        if (nome.isEmpty() || !cpf.matches("\\d+") || cpf.length() < 11 || !telefone.matches("\\d+") || telefone.length() < 11 || !idade.matches("\\d+") || idade.isEmpty() || idade.length() > 3 ||email.isEmpty() || senha.isEmpty()) {            JOptionPane.showMessageDialog(frame, "Por favor, preencha todos os campos corretamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return; // Retornar sem prosseguir com o cadastro
        }

        String query = "INSERT INTO voluntario (nome, idade, cpf, telefone, email, senha) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            statement.setString(2, idade);
            statement.setString(3, cpf);
            statement.setString(4, telefone);
            statement.setString(5, email);
            statement.setString(6, senha);
            
            statement.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Cadastro realizado com sucesso!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao cadastrar a Voluntário: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CadastroVol();
            }
        });
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
    }}}
