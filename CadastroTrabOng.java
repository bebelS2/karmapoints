import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CadastroTrabOng extends JFrame {
    private JFrame frame;
    private JTextField txtNome;
    private JTextField txtData;
    private JTextField txtHorario;
    private JTextField txtRequisitos;
    private JTextField txtOds;
    private JTextField txtPontos;
    private JButton btnCadastrar;
    private DatabaseConnector databaseConnector;
    private Connection connection;

    public CadastroTrabOng() {
        initialize();
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();}

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Cadastro de Trabalho Voluntário");
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
        
        JLabel labelTitulo = new JLabel("Cadastro de Trabalho voluntário:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel lblNome = new JLabel("Nome do Trabalho Voluntário:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        contentPanel.add(lblNome, constraints);

        txtNome = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 4;
        contentPanel.add(txtNome, constraints);

        JLabel lblData = new JLabel("Data em que o trabalho será realizado (dd/mm/aaaa):");
        constraints.gridx = 0;
        constraints.gridy = 5;
        contentPanel.add(lblData, constraints);

        txtData = new JTextField(10);
        constraints.gridx = 1;
        constraints.gridy = 6;
        contentPanel.add(txtData, constraints);

        JLabel lblHorario = new JLabel("Horário em que o trabalho será realizado:");
        constraints.gridx = 0;
        constraints.gridy = 7;
        contentPanel.add(lblHorario, constraints);

        txtHorario = new JTextField(10);
        constraints.gridx = 1;
        constraints.gridy = 8;
        contentPanel.add(txtHorario, constraints);

        JLabel lblRequisitos = new JLabel("Requisitos do voluntário para a inscrição ao trabalho:");
        constraints.gridx = 0;
        constraints.gridy = 9;
        contentPanel.add(lblRequisitos, constraints);

        txtRequisitos = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 10;
        contentPanel.add(txtRequisitos, constraints);

        JLabel lblOds = new JLabel("ODS que o trabalho auxilia:");
        constraints.gridx = 0;
        constraints.gridy = 11;
        contentPanel.add(lblOds, constraints);

        txtOds = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 12;
        contentPanel.add(txtOds, constraints);

        JLabel lblPontos = new JLabel("Pontos que serão atribuídos ao voluntário após o trabalho:");
        constraints.gridx = 0;
        constraints.gridy = 13;
        contentPanel.add(lblPontos, constraints);

        txtPontos = new JTextField(10);
        constraints.gridx = 1;
        constraints.gridy = 14;
        contentPanel.add(txtPontos, constraints);

        btnCadastrar = new JButton("Cadastrar");
        constraints.gridx = 0;
        constraints.gridy = 15;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnCadastrar, constraints);

        // Evento de clique do botão cadastrar
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarTrabalho();
            }
        });
        JButton buttonVoltar = new JButton("Voltar");
        buttonVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InterfaceOng();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 16;
        contentPanel.add(btnCadastrar, constraints);
        constraints.gridx = 1;
        constraints.gridy = 17;
        contentPanel.add(buttonVoltar, constraints);

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

    private void cadastrarTrabalho() {
        String nome = txtNome.getText();
        String dataStr = txtData.getText();
        int pontos = Integer.parseInt(txtPontos.getText());
        String horario = txtHorario.getText();
        String requisitos = txtRequisitos.getText();
        String ods = txtOds.getText();
        int idOng = getIdOngLogado(); // Obter o ID do ong logado do sistema de login

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dataUtil = null;
            try {
                dataUtil = dateFormat.parse(dataStr);
            } catch (ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Formato de data inválido.");
                return;
            }
            java.sql.Date data = new java.sql.Date(dataUtil.getTime());

            String sql = "INSERT INTO trabalho (nome, data, horario, requisitos, ods, pontos, idOng) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nome);
            statement.setDate(2, data);
            statement.setString(3, horario);
            statement.setString(4, requisitos);
            statement.setString(5, ods);
            statement.setInt(6, pontos);
            statement.setInt(7, idOng);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Trabalho cadastrado com sucesso!");
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao cadastrar o trabalho.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados.");
        }
    }

    private int getIdOngLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdOngLogado();
    }

    private void limparCampos() {
        txtNome.setText("");
        txtData.setText("");
        txtHorario.setText("");
        txtRequisitos.setText("");
        txtOds.setText("");
        txtPontos.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CadastroTrabOng();
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