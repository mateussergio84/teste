package br.com.teste.ui;

import br.com.teste.api.ClienteApi;

import javax.swing.*;
import java.awt.*;


public class CadastroClienteForm extends JFrame {

    private JTextField nomeField;
    private JTextField limiteCompraField;
    private JComboBox<Integer> diaFechamentoComboBox;
    private JButton submitButton;

    public CadastroClienteForm() {
        setTitle("Cadastro de Cliente");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        createFormComponents(gbc);

        submitButton.addActionListener(e -> {
            try {
                handleSubmit();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar cadastro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createFormComponents(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        nomeField = new JTextField(15);
        add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Limite de Compra:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        limiteCompraField = new JTextField(15);
        add(limiteCompraField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Dia de Fechamento da Fatura:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;

        Integer[] dias = new Integer[28];
        for (int i = 1; i <= 28; i++) {
            dias[i - 1] = i;
        }
        diaFechamentoComboBox = new JComboBox<>(dias);
        add(diaFechamentoComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        submitButton = new JButton("Cadastrar");
        add(submitButton, gbc);
    }

    private void handleSubmit() throws Exception {
        String nome = nomeField.getText().trim();
        String limiteCompraText = limiteCompraField.getText().trim();
        Integer diaFechamento = (Integer) diaFechamentoComboBox.getSelectedItem();

        if (nome.isEmpty() || limiteCompraText.isEmpty() || diaFechamento == null) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double limiteCompra = Double.parseDouble(limiteCompraText);

            ClienteApi clienteApi = new ClienteApi();
            boolean success = clienteApi.cadastrarCliente(nome, limiteCompraText, diaFechamento.toString());

            if (success) {
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao cadastrar cliente.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Limite de compra deve ser um número válido.", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroClienteForm().setVisible(true));
    }
}
