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

public class ConfirmeVol extends JFrame {
    private JFrame frame;
    private JTable table;
    private DatabaseConnector databaseConnector;
    private Connection connection;
    private JTextField textFieldConfirmar;
    private JTextField textFieldNegar;

    public ConfirmeVol() {
        initialize();
        databaseConnector = new DatabaseConnector();
        connection = databaseConnector.getConnection();
        loadTableData();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Definir tela cheia

        // Carregar imagem de plano de fundo
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background.png"));
            BackgroundPanel backgroundPanel = new BackgroundPanel(backgroundImage);
            frame.setContentPane(backgroundPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

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

        JLabel labelTitulo = new JLabel("Lista de inscrições em trabalhos:");
        constraints.gridx = 0;
        constraints.gridy = 1; // Posição abaixo do logo
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Criação da tabela
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        constraints.gridx = 0;
        constraints.gridy = 2; // Posição abaixo do título
        constraints.gridwidth = 2;
        contentPanel.add(scrollPane, constraints);

        // Criação da caixa de texto e botões
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.anchor = GridBagConstraints.WEST;
        panelConstraints.insets = new Insets(5, 5, 5, 5);

        JLabel labelConfirmar = new JLabel("ID da Inscrição que deseja confirmar presença de voluntário:");
        textFieldConfirmar = new JTextField(10);
        JButton bttnConfirmar = new JButton("Confirmar");
        bttnConfirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmarInscrição();
            }
        });

        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panel.add(labelConfirmar, panelConstraints);
        panelConstraints.gridx = 1;
        panel.add(textFieldConfirmar, panelConstraints);
        panelConstraints.gridx = 2;
        panel.add(bttnConfirmar, panelConstraints);

        JLabel labelNegar = new JLabel("ID da Inscrição que deseja negar presença de voluntário:");
        textFieldNegar = new JTextField(10);
        JButton bttnNegar = new JButton("Negar");
        bttnNegar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                negarInscricao();
            }
        });

        panelConstraints.gridx = 0;
        panelConstraints.gridy = 1;
        panel.add(labelNegar, panelConstraints);
        panelConstraints.gridx = 1;
        panel.add(textFieldNegar, panelConstraints);
        panelConstraints.gridx = 2;
        panel.add(bttnNegar, panelConstraints);

        constraints.gridx = 0;
        constraints.gridy = 3; // Posição abaixo da tabela
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
        constraints.gridy = 4; // Posição abaixo do painel de botões
        constraints.gridwidth = 2;
        contentPanel.add(buttonVoltar, constraints);

        frame.add(contentPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadTableData() {
        try (Statement statement = connection.createStatement()) {
            int idOng = getIdOngLogado(); // Obter o ID do ong logado do sistema de login
            String sql = "SELECT inscricao.numInscricao, trabalho.nome, trabalho.data, voluntario.nome FROM inscricao INNER JOIN trabalho ON inscricao.idTrab = trabalho.idTrab inner join voluntario on inscricao.idVol = voluntario.idVol WHERE trabalho.idOng = ? and inscricao.confirme is null";
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setInt(1, idOng);
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
            int[] columnWidths = {100, 100, 250};

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

    private void confirmarInscrição() {
        String idTrabText = textFieldConfirmar.getText();
        if (idTrabText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o ID da inscrição que quer confirmar.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja confirmar a presença do voluntário?");
        if (confirmacao == JOptionPane.YES_OPTION) {
            confirmeVol();
        }
    }

    private void negarInscricao() {
        String idTrabText = textFieldNegar.getText();
        if (idTrabText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o ID da inscrição que quer negar presença.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja negar a presença do voluntário?");
        if (confirmacao == JOptionPane.YES_OPTION) {
            negarVol();
        }
    }

    private void confirmeVol() {
        String idInscricao = textFieldConfirmar.getText();
        try {
            int idIns = Integer.parseInt(idInscricao);
            String sql1 = "UPDATE inscricao SET confirme = true WHERE numInscricao = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql1)) {
                statement.setInt(1, idIns);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Voluntário confirmado com sucesso.");
                    loadTableData();
                    limparCampoIdTrab();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhuma inscrição encontrada com o ID informado.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID da inscrição deve ser um número inteiro.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao confirmar presença.");
        }
    }

    private void negarVol() {
        String idInscricao = textFieldNegar.getText();
        try {
            int idIns = Integer.parseInt(idInscricao);
            String sql = "UPDATE inscricao SET confirme = false WHERE numInscricao = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idIns);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Voluntário negado com sucesso.");
                    loadTableData();
                    limparCampoIdTrab();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhuma inscrição encontrada com o ID informado.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID da inscrição deve ser um número inteiro.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao confirmar presença.");
        }
    }

    private void limparCampoIdTrab() {
        textFieldConfirmar.setText("");
        textFieldNegar.setText("");
    }
 
    private int getIdOngLogado() {
        UserData userData = UserData.getInstance();
        return userData.getIdOngLogado();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ConfirmeVol();
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
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}
