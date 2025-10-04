package com.example.auction.services;

import com.example.auction.dto.ProductDto;
import com.example.auction.models.Product;
import com.example.auction.models.User;
import com.example.auction.repo.ProductRepository;
import com.example.auction.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepository;
    private final AuctionService auctionService;

    public ProductService(ProductRepository productRepo,
                          UserRepository userRepository,
                          AuctionService auctionService) {
        this.productRepo = productRepo;
        this.userRepository = userRepository;
        this.auctionService = auctionService;
    }

    public List<ProductDto> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getActiveAuctionProducts() {
        return productRepo.findProductsOnAuction().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsBySeller(Long sellerId) {
        return productRepo.findBySellerId(sellerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return convertToDto(product);
    }

    public ProductDto createProduct(ProductDto dto, Long sellerId) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + sellerId));


        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setStartPrice(dto.getStartPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepo.save(product);


        if (dto.getAuctionDurationMinutes() != null && dto.getAuctionDurationMinutes() > 0) {
            auctionService.createAuctionForProduct(savedProduct, dto.getAuctionDurationMinutes());
        }

        return convertToDto(savedProduct);
    }

    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));


        if (dto.getTitle() != null) {
            product.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            product.setCategory(dto.getCategory());
        }
        if (dto.getImageUrl() != null) {
            product.setImageUrl(dto.getImageUrl());
        }

        Product updatedProduct = productRepo.save(product);
        return convertToDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepo.delete(product);
    }

    public List<ProductDto> searchProducts(String query) {
        return productRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsByCategory(String category) {
        return productRepo.findByCategory(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsWithoutAuction() {
        return productRepo.findProductsWithoutAuction().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductsWithExpiringAuctions() {
        return productRepo.findProductsWithExpiringAuction(LocalDateTime.now().plusMinutes(10)).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean isProductOwner(Long productId, Long userId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return product.getSeller().getId().equals(userId);
    }

    public ProductDto createAuctionForProduct(Long productId, Integer durationMinutes) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));


        if (product.getAuction() != null && product.getAuction().getActive()) {
            throw new RuntimeException("Active auction already exists for this product");
        }


        auctionService.createAuctionForProduct(product, durationMinutes);

        return convertToDto(product);
    }

    public List<String> getAllCategories() {
        return productRepo.findAllCategories();
    }

    public List<ProductDto> getFeaturedProducts(int limit) {
        return productRepo.findFeaturedProducts(limit).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setStartPrice(product.getStartPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setCreatedAt(product.getCreatedAt());


        if (product.getSeller() != null) {
            dto.setSellerId(product.getSeller().getId());
            dto.setSellerUsername(product.getSeller().getUsername());
        }


        if (product.getAuction() != null) {
            dto.setAuctionId(product.getAuction().getId());
            dto.setAuctionActive(product.getAuction().getActive());
            dto.setCurrentPrice(product.getAuction().getCurrentPrice());
            dto.setAuctionEndTime(product.getAuction().getEndTime());


            if (product.getAuction().getActive() && product.getAuction().getEndTime() != null) {
                long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), product.getAuction().getEndTime()).getSeconds();
                dto.setTimeRemainingSeconds(Math.max(0, secondsRemaining));
            }
        }

        return dto;
    }


    public ProductStatistics getProductStatistics(Long sellerId) {
        List<Product> sellerProducts = productRepo.findBySellerId(sellerId);

        ProductStatistics stats = new ProductStatistics();
        stats.setTotalProducts((long) sellerProducts.size());
        stats.setActiveAuctions(sellerProducts.stream()
                .filter(p -> p.getAuction() != null && p.getAuction().getActive())
                .count());
        stats.setCompletedAuctions(sellerProducts.stream()
                .filter(p -> p.getAuction() != null && !p.getAuction().getActive())
                .count());
        stats.setTotalRevenue(sellerProducts.stream()
                .filter(p -> p.getAuction() != null && !p.getAuction().getActive() && p.getAuction().getWinner() != null)
                .map(p -> p.getAuction().getCurrentPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return stats;
    }


    public static class ProductStatistics {
        private Long totalProducts;
        private Long activeAuctions;
        private Long completedAuctions;
        private BigDecimal totalRevenue;


        public Long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }

        public Long getActiveAuctions() { return activeAuctions; }
        public void setActiveAuctions(Long activeAuctions) { this.activeAuctions = activeAuctions; }

        public Long getCompletedAuctions() { return completedAuctions; }
        public void setCompletedAuctions(Long completedAuctions) { this.completedAuctions = completedAuctions; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    }
}