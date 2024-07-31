package br.com.teste.ui;

import br.com.teste.api.PedidoApi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Filtro para Cliente
        String cliente = (String) clienteFilter.getSelectedItem();
        if (cliente != null && !"Todos".equals(cliente)) {
            filters.add(RowFilter.regexFilter(getRegexForFilter(cliente), 1)); // Coluna do nome do cliente
        }

        // Filtro para Produto
        String produto = (String) produtoFilter.getSelectedItem();
        if (produto != null && !"Todos".equals(produto)) {
            filters.add(RowFilter.regexFilter(getRegexForFilter(produto), 2)); // Coluna do nome do produto
        }

        // Filtro para Situação
        String situacao = (String) situacaoFilter.getSelectedItem();
        if (situacao != null && !"Todos".equals(situacao)) {
            filters.add(RowFilter.regexFilter(getRegexForFilter(situacao), 4)); // Coluna da situação
        }

        // Filtro para Data
        String dataInicio = dataFilterStart.getText();
        String dataFim = dataFilterEnd.getText();
        if (!dataInicio.isEmpty() || !dataFim.isEmpty()) {
            filters.add(RowFilter.regexFilter(getRegexForDateRange(dataInicio, dataFim), 3)); // Coluna da data
        }

        RowFilter<Object, Object> combinedFilter = RowFilter.andFilter(filters);
        rowSorter.setRowFilter(combinedFilter);
    }

    private String getRegexForDateRange(String start, String end) {
        String startDatePattern = start.isEmpty() ? "" : start;
        String endDatePattern = end.isEmpty() ? "" : end;
        if (startDatePattern.isEmpty() && endDatePattern.isEmpty()) {
            return ".*";
        } else if (!startDatePattern.isEmpty() && endDatePattern.isEmpty()) {
            return startDatePattern;
        } else if (startDatePattern.isEmpty()) {
            return endDatePattern;
        } else {
            return startDatePattern + "|" + endDatePattern;
        }
    }

    private String getRegexForFilter(String filter) {
        if (filter == null || "Todos".equals(filter)) {
            return ".*"; // Permitir todos os valores
        }
        return filter;
    }

    private void atualizarTabela() {
        try {
            PedidoApi pedidoApi = new PedidoApi();
            JSONArray pedidos;
            String view = (String) viewComboBox.getSelectedItem();

            if ("Por Cliente".equals(view)) {
                pedidos = pedidoApi.obterPedidosAgrupadosPorCliente();
                atualizarComboBox(clienteFilter, pedidos, "cliente_nome");
                produtoFilter.setEnabled(false);
            } else {
                pedidos = pedidoApi.obterPedidosAgrupadosPorProduto();
                atualizarComboBox(produtoFilter, pedidos, "produto_descricao");
                clienteFilter.setEnabled(false);
            }

            // Habilitar o JComboBox que não está ativo
            if ("Por Cliente".equals(view)) {
                produtoFilter.setEnabled(false);
                clienteFilter.setEnabled(true);
            } else {
                clienteFilter.setEnabled(false);
                produtoFilter.setEnabled(true);
            }

            tableModel.setRowCount(0);

            for (int i = 0; i < pedidos.length(); i++) {
                JSONObject pedido = pedidos.getJSONObject(i);

                Object codigo;
                String nome;
                Double totalValor = pedido.optDouble("total_valor", 0.0);
                String data = pedido.optString("data_pedido", "Desconhecido");
                String situacao = pedido.optBoolean("situacao_pedido", false) ? "Ativo" : "Inativo";

                if ("Por Cliente".equals(view)) {
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

            // Aplicar filtros após atualizar a tabela
            aplicarFiltros();
        } catch (Exception e) {
            e.printStackTrace(); // Adicione um tratamento de erro mais robusto
        }
    }

    private void atualizarComboBox(JComboBox<String> comboBox, JSONArray pedidos, String key) {
        // Limpar e adicionar a primeira opção
        comboBox.removeAllItems();
        comboBox.addItem("Todos");

        // Criar um conjunto para evitar duplicatas
        Set<String> items = new HashSet<>();
        for (int i = 0; i < pedidos.length(); i++) {
            JSONObject pedido = pedidos.getJSONObject(i);
            items.add(pedido.optString(key, "Desconhecido"));
        }

        // Adicionar itens ao comboBox
        for (String item : items) {
            comboBox.addItem(item);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConsultaPedidosForm().setVisible(true);
        });
    }
}
