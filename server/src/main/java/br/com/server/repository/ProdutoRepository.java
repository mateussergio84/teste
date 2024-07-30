package br.com.server.repository;

import br.com.server.model.Cliente;
import br.com.server.model.Pedido;
import br.com.server.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;


public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT COALESCE(SUM(p.quantidade * p.produto.preco), 0) FROM Pedido p WHERE p.cliente.codigo = :clienteId AND p.data > :dataFechamento")
    BigDecimal sumPedidosAfterData(@Param("clienteId") Long clienteId, @Param("dataFechamento") LocalDate dataFechamento);

    @Query("SELECT p FROM Pedido p WHERE p.cliente = :cliente AND p.produto = :produto")
    Optional<Pedido> findByClienteAndProduto(@Param("cliente") Cliente cliente, @Param("produto") Produto produto);
}