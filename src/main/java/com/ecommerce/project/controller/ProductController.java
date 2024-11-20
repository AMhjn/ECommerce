package com.ecommerce.project.controller;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO product,
                                                 @PathVariable Long categoryId){

        ProductDTO addedProductDTO = productService.addProduct(categoryId, product);
        return new ResponseEntity<>(addedProductDTO, HttpStatus.CREATED);

    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){

        ProductResponse productResponse = productService.getAllProducts();
        return  new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsInCategory(@PathVariable Long categoryId){

        ProductResponse productResponse = productService.searchProductByCategoryId(categoryId);
        return  new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword){

        ProductResponse productResponse = productService.searchProductBykeyword(keyword);
        return  new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,
                                                    @RequestBody ProductDTO product){

        ProductDTO updatedProductDTO = productService.updateProduct(productId,product);
        return  new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }


    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){

            ProductDTO deletedProductDTO = productService.deleteProduct(productId);
            return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);
    }
}