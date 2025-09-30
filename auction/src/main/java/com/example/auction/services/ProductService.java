package com.example.auction.services;

import com.example.auction.dto.ProductDto;
import com.example.auction.models.Product;
import com.example.auction.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepo;

    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductDto> getAllProducts(){
        return productRepo.findAll().stream().map(p -> new ProductDto(p.getId(), p.getDescription(),p.getName()))
                .collect(Collectors.toList());
    }
}
