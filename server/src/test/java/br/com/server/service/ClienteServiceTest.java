package br.com.server.service;

import br.com.server.model.Cliente;
import br.com.server.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllClientes() {
        // Given
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Cliente> clientes = clienteService.getAllClientes();

        // Then
        assertNotNull(clientes);
        assertTrue(clientes.isEmpty());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void testGetClienteById() {
        // Given
        Cliente cliente = new Cliente();
        cliente.setCodigo(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        Cliente result = clienteService.getClienteById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getCodigo());
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClienteById_NotFound() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Cliente result = clienteService.getClienteById(1L);

        // Then
        assertNull(result);
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateCliente() {
        // Given
        Cliente cliente = new Cliente();
        cliente.setCodigo(1L);
        cliente.setNome("Cliente Teste");

        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // When
        Cliente result = clienteService.createCliente(cliente);

        // Then
        assertNotNull(result);
        assertEquals(cliente, result);
        verify(clienteRepository, times(1)).save(cliente);
    }
}
