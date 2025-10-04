package com.example.auction.repo;

import com.example.auction.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findBySellerId(Long sellerId);

    @Query("SELECT p FROM Product p WHERE p.auction IS NOT NULL AND p.auction.active = true")
    List<Product> findProductsOnAuction();

    List<Product> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.auction IS NULL")
    List<Product> findProductsWithoutAuction();

    @Query("SELECT p FROM Product p WHERE p.auction.active = true AND p.auction.endTime < :time")
    List<Product> findProductsWithExpiringAuction(@Param("time") LocalDateTime time);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findAllCategories();

    @Query("SELECT p FROM Product p WHERE p.auction IS NOT NULL ORDER BY p.auction.currentPrice DESC LIMIT :limit")
    List<Product> findFeaturedProducts(@Param("limit") int limit);
}
