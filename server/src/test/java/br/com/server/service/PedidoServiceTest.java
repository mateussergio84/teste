package br.com.server.service;

import br.com.server.model.Cliente;
import br.com.server.model.Pedido;
import br.com.server.model.Produto;
import br.com.server.repository.ClienteRepository;
import br.com.server.repository.PedidoRepository;
import br.com.server.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPedidos() {
        // Given
        when(pedidoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Pedido> pedidos = pedidoService.getAllPedidos();

        // Then
        assertNotNull(pedidos);
        assertTrue(pedidos.isEmpty());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void testGetPedidoById() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setCodigo(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When
        Pedido result = pedidoService.getPedidoById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePedido_Success() {
        // Given
        Cliente cliente = new Cliente();
        cliente.setCodigo(1L);
        cliente.setLimite(BigDecimal.valueOf(1000));
        cliente.setDtFechamento(10);

        Produto produto = new Produto();
        produto.setCodigo(1L);
        produto.setPreco(BigDecimal.valueOf(100));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        pedido.setQuantidade(5);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        // When
        Pedido result = pedidoService.createPedido(pedido);

        // Then
        assertNotNull(result);
        assertEquals(pedido, result);
        verify(clienteRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testCreatePedido_ClientNotFound() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setCliente(new Cliente());
        pedido.getCliente().setCodigo(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            pedidoService.createPedido(pedido);
        });
        assertEquals("Cliente não encontrado.", thrown.getMessage());
        verify(clienteRepository, times(1)).findById(1L);
        verify(produtoRepository, never()).findById(anyLong());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void testUpdatePedido_Success() {
        // Given
        Pedido existingPedido = new Pedido();
        existingPedido.setCodigo(1L);
        Pedido updatedPedido = new Pedido();
        updatedPedido.setCliente(new Cliente());
        updatedPedido.setProduto(new Produto());
        updatedPedido.setData(LocalDate.now());
        updatedPedido.setQuantidade(10);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(existingPedido));
        when(pedidoRepository.save(existingPedido)).thenReturn(existingPedido);

        // When
        Pedido result = pedidoService.updatePedido(1L, updatedPedido);

        // Then
        assertNotNull(result);
        assertEquals(updatedPedido, result);
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(existingPedido);
    }

    @Test
    void testUpdatePedido_NotFound() {
        // Given
        Pedido updatedPedido = new Pedido();
        updatedPedido.setCliente(new Cliente());
        updatedPedido.setProduto(new Produto());
        updatedPedido.setData(LocalDate.now());
        updatedPedido.setQuantidade(10);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Pedido result = pedidoService.updatePedido(1L, updatedPedido);

        // Then
        assertNull(result);
        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void testDeletePedido_Success() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setCodigo(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When
        boolean result = pedidoService.deletePedido(1L);

        // Then
        assertTrue(result);
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    @Test
    void testDeletePedido_NotFound() {
        // Given
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = pedidoService.deletePedido(1L);

        // Then
        assertFalse(result);
        verify(pedidoRepository, never()).delete(any());
    }
}
