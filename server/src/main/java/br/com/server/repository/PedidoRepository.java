package br.com.server.repository;

import br.com.server.model.Cliente;
import br.com.server.model.Pedido;
import br.com.server.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteAndProduto(Cliente cliente, Produto produto);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.codigo = :clienteId AND p.data > :dataFechamento")
    List<Pedido> findPedidosDepoisFechamento(@Param("clienteId") Long clienteId, @Param("dataFechamento") LocalDate dataFechamento);

    List<Pedido> findByDataAndSituacao(LocalDate data, boolean situacao);


    @Query("SELECT c.codigo AS cliente_codigo, c.nome AS cliente_nome, " +
            "SUM(p.preco * pd.quantidade) AS total_valor, " +
            "pd.data AS data_pedido, pd.situacao AS situacao_pedido " +
            "FROM Pedido pd " +
            "JOIN pd.cliente c " +
            "JOIN pd.produto p " +
            "GROUP BY c.codigo, c.nome, pd.data, pd.situacao " +
            "ORDER BY c.codigo, pd.data")
    List<Map<String, Object>> findTotalValorPorCliente();




    @Query("SELECT p.codigo AS produto_codigo, p.descricao AS produto_descricao, " +
            "SUM(p.preco * pd.quantidade) AS total_valor, " +
            "pd.data AS data_pedido, pd.situacao AS situacao_pedido " +
            "FROM Pedido pd " +
            "JOIN pd.produto p " +
            "GROUP BY p.codigo, p.descricao, pd.data, pd.situacao " +
            "ORDER BY p.codigo, pd.data")
    List<Map<String, Object>> findTotalValorPorProduto();





}
