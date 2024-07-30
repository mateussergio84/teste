package br.com.server.service;

import br.com.server.model.Cliente;
import br.com.server.model.Pedido;
import br.com.server.model.Produto;
import br.com.server.repository.PedidoRepository;
import br.com.server.repository.ClienteRepository;
import br.com.server.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }


    public Pedido getPedidoById(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElse(null);
    }

    public List<Pedido> getPedidosByFilters(LocalDate data, Boolean situacao) {
        return pedidoRepository.findByDataAndSituacao(data, situacao);
    }



    private BigDecimal calcularValorPedido(Pedido pedido) {
        // Obtenha o preço do produto e multiplique pela quantidade
        BigDecimal precoProduto = pedido.getProduto().getPreco();
        Integer quantidade = pedido.getQuantidade();
        return precoProduto.multiply(BigDecimal.valueOf(quantidade));
    }

    public Pedido createPedido(Pedido pedido) {
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Produto produto = produtoRepository.findById(pedido.getProduto().getCodigo())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        if (!validarCredito(cliente.getCodigo(), produto.getPreco(), pedido.getQuantidade())) {
            BigDecimal valorPedido = calcularValorPedido(pedido.getQuantidade(), produto.getPreco());
            BigDecimal totalPedidosDepoisFechamento = calcularTotalPedidosDepoisFechamento(cliente.getCodigo(), cliente.getDtFechamento());
            BigDecimal limiteDisponivel = cliente.getLimite().subtract(totalPedidosDepoisFechamento.add(valorPedido));
            throw new IllegalArgumentException(
                    String.format("Limite de crédito ultrapassado. Limite disponível: %s, Valor do pedido: %s",
                            limiteDisponivel, valorPedido));
        }

        if (verificarProdutoRepetido(cliente.getCodigo(), produto.getCodigo())) {
            throw new IllegalArgumentException("Produto já foi adicionado na venda. Altere em produtos se necessario");
        }

        pedido.setData(LocalDate.now());
        return pedidoRepository.save(pedido);
    }
    public Pedido updatePedido(Long id, Pedido pedidoDetails) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            Pedido existingPedido = pedido.get();
            existingPedido.setCliente(pedidoDetails.getCliente());
            existingPedido.setProduto(pedidoDetails.getProduto());
            existingPedido.setData(pedidoDetails.getData());
            existingPedido.setQuantidade(pedidoDetails.getQuantidade());
            return pedidoRepository.save(existingPedido);
        }
        return null;
    }

    public boolean deletePedido(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            pedidoRepository.delete(pedido.get());
            return true;
        }
        return false;
    }

    private BigDecimal calcularTotalPedidosDepoisFechamento(Long clienteId, Integer dataFechamento) {
        List<Pedido> pedidosDepoisFechamento = pedidoRepository.findPedidosDepoisFechamento(clienteId, LocalDate.ofYearDay(LocalDate.now().getYear(), dataFechamento));

        BigDecimal total = BigDecimal.ZERO;
        for (Pedido pedido : pedidosDepoisFechamento) {
            total = total.add(calcularValorPedido(pedido.getQuantidade(), pedido.getProduto().getPreco()));
        }
        return total;
    }

    private BigDecimal calcularValorPedido(Integer quantidade, BigDecimal preco) {
        BigDecimal valorPedido = BigDecimal.ZERO;
        for (int i = 0; i < quantidade; i++) {
            valorPedido = valorPedido.add(preco);
        }
        return valorPedido;
    }

    public boolean validarCredito(Long clienteId, BigDecimal precoProduto, Integer quantidade) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        BigDecimal totalPedidosDepoisFechamento = calcularTotalPedidosDepoisFechamento(clienteId, cliente.getDtFechamento());
        BigDecimal valorPedido = calcularValorPedido(quantidade, precoProduto);
        return totalPedidosDepoisFechamento.add(valorPedido).compareTo(cliente.getLimite()) <= 0;
    }

    public boolean verificarProdutoRepetido(Long clienteId, Long produtoId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        List<Pedido> pedidosExistentes = pedidoRepository.findByClienteAndProduto(cliente, produto);
        return !pedidosExistentes.isEmpty();
    }

    public List<Map<String, Object>> getPedidosAgrupadosPorCliente() {
        return pedidoRepository.findTotalValorPorCliente();
    }

    public List<Map<String, Object>> getPedidosAgrupadosPorProduto() {
        return pedidoRepository.findTotalValorPorProduto();
    }







}
