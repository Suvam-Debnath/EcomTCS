package com.example.product.service;

import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    /* ---------------- CREATE PRODUCT ----------------*/

    @DisplayName("Create Product - Success")
    @Test
    void test_When_Create_Product_Success() {

        Product product = getMockProduct();

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product savedProduct = productService.createProduct(product);

        verify(productRepository, times(1)).save(any(Product.class));
        assertTrue(savedProduct.getActive());
        assertEquals("iPhone", savedProduct.getName());
    }

    /* ---------------- UPDATE PRODUCT ----------------*/

    @DisplayName("Update Product - Success")
    @Test
    void test_When_Update_Product_Success() {

        Product existingProduct = getMockProduct();
        existingProduct.setId(1L);

        Product updatedProduct = getMockProduct();
        updatedProduct.setName("iPhone Pro");

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenReturn(existingProduct);


        Optional<Product> result =
                productService.updateProduct(1L, updatedProduct);

        assertTrue(result.isPresent());
        assertEquals("iPhone Pro", result.get().getName());
        verify(productRepository).save(any(Product.class));
    }

    @DisplayName("Update Product - Not Found")
    @Test
    void test_When_Update_Product_Not_Found() {

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Optional<Product> result =
                productService.updateProduct(1L, getMockProduct());

        verify(productRepository, never()).save(any());
        assertTrue(result.isEmpty());
    }

    /* ---------------- GET ALL PRODUCTS ----------------*/

    @DisplayName("Get All Active Products - Success")
    @Test
    void test_When_Get_All_Products_Success() {

        when(productRepository.findByActiveTrue())
                .thenReturn(List.of(getMockProduct(), getMockProduct()));

        List<Product> products = productService.getAllProducts();

        verify(productRepository).findByActiveTrue();
        assertEquals(2, products.size());
    }

    /* ---------------- DELETE PRODUCT (SOFT DELETE) ----------------*/

    @DisplayName("Delete Product - Success (Soft Delete)")
    @Test
    void test_When_Delete_Product_Success() {

        Product product = getMockProduct();
        product.setId(1L);

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.of(product));

        boolean result = productService.deleteProduct(1L);

        verify(productRepository).save(product);
        assertFalse(product.getActive());
        assertTrue(result);
    }

    @DisplayName("Delete Product - Not Found")
    @Test
    void test_When_Delete_Product_Not_Found() {

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        boolean result = productService.deleteProduct(1L);

        verify(productRepository, never()).save(any());
        assertFalse(result);
    }

    /* ---------------- SEARCH PRODUCTS ----------------*/

    @DisplayName("Search Products - Success")
    @Test
    void test_When_Search_Product_Success() {

        when(productRepository.searchProducts(anyString()))
                .thenReturn(List.of(getMockProduct()));

        List<Product> products = productService.searchProducts("iphone");

        verify(productRepository).searchProducts(anyString());
        assertEquals(1, products.size());
    }

    /* ---------------- GET PRODUCT BY ID ----------------*/

    @DisplayName("Get Product By ID - Success")
    @Test
    void test_When_Get_Product_By_Id_Success() {

        when(productRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(getMockProduct()));

        Optional<Product> result =
                productService.getProductById("1");

        assertTrue(result.isPresent());
    }

    @DisplayName("Get Product By ID - Not Found")
    @Test
    void test_When_Get_Product_By_Id_Not_Found() {

        when(productRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());

        Optional<Product> result =
                productService.getProductById("1");

        assertTrue(result.isEmpty());
    }

    /* ---------------- MOCK HELPER ----------------*/

    private Product getMockProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("iPhone");
        product.setCategory("Mobile");
        product.setDescription("Apple Phone");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStockQuantity(10);
        product.setActive(true);
        product.setImageUrl("img.jpg");
        return product;
    }
}
