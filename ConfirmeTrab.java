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

public class ConfirmeTrab extends JFrame {
    private JFrame frame;
    private JTable table;
    private DatabaseConnector databaseConnector;
    private Connection connection;
    private JTextField textFieldIdInscricao;

    public ConfirmeTrab() {
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

        JLabel labelTitulo = new JLabel("Lista de Trabalhos Inscritos:");
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
        JLabel labelIdInscricao = new JLabel("ID da incrição do trabalho a ser concluído:");
        textFieldIdInscricao = new JTextField(10);
        JButton buttonExcluir = new JButton("Concluir");
        buttonExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarConclusao();
            }
        });
        panel.add(labelIdInscricao);
        panel.add(textFieldIdInscricao);
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
            int idVol = getIdVolLogado(); // Obter o ID do vol logado do sistema de login
            String sql1 = "SELECT inscricao.numInscricao, trabalho.nome, trabalho.data, voluntario.nome FROM inscricao INNER JOIN trabalho ON inscricao.idTrab = trabalho.idTrab inner join voluntario on inscricao.idVol = voluntario.idVol WHERE inscricao.idVol = ?";
            PreparedStatement stat = connection.prepareStatement(sql1);
            stat.setInt(1, idVol);
            ResultSet resultSet = stat.executeQuery();

            // Criação do modelo de tabela com os dados do ResultSet
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID Inscrição");
            model.addColumn("Trabalho");
            model.addColumn("Data");
            model.addColumn("Nome Voluntário");

            while (resultSet.next()) {
                int idT = resultSet.getInt("numInscricao");
                String nomeT = resultSet.getString("trabalho.nome");
                Date data = resultSet.getDate("data");
                String nomeV = resultSet.getString("voluntario.nome");
                Object[] rowData = {idT, nomeT, data, nomeV};
                model.addRow(rowData);
            }

            table.setModel(model);
            // Defina as larguras desejadas para cada coluna
            int[] columnWidths = {100, 100, 100, 250};

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

    private void confirmarConclusao() {
        String idInscricao = textFieldIdInscricao.getText();
        if (idInscricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o ID da inscrição a ser concluída.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja concluir o Trabalho?");
        if (confirmacao == JOptionPane.YES_OPTION) {
            concluirTrabalho();
        }
    }

    private void concluirTrabalho() {
        String idInscricao = textFieldIdInscricao.getText();
        if (idInscricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o ID da inscrição a ser concluída.");
            return;
        }

        int idIns;
        try {
            idIns = Integer.parseInt(idInscricao);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID da inscrição deve ser um número inteiro.");
            return;
        }

        try {
            String sql = "SELECT COUNT(*) FROM inscricao WHERE confirme = true AND numInscricao = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idIns);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    if (count > 0) {
                        // Verificar se o trabalho já foi registrado pelo voluntário
                        String trabalhoConcluidoSql = "SELECT COUNT(*) FROM trabalhos_concluidos WHERE numInscricao = ?";
                        try (PreparedStatement trabalhoConcluidoStatement = connection.prepareStatement(trabalhoConcluidoSql)) {
                            trabalhoConcluidoStatement.setInt(1, idIns);
                            ResultSet trabalhoConcluidoResultSet = trabalhoConcluidoStatement.executeQuery();
                            trabalhoConcluidoResultSet.next(); // Mova para a primeira linha do ResultSet
                            int countt = trabalhoConcluidoResultSet.getInt(1);
                            if (countt > 0) {
                                JOptionPane.showMessageDialog(this, "Este trabalho já foi concluído anteriormente.");
                                return;
                            }
                        }
                        String updateSql = "UPDATE voluntario " +
                                "INNER JOIN inscricao ON inscricao.idVol = voluntario.idVol " +
                                "INNER JOIN trabalho ON trabalho.idTrab = inscricao.idTrab " +
                                "SET voluntario.saldo = COALESCE(voluntario.saldo, 0) + trabalho.pontos " +
                                "WHERE inscricao.numInscricao = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                            updateStatement.setInt(1, idIns);
                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                String insertTrabalhoConcluidoSql = "INSERT INTO trabalhos_concluidos (numInscricao) VALUES (?)";
                                try (PreparedStatement insertTrabalhoConcluidoStatement = connection.prepareStatement(insertTrabalhoConcluidoSql)) {
                                    insertTrabalhoConcluidoStatement.setInt(1, idIns);
                                    JOptionPane.showMessageDialog(this, "Trabalho concluído com sucesso.");
                                    loadTableData();
                                    limparCampoIdInscricao();
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "Falha ao concluir o trabalho. Verifique o ID da inscrição.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "A inscrição não foi concluída pela ONG.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao concluir o trabalho.");
        }
    }

    private void limparCampoIdInscricao() {
        textFieldIdInscricao.setText("");
    }

    private int getIdVolLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdVolLogado();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ConfirmeTrab();
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
