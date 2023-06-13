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

public class TrocaProd extends JFrame {
    private JFrame frame;
    private JTable table;
    private DatabaseConnector databaseConnector;
    private Connection connection;
    private JTextField textFieldIdProd;
    private JLabel labelSaldoValor;

    public TrocaProd() {
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();
        initialize();
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

        JLabel labelTitulo = new JLabel("Lista de Produtos Disponíveis:");
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
        constraints.fill = GridBagConstraints.BOTH; // Adicione esta linha para preencher todo o espaço disponível
        constraints.weightx = 1.0; // Adicione esta linha para preencher todo o espaço disponível
        constraints.weighty = 1.0; // Adicione esta linha para preencher todo o espaço disponível
        contentPanel.add(scrollPane, constraints);

        // Criação da caixa de texto e botões
        JPanel panel = new JPanel(new FlowLayout());
        JLabel labelIdProd = new JLabel("ID do Produto desejado:");
        textFieldIdProd = new JTextField(10);
        JButton buttonExcluir = new JButton("Adquirir");
        buttonExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                comprarProduto();
            }
        });
        panel.add(labelIdProd);
        panel.add(textFieldIdProd);
        panel.add(buttonExcluir);
        constraints.gridx = 0;
        constraints.gridy = 6; // Posição abaixo da tabela
        constraints.gridwidth = 2;
        contentPanel.add(panel, constraints);

        JLabel labelSaldo = new JLabel("Saldo do Usuário: ");
        constraints.gridx = 0;
        constraints.gridy = 8; // Posição abaixo do painel anterior
        contentPanel.add(labelSaldo, constraints);

        labelSaldoValor = new JLabel(); // Altere esta linha
        constraints.gridx = 8;
        constraints.gridy = 8; // Posição ao lado do labelSaldo
        contentPanel.add(labelSaldoValor, constraints);
        int idVol = getIdVolLogado();
        String sql3 = "SELECT saldo FROM voluntario where idVol = ?";
        try (PreparedStatement stat1 = connection.prepareStatement(sql3)) {
            stat1.setInt(1, idVol);
            ResultSet resultS = stat1.executeQuery();

            if (resultS.next()) {
                int saldo = resultS.getInt("saldo");
                labelSaldoValor.setText(Integer.toString(saldo));
            } else {
                // Caso o resultado esteja vazio, você pode tratar essa situação aqui
                // Por exemplo, exibir uma mensagem de erro ou definir um valor padrão para o saldo
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Trate a exceção de acordo com o que deseja fazer em caso de erro de consulta
        }

        frame.add(contentPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadTableData() {
        // Consultar os dados do banco de dados
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM produto");

            // Criação do modelo da tabela
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Nome");
            tableModel.addColumn("Preço");

            // Preencher a tabela com os dados do banco de dados
            while (resultSet.next()) {
                String id = resultSet.getString("idProd");
                String nome = resultSet.getString("nome");
                String preco = resultSet.getString("valor");
                tableModel.addRow(new String[]{id, nome, preco});
            }

            // Definir o modelo da tabela
            table.setModel(tableModel);

            // Redimensionar colunas para ajustar o conteúdo
            TableColumn column;
            for (int i = 0; i < table.getColumnCount(); i++) {
                column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(150);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void comprarProduto() {
        String idProdText = textFieldIdProd.getText();
        try {
            int idProd = Integer.parseInt(idProdText);
            int idVol = getIdVolLogado();
            String sql4 = "SELECT saldo FROM voluntario where idVol = ?";
            try (PreparedStatement stat2 = connection.prepareStatement(sql4)) {
                stat2.setInt(1, idVol);
                ResultSet resultSet = stat2.executeQuery();

                if (resultSet.next()) {
                    int saldo = resultSet.getInt("saldo");
                    String sql2 = "SELECT valor FROM produto WHERE idProd = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
                        preparedStatement.setInt(1, idProd);
                        ResultSet resultSe = preparedStatement.executeQuery();

                        if (resultSe.next()) {
                            int valor = resultSe.getInt("valor");

                            if (saldo > valor) {
                                String sql = "INSERT INTO posse (idVol, idProd) VALUES (?, ?)";
                                try (PreparedStatement statement2 = connection.prepareStatement(sql)) {
                                    statement2.setInt(1, idVol);
                                    statement2.setInt(2, idProd);
                                    int rowsAffected = statement2.executeUpdate();

                                    if (rowsAffected > 0) {
                                        String updateSql = "UPDATE voluntario " +
                                                "INNER JOIN posse ON posse.idVol = voluntario.idVol " +
                                                "INNER JOIN produto ON produto.idProd = posse.idProd " +
                                                "SET voluntario.saldo = voluntario.saldo - produto.valor " +
                                                "WHERE posse.idVol = ? and produto.idProd = ?";
                                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                                            updateStatement.setInt(1, idVol);
                                            updateStatement.setInt(2, idProd);
                                            updateStatement.executeUpdate();

                                            // Atualizar o saldo na interface
                                            String saldoSql = "SELECT saldo FROM voluntario where idVol = ?";
                                            try (PreparedStatement saldoStatement = connection.prepareStatement(saldoSql)) {
                                                saldoStatement.setInt(1, idVol);
                                                ResultSet saldoResult = saldoStatement.executeQuery();

                                                if (saldoResult.next()) {
                                                    int saldoAtualizado = saldoResult.getInt("saldo");
                                                    labelSaldoValor.setText(Integer.toString(saldoAtualizado));
                                                }
                                            }

                                            JOptionPane.showMessageDialog(frame, "Produto adquirido com sucesso.");
                                            loadTableData();
                                            limparCampoIdProd();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            JOptionPane.showMessageDialog(frame, "Erro ao adquirir o produto.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "O ID do Produto deve ser um número inteiro.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao adquirir o produto.");
        }
    }

    private void limparCampoIdProd() {
        textFieldIdProd.setText("");
    }

    private int getIdVolLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdVolLogado();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TrocaProd();
            }
        });
    }
}
