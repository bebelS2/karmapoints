import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class InterfaceFinan extends JFrame {
    private JFrame frame;

    public InterfaceFinan() {
        frame = new JFrame();
        frame.setTitle("Área do Financiador");
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
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            contentPanel.add(labelLogo, constraints);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel labelTitulo = new JLabel("Bem vindo, Financiador!");
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Painel dos botões
        JPanel buttonPanel = new JPanel();

        // Botão de Login
        JButton cadastroButton = new JButton("Cadastrar Produto");
        cadastroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela atual
                new CadastroProdutoFinan();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        buttonPanel.add(cadastroButton, constraints);

        // Botão de Cadastro
        JButton Button = new JButton("Meus Produtos");
        Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela atual
                new MenuProdutos();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        buttonPanel.add(Button, constraints);
        
        JButton btnMainInterface = new JButton("Sair");
        btnMainInterface.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Fechar a janela atual
                new MainInterface(); // Abrir a interface principal
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 9;
        buttonPanel.add(btnMainInterface, constraints);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        contentPanel.add(mainPanel, constraints);
        frame.add(contentPanel);

        // Ajusta o tamanho da janela para que o conteúdo seja exibido corretamente
        frame.pack();

        // Centraliza a janela na tela
        frame.setLocationRelativeTo(null);

        // Exibe a janela
        frame.setResizable(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfaceFinan();
            }
        });
    }
}
