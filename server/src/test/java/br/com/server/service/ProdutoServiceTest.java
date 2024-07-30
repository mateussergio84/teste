package br.com.server.service;

import br.com.server.model.Produto;
import br.com.server.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProdutos() {
        // Given
        when(produtoRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        var produtos = produtoService.getAllProdutos();

        // Then
        assertNotNull(produtos);
        assertTrue(produtos.isEmpty());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void testGetProdutoById() {
        // Given
        Produto produto = new Produto();
        produto.setCodigo(1L);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // When
        Produto result = produtoService.getProdutoById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getCodigo());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProdutoById_NotFound() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Produto result = produtoService.getProdutoById(1L);

        // Then
        assertNull(result);
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateProduto() {
        // Given
        Produto produto = new Produto();
        produto.setCodigo(1L);
        produto.setDescricao("Produto Teste");
        produto.setPreco(BigDecimal.valueOf(99.99));

        when(produtoRepository.save(produto)).thenReturn(produto);

        // When
        Produto result = produtoService.createProduto(produto);

        // Then
        assertNotNull(result);
        assertEquals(produto, result);
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void testUpdateProduto_Success() {
        // Given
        Produto existingProduto = new Produto();
        existingProduto.setCodigo(1L);
        existingProduto.setDescricao("Descrição Antiga");
        existingProduto.setPreco(BigDecimal.valueOf(50.00));

        Produto updatedProduto = new Produto();
        updatedProduto.setDescricao("Descrição Atualizada");
        updatedProduto.setPreco(BigDecimal.valueOf(75.00));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(existingProduto));
        when(produtoRepository.save(existingProduto)).thenReturn(existingProduto);

        // When
        Produto result = produtoService.updateProduto(1L, updatedProduto);

        // Then
        assertNotNull(result);
        assertEquals("Descrição Atualizada", result.getDescricao());
        assertEquals(BigDecimal.valueOf(75.00), result.getPreco());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(existingProduto);
    }

    @Test
    void testUpdateProduto_NotFound() {
        // Given
        Produto updatedProduto = new Produto();
        updatedProduto.setDescricao("Descrição Atualizada");
        updatedProduto.setPreco(BigDecimal.valueOf(75.00));

        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Produto result = produtoService.updateProduto(1L, updatedProduto);

        // Then
        assertNull(result);
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, never()).save(any());
    }

    @Test
    void testDeleteProduto_Success() {
        // Given
        Produto produto = new Produto();
        produto.setCodigo(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // When
        boolean result = produtoService.deleteProduto(1L);

        // Then
        assertTrue(result);
        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    void testDeleteProduto_NotFound() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = produtoService.deleteProduto(1L);

        // Then
        assertFalse(result);
        verify(produtoRepository, never()).delete(any());
    }
}
