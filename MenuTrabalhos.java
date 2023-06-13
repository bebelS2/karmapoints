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

public class MenuTrabalhos extends JFrame {
    private JFrame frame;
    private JTable table;
    private DatabaseConnector databaseConnector;
    private Connection connection;
    private JTextField textFieldIdTrab;

    public MenuTrabalhos() {
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

        JLabel labelTitulo = new JLabel("Lista de Trabalhos Cadastrados:");
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
        JLabel labelIdTrab = new JLabel("ID do Trabalho a ser excluído:");
        textFieldIdTrab = new JTextField(10);
        JButton buttonExcluir = new JButton("Excluir");
        buttonExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarExclusao();
            }
        });
        panel.add(labelIdTrab);
        panel.add(textFieldIdTrab);
        panel.add(buttonExcluir);
        constraints.gridx = 0;
        constraints.gridy = 3; // Posição abaixo do título
        constraints.gridwidth = 2;
        contentPanel.add(panel, constraints);

        JButton buttonVoltar = new JButton("Voltar");
        buttonVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new InterfaceOng();
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
            int idOng = getIdOngLogado(); // Obter o ID do ong logado do sistema de login
            String sql1 = "SELECT * FROM trabalho where idOng = ?";
            PreparedStatement stat = connection.prepareStatement(sql1);
            stat.setInt(1, idOng);
            ResultSet resultSet = stat.executeQuery();

            // Criação do modelo de tabela com os dados do ResultSet
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nome");
            model.addColumn("Data");
            model.addColumn("Horário");
            model.addColumn("Requisitos");
            model.addColumn("ODS");
            model.addColumn("Pontos");

            while (resultSet.next()) {
                int id = resultSet.getInt("idTrab");
                String nome = resultSet.getString("nome");
                Date data = resultSet.getDate("data");
                String horario = resultSet.getString("horario");
                String requisitos = resultSet.getString("requisitos");
                String ods = resultSet.getString("ods");
                int pontos = resultSet.getInt("pontos");

                Object[] rowData = { id, nome, data, horario, requisitos, ods, pontos };
                model.addRow(rowData);
            }

            table.setModel(model);
            // Defina as larguras desejadas para cada coluna
            int[] columnWidths = { 100, 150, 150, 100, 150, 150, 100 };

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
        String idTrabText = textFieldIdTrab.getText();
        if (idTrabText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o ID do Trabalho a ser excluído.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o Trabalho?");
        if (confirmacao == JOptionPane.YES_OPTION) {
            excluirTrabalho();
        }
    }

    private void excluirTrabalho() {
        String idTrabText = textFieldIdTrab.getText();
        try {
            int idTrab = Integer.parseInt(idTrabText);

            String sql = "DELETE FROM trabalho WHERE idTrab = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idTrab);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Trabalho excluído com sucesso.");
                    loadTableData();
                    limparCampoIdTrab();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhum trabalho encontrado com o ID informado.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID do Trabalho deve ser um número inteiro.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir o trabalho.");
        }
    }

    private void limparCampoIdTrab() {
        textFieldIdTrab.setText("");
    }

    private int getIdOngLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdOngLogado();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MenuTrabalhos();
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
