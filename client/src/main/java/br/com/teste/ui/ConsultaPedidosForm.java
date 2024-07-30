package br.com.teste.ui;

import br.com.teste.api.PedidoApi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConsultaPedidosForm extends JFrame {

    private JTable pedidosTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private DefaultTableModel tableModel;
    private JComboBox<String> viewComboBox;
    private JComboBox<String> clienteFilter;
    private JComboBox<String> produtoFilter;
    private JComboBox<String> situacaoFilter;
    private JTextField dataFilterStart;
    private JTextField dataFilterEnd;

    public ConsultaPedidosForm() {
        setTitle("Consulta de Pedidos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        criarComponentes();
        atualizarTabela();
    }

    private void criarComponentes() {
        tableModel = new DefaultTableModel(
                new Object[]{"Código", "Nome", "Total Valor", "Data", "Situação"},
                0
        );
        pedidosTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        pedidosTable.setRowSorter(rowSorter);
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        viewComboBox = new JComboBox<>(new String[]{"Por Cliente", "Por Produto"});
        viewComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarTabela();
            }
        });
        topPanel.add(new JLabel("Visualização:"), gbc);
        gbc.gridx = 1;
        topPanel.add(viewComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Data Início:"), gbc);
        gbc.gridx = 1;
        dataFilterStart = new JTextField(10);
        topPanel.add(dataFilterStart, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Data Fim:"), gbc);
        gbc.gridx = 3;
        dataFilterEnd = new JTextField(10);
        topPanel.add(dataFilterEnd, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        clienteFilter = new JComboBox<>(new String[]{"Todos"});
        clienteFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarFiltros();
            }
        });
        topPanel.add(clienteFilter, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 3;
        produtoFilter = new JComboBox<>(new String[]{"Todos"});
        produtoFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarFiltros();
            }
        });
        topPanel.add(produtoFilter, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        topPanel.add(new JLabel("Situação:"), gbc);
        gbc.gridx = 1;
        situacaoFilter = new JComboBox<>(new String[]{"Todos", "Ativo", "Inativo"});
        situacaoFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aplicarFiltros();
            }
        });
        topPanel.add(situacaoFilter, gbc);

        add(topPanel, BorderLayout.NORTH);
        pack();
    }

    private void aplicarFiltros() {
        String cliente = (String) clienteFilter.getSelectedItem();
        String produto = (String) produtoFilter.getSelectedItem();
        String situacao = (String) situacaoFilter.getSelectedItem();
        String dataInicio = dataFilterStart.getText();
        String dataFim = dataFilterEnd.getText();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!"Todos".equals(cliente)) {
            filters.add(RowFilter.regexFilter(getRegexForFilter(cliente), 1));
        }
        if (!"Todos".equals(produto)) {
            filters.add(RowFilter.regexFilter(getRegexForFilter(produto), 2));
        }
        if (!"Todos".equals(situacao)) {
            filters.add(RowFilter.regexFilter(getRegexForFilter(situacao), 4));
        }
        if (!dataInicio.isEmpty() || !dataFim.isEmpty()) {
            filters.add(RowFilter.regexFilter(getRegexForDateRange(dataInicio, dataFim), 3));
        }

        RowFilter<Object, Object> combinedFilter = RowFilter.andFilter(filters);
        rowSorter.setRowFilter(combinedFilter);

        tableModel.fireTableDataChanged();
    }

    private String getRegexForDateRange(String start, String end) {
        if (start.isEmpty() && end.isEmpty()) {
            return ".*";
        } else if (!start.isEmpty() && end.isEmpty()) {
            return start;
        } else if (start.isEmpty()) {
            return end;
        } else {
            return start + "|" + end;
        }
    }

    private String getRegexForFilter(String filter) {
        return filter.equals("Todos") ? ".*" : filter;
    }

    private void atualizarTabela() {
        try {
            PedidoApi pedidoApi = new PedidoApi();
            JSONArray pedidos;
            if ("Por Cliente".equals(viewComboBox.getSelectedItem())) {
                pedidos = pedidoApi.obterPedidosAgrupadosPorCliente();
            } else {
                pedidos = pedidoApi.obterPedidosAgrupadosPorProduto();
            }

            tableModel.setRowCount(0);

            for (int i = 0; i < pedidos.length(); i++) {
                JSONObject pedido = pedidos.getJSONObject(i);

                Object codigo;
                String nome;
                Double totalValor = pedido.optDouble("total_valor", 0.0);
                String data = pedido.optString("data_pedido", "Desconhecido");
                String situacao = pedido.optString("situacao_pedido", "Desconhecido");

                if ("Por Cliente".equals(viewComboBox.getSelectedItem())) {
                    codigo = pedido.optLong("cliente_codigo", -1);
                    nome = pedido.optString("cliente_nome", "Desconhecido");
                } else {
                    codigo = pedido.optLong("produto_codigo", -1);
                    nome = pedido.optString("produto_descricao", "Desconhecido");
                }

                tableModel.addRow(new Object[]{
                        codigo,
                        nome,
                        totalValor,
                        data,
                        situacao
                });
            }

            aplicarFiltros();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConsultaPedidosForm().setVisible(true));
    }
}
