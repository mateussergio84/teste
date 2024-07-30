package br.com.server.service;

import br.com.server.model.Produto;
import br.com.server.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> getAllProdutos() {
        return produtoRepository.findAll();
    }

    public Produto getProdutoById(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public Produto createProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto updateProduto(Long id, Produto produtoDetails) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isPresent()) {
            Produto existingProduto = produto.get();
            existingProduto.setDescricao(produtoDetails.getDescricao());
            existingProduto.setPreco(produtoDetails.getPreco());
            return produtoRepository.save(existingProduto);
        }
        return null;
    }

    public boolean deleteProduto(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isPresent()) {
            produtoRepository.delete(produto.get());
            return true;
        }
        return false;
    }
}
