import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.*;

public class CadastroProdutoFinan extends JFrame {
    private JFrame frame;
    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtValor;
    private JTextField txtItem;
    private JButton btnCadastrar;
    private DatabaseConnector databaseConnector;
    private Connection connection;

    public CadastroProdutoFinan() {
        initialize();
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();    }

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
        
        JLabel labelTitulo = new JLabel("Cadastro de Produto:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel lblNome = new JLabel("Nome do produto:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        contentPanel.add(lblNome, constraints);

        txtNome = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        contentPanel.add(txtNome, constraints);

        JLabel lblDescricao = new JLabel("Descrição do produto:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        contentPanel.add(lblDescricao, constraints);

        txtDescricao = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 5;
        contentPanel.add(txtDescricao, constraints);

        JLabel lblValor = new JLabel("Valor em pontos:");
        constraints.gridx = 0;
        constraints.gridy = 6;
        contentPanel.add(lblValor, constraints);

        txtValor = new JTextField(10);
        constraints.gridx = 1;
        constraints.gridy = 7;
        contentPanel.add(txtValor, constraints);

        JLabel lblItem = new JLabel("Link em que o produto está ou nome do cupom:");
        constraints.gridx = 0;
        constraints.gridy = 8;
        contentPanel.add(lblItem, constraints);

        txtItem = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 9;
        contentPanel.add(txtItem, constraints);

        btnCadastrar = new JButton("Cadastrar");
        constraints.gridx = 0;
        constraints.gridy = 10;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        contentPanel.add(btnCadastrar, constraints);

        // Evento de clique do botão cadastrar
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarProduto();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 11;
        contentPanel.add(btnCadastrar, constraints);

        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Fecha a janela atual
                new InterfaceFinan();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 10;
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

    private void cadastrarProduto() {
        String nome = txtNome.getText();
        String descricao = txtDescricao.getText();
        int valor = Integer.parseInt(txtValor.getText());
        String item = txtItem.getText();
        int idFinan = getIdFinanciadorLogado(); // Obter o ID do financiador logado do sistema de login

        try {
            String sql = "INSERT INTO produto (nome, descricao, valor, item, idFinan) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nome);
            statement.setString(2, descricao);
            statement.setInt(3, valor);
            statement.setString(4, item);
            statement.setInt(5, idFinan);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
                limparCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao cadastrar o produto.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados.");
        }
    }

    private int getIdFinanciadorLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdFinanciadorLogado();
    }

    private void limparCampos() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtValor.setText("");
        txtItem.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CadastroProdutoFinan();
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