package br.com.teste.ui;

import br.com.teste.api.PedidoApi;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManutencaoPedidoForm {

    private static PedidoApi pedidoApi = new PedidoApi();
    private static JFrame frame;
    private static JTable table;
    private static DefaultTableModel tableModel;
    private static JTextField quantidadeField;
    private static JButton editarButton;
    private static JButton deletarButton;
    private static long selectedPedidoId = -1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Manutenção de Pedidos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLayout(new BorderLayout());

            // Table Model
            String[] columnNames = {"ID", "Cliente", "Produto", "Quantidade"};
            tableModel = new DefaultTableModel(columnNames, 0);
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Add ListSelectionListener to table
            table.getSelectionModel().addListSelectionListener(new PedidoTableSelectionListener());

            // Bottom Panel
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout());

            quantidadeField = new JTextField(10);
            bottomPanel.add(new JLabel("Quantidade:"));
            bottomPanel.add(quantidadeField);

            editarButton = new JButton("Editar");
            editarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editarQuantidade();
                }
            });
            bottomPanel.add(editarButton);

            deletarButton = new JButton("Deletar");
            deletarButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deletarPedido();
                }
            });
            bottomPanel.add(deletarButton);

            frame.add(bottomPanel, BorderLayout.SOUTH);

            // Load Data
            carregarDados();

            frame.setVisible(true);
        });
    }

    private static void carregarDados() {
        try {
            JSONArray pedidos = pedidoApi.obterPedidos();
            tableModel.setRowCount(0);

            for (int i = 0; i < pedidos.length(); i++) {
                JSONObject pedido = pedidos.getJSONObject(i);
                long id = pedido.getLong("id");
                String cliente = pedido.getJSONObject("cliente").optString("nome", "N/A");
                String produto = pedido.getJSONObject("produto").optString("descricao", "N/A");
                int quantidade = pedido.optInt("quantidade", 0);
                tableModel.addRow(new Object[]{id, cliente, produto, quantidade});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Erro ao carregar pedidos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void editarQuantidade() {
        if (selectedPedidoId != -1) {
            try {
                int novaQuantidade = Integer.parseInt(quantidadeField.getText());
                boolean sucesso = pedidoApi.editarQuantidade(selectedPedidoId, novaQuantidade);
                if (sucesso) {
                    JOptionPane.showMessageDialog(frame, "Quantidade atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarDados();
                } else {
                    JOptionPane.showMessageDialog(frame, "Falha ao atualizar quantidade.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao editar quantidade: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Selecione um pedido para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void deletarPedido() {
        if (selectedPedidoId != -1) {
            try {
                boolean sucesso = pedidoApi.deletarPedido(selectedPedidoId);
                if (sucesso) {
                    JOptionPane.showMessageDialog(frame, "Pedido deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarDados();
                    selectedPedidoId = -1;
                } else {
                    JOptionPane.showMessageDialog(frame, "Falha ao deletar pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao deletar pedido: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Selecione um pedido para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void setVisible(boolean b) {
    }

    private static class PedidoTableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                selectedPedidoId = (Long) table.getValueAt(selectedRow, 0);
                quantidadeField.setText(table.getValueAt(selectedRow, 3).toString());
            }
        }
    }
}
