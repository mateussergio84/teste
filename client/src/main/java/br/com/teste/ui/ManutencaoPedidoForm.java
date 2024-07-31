package br.com.teste.ui;

import br.com.teste.api.PedidoApi;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManutencaoPedidoForm extends JFrame {

    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private JButton alterarQuantidadeButton;
    private JButton deletarPedidoButton;
    private PedidoApi pedidoApi;

    public ManutencaoPedidoForm() {
        pedidoApi = new PedidoApi();

        setTitle("Manutenção de Pedidos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        criarComponentes();
        carregarPedidos();
    }

    private void criarComponentes() {
        tableModel = new DefaultTableModel(
                new Object[]{"Código", "Cliente", "Produto", "Quantidade", "Data", "Situação"},
                0
        );
        pedidosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        alterarQuantidadeButton = new JButton("Alterar Quantidade");
        deletarPedidoButton = new JButton("Deletar Pedido");

        alterarQuantidadeButton.addActionListener(e -> alterarQuantidade());
        deletarPedidoButton.addActionListener(e -> deletarPedido());

        buttonPanel.add(alterarQuantidadeButton);
        buttonPanel.add(deletarPedidoButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void carregarPedidos() {
        try {
            JSONArray pedidos = pedidoApi.obterTodosPedidos();
            tableModel.setRowCount(0);

            for (int i = 0; i < pedidos.length(); i++) {
                JSONObject pedido = pedidos.getJSONObject(i);

                if (pedido.has("cliente") && pedido.get("cliente") instanceof JSONObject &&
                        pedido.has("produto") && pedido.get("produto") instanceof JSONObject) {

                    JSONObject cliente = pedido.getJSONObject("cliente");
                    JSONObject produto = pedido.getJSONObject("produto");

                    tableModel.addRow(new Object[]{
                            pedido.getInt("codigo"),
                            cliente.getString("nome"),
                            produto.getString("descricao"),
                            pedido.getInt("quantidade"),
                            pedido.getString("data"),
                            pedido.getBoolean("situacao") ? "Ativo" : "Inativo"
                    });
                } else {
                    System.out.println("Formato de JSON inesperado: " + pedido);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void alterarQuantidade() {
        int selectedRow = pedidosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para alterar a quantidade.");
            return;
        }

        int codigo = (int) tableModel.getValueAt(selectedRow, 0);
        String quantidadeStr = JOptionPane.showInputDialog(this, "Nova Quantidade:");
        if (quantidadeStr != null) {
            try {
                int quantidade = Integer.parseInt(quantidadeStr);
                pedidoApi.alterarQuantidadePedido(codigo, quantidade);
                carregarPedidos();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao alterar a quantidade: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deletarPedido() {
        int selectedRow = pedidosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para deletar.");
            return;
        }

        int codigo = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar o pedido?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pedidoApi.deletarPedido(codigo);
                carregarPedidos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar o pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManutencaoPedidoForm().setVisible(true));
    }
}
