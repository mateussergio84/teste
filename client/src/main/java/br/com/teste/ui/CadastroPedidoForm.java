package br.com.teste.ui;

import br.com.teste.api.PedidoApi;
import br.com.teste.api.ProdutoApi;
import br.com.teste.api.ClienteApi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CadastroPedidoForm extends JFrame {

    private JComboBox<String> clienteComboBox;
    private JTable produtosTable;
    private DefaultTableModel produtosTableModel;
    private JTextField quantidadeField;
    private JButton submitButton;

    public CadastroPedidoForm() {
        setTitle("Cadastro de Pedido de Compra");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Clientes
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        clienteComboBox = new JComboBox<>();
        add(clienteComboBox, gbc);
        carregarClientes();

        // Produtos
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Produtos:"), gbc);

        produtosTableModel = new DefaultTableModel(new String[]{"Código", "Produto", "Preço"}, 0);
        produtosTable = new JTable(produtosTableModel);
        JScrollPane scrollPane = new JScrollPane(produtosTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(scrollPane, gbc);

        // Quantidade
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Quantidade:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        quantidadeField = new JTextField(10);
        add(quantidadeField, gbc);

        // Enviar pedido
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        submitButton = new JButton("Adicionar Pedido");
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarPedido();
            }
        });

        // Carregar produtos
        carregarProdutos();
    }

    private void carregarClientes() {
        SwingWorker<List<String[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                ClienteApi clienteApi = new ClienteApi();
                return clienteApi.buscarClientes();
            }

            @Override
            protected void done() {
                try {
                    List<String[]> clientesArray = get();
                    clienteComboBox.removeAllItems();
                    if (clientesArray.isEmpty()) {
                        JOptionPane.showMessageDialog(CadastroPedidoForm.this, "Nenhum cliente encontrado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        for (String[] cliente : clientesArray) {
                            clienteComboBox.addItem(cliente[1]); // Nome do cliente
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CadastroPedidoForm.this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void carregarProdutos() {
        SwingWorker<List<String[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                ProdutoApi produtoApi = new ProdutoApi();
                return produtoApi.buscarProdutos();
            }

            @Override
            protected void done() {
                try {
                    List<String[]> produtos = get();
                    produtosTableModel.setRowCount(0);
                    for (String[] produto : produtos) {
                        produtosTableModel.addRow(new Object[]{produto[0], produto[1], produto[2]});
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CadastroPedidoForm.this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void adicionarPedido() {
        try {
            // Obter o cliente selecionado
            String clienteNome = (String) clienteComboBox.getSelectedItem();
            if (clienteNome == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obter o ID do cliente
            ClienteApi clienteApi = new ClienteApi();
            Long clienteId = clienteApi.buscarClientes().stream()
                    .filter(cliente -> cliente[1].equals(clienteNome))
                    .map(cliente -> Long.parseLong(cliente[0]))
                    .findFirst()
                    .orElse(null);

            if (clienteId == null) {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obter o produto selecionado e quantidade
            int selectedRow = produtosTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Long produtoId = Long.parseLong((String) produtosTableModel.getValueAt(selectedRow, 0));
            Integer quantidade;
            try {
                quantidade = Integer.parseInt(quantidadeField.getText());
                if (quantidade <= 0) {
                    throw new NumberFormatException("Quantidade deve ser maior que zero.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantidade deve ser um número válido e maior que zero.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Adicionar o pedido
            PedidoApi pedidoApi = new PedidoApi();
            boolean sucesso = pedidoApi.adicionarPedido(clienteId, produtoId, quantidade);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Pedido adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // Limpar campos
                clienteComboBox.setSelectedIndex(-1);
                produtosTable.clearSelection();
                quantidadeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao adicionar o pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " " + e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroPedidoForm().setVisible(true));
    }
}
