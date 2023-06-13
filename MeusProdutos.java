import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.TableColumn;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class MeusProdutos extends JFrame {
    private JFrame frame;
    private JTable table;
    private DatabaseConnector databaseConnector;
    private Connection connection;

    public MeusProdutos() {
        initialize();
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();
        loadTableData();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Definir tela cheia

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

        JLabel labelTitulo = new JLabel("Lista de Produtos Adquiridos:");
        constraints.gridx = 0;
        constraints.gridy = 2; // Posição abaixo do logo e do título
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Criação da tabela
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        constraints.gridx = 0;
        constraints.gridy = 4; // Posição abaixo da parte de exclusão
        constraints.gridwidth = 2;
        contentPanel.add(scrollPane, constraints);

        JButton buttonVoltar = new JButton("Voltar");
        buttonVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InterfaceVol();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 5; // Posição abaixo da tabela
        constraints.gridwidth = 2;
        contentPanel.add(buttonVoltar, constraints);

        frame.add(contentPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadTableData() {
        try (Statement statement = connection.createStatement()) {
            int idFinan = getIdVoluntarioLogado(); // Obter o ID do financiador logado do sistema de login
            String sql1 = "SELECT produto.nome, produto.item FROM produto inner join posse on posse.idProd = produto.idProd inner join voluntario on voluntario.idVol = posse.idVol where voluntario.idVol = ?";
            PreparedStatement stat = connection.prepareStatement(sql1);
            stat.setInt(1, idFinan);
            ResultSet resultSet = stat.executeQuery();

            // Criação do modelo de tabela com os dados do ResultSet
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Nome");
            model.addColumn("Produto");

            while (resultSet.next()) {
                String nome = resultSet.getString("produto.nome");
                String item = resultSet.getString("produto.item");

                Object[] rowData = { nome, item };
                model.addRow(rowData);
            }

            table.setModel(model);
            // Defina as larguras desejadas para cada coluna
            int[] columnWidths = { 100, 150, 300, 100};

            // Ajuste a largura das colunas
            for (int i = 0; i < table.getColumnCount(); i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidths[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao obter os dados da tabela.");
        }
    }

    private int getIdVoluntarioLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdVolLogado();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MeusProdutos();
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
