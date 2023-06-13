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

public class MenuProdutos extends JFrame {
    private JFrame frame;
    private JTable table;
    private DatabaseConnector databaseConnector;
    private Connection connection;
    private JTextField textFieldIdProd;

    public MenuProdutos() {
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

        JLabel labelTitulo = new JLabel("Lista de Produtos Cadastrados:");
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

        // Criação da caixa de texto e botões
        JPanel panel = new JPanel(new FlowLayout());
        JLabel labelIdProd = new JLabel("ID do Produto a ser excluído:");
        textFieldIdProd = new JTextField(10);
        JButton buttonExcluir = new JButton("Excluir");
        buttonExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarExclusao();
            }
        });
        panel.add(labelIdProd);
        panel.add(textFieldIdProd);
        panel.add(buttonExcluir);
        constraints.gridx = 0;
        constraints.gridy = 3; // Posição abaixo do título
        constraints.gridwidth = 2;
        contentPanel.add(panel, constraints);

        JButton buttonVoltar = new JButton("Voltar");
        buttonVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InterfaceFinan();
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
            int idFinan = getIdFinanciadorLogado(); // Obter o ID do financiador logado do sistema de login
            String sql1 = "SELECT * FROM produto where idFinan = ?";
            PreparedStatement stat = connection.prepareStatement(sql1);
            stat.setInt(1, idFinan);
            ResultSet resultSet = stat.executeQuery();

            // Criação do modelo de tabela com os dados do ResultSet
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nome");
            model.addColumn("Descrição");
            model.addColumn("Valor");

            while (resultSet.next()) {
                int id = resultSet.getInt("idProd");
                String nome = resultSet.getString("nome");
                String descricao = resultSet.getString("descricao");
                int valor = resultSet.getInt("valor");

                Object[] rowData = { id, nome, descricao, valor };
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

    private void confirmarExclusao() {
        String idProdText = textFieldIdProd.getText();
        if (idProdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o ID do Produto a ser excluído.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o Produto?");
        if (confirmacao == JOptionPane.YES_OPTION) {
            excluirProduto();
        }
    }

    private void excluirProduto() {
        String idProdText = textFieldIdProd.getText();
        try {
            int idProd = Integer.parseInt(idProdText);

            String sql = "DELETE FROM produto WHERE idProd = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idProd);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Produto excluído com sucesso.");
                    loadTableData();
                    limparCampoIdProd();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhum produto encontrado com o ID informado.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID do Produto deve ser um número inteiro.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir o produto.");
        }
    }

    private void limparCampoIdProd() {
        textFieldIdProd.setText("");
    }

    private int getIdFinanciadorLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdFinanciadorLogado();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MenuProdutos();
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
