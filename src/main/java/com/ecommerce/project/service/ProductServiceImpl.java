package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Product product = modelMapper.map(productDTO, Product.class);
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
            new ResourceNotFoundException("Category not Found")
        );

        product.setCategory(category);
        double specialPrice = product.getPrice() - (product.getPrice() *(product.getDiscount() * 0.01));
        product.setSpecialPrice(specialPrice);
        product.setImage("default.png");
        Product productSaved = productRepository.save(product);
        return modelMapper.map(productSaved, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product,ProductDTO.class)
        ).toList();
        ProductResponse repsonse = new ProductResponse();
        repsonse.setContent(productDTOS);
        return repsonse;
    }

    @Override
    public ProductResponse searchProductByCategoryId(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category not Found")
        );
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product,ProductDTO.class)
        ).toList();
        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        return response;

    }

    @Override
    public ProductResponse searchProductBykeyword(String keyword) {


        List<Product> products = productRepository.findByProductNameLikeIgnoreCase("%" +keyword+"%");

        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product,ProductDTO.class)
        ).toList();
        ProductResponse response = new ProductResponse();
        response.setContent(productDTOS);
        return response;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        Product product = modelMapper.map(productDTO, Product.class);
        Product productFound = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product not FOund !"));

        productFound.setProductName(product.getProductName());
        productFound.setDescription(product.getDescription());
        productFound.setQuantity(product.getQuantity());
        productFound.setDiscount(product.getDiscount());
        productFound.setPrice(product.getPrice());
        productFound.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFound);
        return modelMapper.map(savedProduct,ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product to be Deleted not found!"));
        productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }
}
