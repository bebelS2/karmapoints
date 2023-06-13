import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastroOpt extends JFrame {
    private JFrame frame;

    public CadastroOpt() {
        frame = new JFrame();
        frame.setTitle("Tipos de Cadastro");
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
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            contentPanel.add(labelLogo, constraints);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel labelTitulo = new JLabel("Escolha um dos tipos de entidade para se cadastrar no KarmaPoints:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        contentPanel.add(labelTitulo, constraints);

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Painel dos botões
        JPanel buttonPanel = new JPanel();

        // Botão 1 (ONG)
        JButton button1 = new JButton("Cadastro ONG");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela atual
                new CadastroONG();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        buttonPanel.add(button1, constraints);

        // Botão 2 (Voluntário)
        JButton button2 = new JButton("Cadastro Voluntário");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela atual
                new CadastroVol();
            }
        });
        buttonPanel.add(button2);

        // Botão 3 (Financiador Externo)
        JButton button3 = new JButton("Cadastro Financiador Externo");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela atual
                new CadastroFinan();
            }
        });
        buttonPanel.add(button3);

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
                new CadastroOpt();
            }
        });
    }
}

class CadastroONG extends JFrame {
    public CadastroONG() {
        // Configurações básicas da janela
        setTitle("Cadastro ONG");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));

        // Resto da implementação do cadastro de ONG
    }
}

class CadastroVol extends JFrame {
    public CadastroVol() {
        // Configurações básicas da janela
        setTitle("Cadastro Voluntário");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));

        // Resto da implementação do cadastro de voluntário
    }
}

class CadastroFinan extends JFrame {
    public CadastroFinan() {
        // Configurações básicas da janela
        setTitle("Cadastro Financiador Externo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(400, 300));

        // Resto da implementação do cadastro de financiador externo
    }
}

class BackgroundPanel extends JPanel {
    private BufferedImage background;

    public BackgroundPanel(BufferedImage background) {
        this.background = background;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
