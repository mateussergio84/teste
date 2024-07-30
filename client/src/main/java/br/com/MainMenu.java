package br.com;

import br.com.teste.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Menu Principal");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.CENTER;

            gbc.gridx = 0;
            gbc.gridy = 0;

            JButton btnCadastrarCliente = new JButton("Cadastrar Cliente");
            JButton btnCadastrarProduto = new JButton("Cadastrar Produto");
            JButton btnCadastrarPedido = new JButton("Cadastrar Pedido");
            JButton btnConsultarPedido = new JButton("Consultar Pedido");
            JButton btnManutencaoPedido = new JButton("Manutenção Pedido");

            btnCadastrarCliente.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new CadastroClienteForm().setVisible(true);
                }
            });

            btnCadastrarProduto.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new CadastroProdutoForm().setVisible(true);
                }
            });

            btnCadastrarPedido.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new CadastroPedidoForm().setVisible(true);
                }
            });

            btnConsultarPedido.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ConsultaPedidosForm().setVisible(true);
                }
            });

            btnManutencaoPedido.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ManutencaoPedidoForm().setVisible(true);
                }
            });

            frame.add(btnCadastrarCliente, gbc);
            gbc.gridy++;
            frame.add(btnCadastrarProduto, gbc);
            gbc.gridy++;
            frame.add(btnCadastrarPedido, gbc);
            gbc.gridy++;
            frame.add(btnConsultarPedido, gbc);
            gbc.gridy++;
            frame.add(btnManutencaoPedido, gbc);

            frame.setVisible(true);
        });
    }
}
