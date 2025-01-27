package com.nathanlucas.nscatalog.repositories;

import com.nathanlucas.nscatalog.entities.Product;
import com.nathanlucas.nscatalog.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"categories"})
    @Query(value = "SELECT obj FROM Product obj WHERE UPPER(obj.name) LIKE UPPER(CONCAT('%',:name,'%'))")
    Page<Product> searchAll(String name, Pageable pageable);

    @SuppressWarnings("SqlNoDataSourceInspection")
    @Query(nativeQuery = true, value = """
            SELECT DISTINCT tb_product.id, tb_product.name FROM tb_product
            INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
            WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND (UPPER(tb_product.name) LIKE UPPER(CONCAT('%', :name, '%')))
            ORDER BY tb_product.name
            """, countQuery = """
            SELECT COUNT(*) FROM (
            SELECT DISTINCT tb_product.id, tb_product.name FROM tb_product
            INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
            WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND (UPPER(tb_product.name) LIKE UPPER(CONCAT('%', :name, '%')))
            ORDER BY tb_product.name) AS tb_result
            """)
    Page<ProductProjection> searchProducts(String name, Pageable pageable, List<Long> categoryIds);
}
