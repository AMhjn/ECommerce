package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Product product = modelMapper.map(productDTO, Product.class);

        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
            new ResourceNotFoundException("Category not Found")
        );

        // check if product already present or not
        boolean productAlreadyPresent = false;
        List<Product> products = category.getProducts();
        for (int i=0;i< products.size();i++){
            if(products.get(i).getProductName().equals(product.getProductName()) ){
                productAlreadyPresent = true;
                break;
            }
        }

        if(!productAlreadyPresent){
            product.setCategory(category);
            double specialPrice = product.getPrice() - (product.getPrice() *(product.getDiscount() * 0.01));
            product.setSpecialPrice(specialPrice);
            product.setImage("default.png");
            Product productSaved = productRepository.save(product);
            return modelMapper.map(productSaved, ProductDTO.class);
        }
        else{
            throw new APIException("Product Already present for the category!");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNUmber, Integer pageSize,String sortBy, String sortOrder) {

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();


        Pageable pageDetails = PageRequest.of(pageNUmber,pageSize,sortByandOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> products = pageProducts.getContent();
        if(products.isEmpty()){
            throw new APIException("No Products Found !!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product,ProductDTO.class)
        ).toList();
        ProductResponse repsonse = new ProductResponse();
        repsonse.setContent(productDTOS);
        repsonse.setPageNumber(pageProducts.getNumber());
        repsonse.setPageSize(pageSize);
        repsonse.setTotalElements(pageProducts.getTotalElements());
        repsonse.setTotalPages(pageProducts.getTotalPages());
        repsonse.setLastPage(pageProducts.isLast());
        return repsonse;
    }

    @Override
    public ProductResponse searchProductByCategoryId(Long categoryId, Integer pageNumber,
                                                     Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->
                new ResourceNotFoundException("Category not Found")
        );

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = pageProducts.getContent();
        if(products.isEmpty()){
            throw new APIException("No Products Found !!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product,ProductDTO.class)
        ).toList();
        ProductResponse repsonse = new ProductResponse();
        repsonse.setContent(productDTOS);
        repsonse.setPageNumber(pageProducts.getNumber());
        repsonse.setPageSize(pageSize);
        repsonse.setTotalElements(pageProducts.getTotalElements());
        repsonse.setTotalPages(pageProducts.getTotalPages());
        repsonse.setLastPage(pageProducts.isLast());
        return repsonse;

    }

    @Override
    public ProductResponse searchProductBykeyword(String keyword, Integer pageNumber,
                                                  Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByandOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByandOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase("%" +keyword+"%", pageDetails);
        List<Product> products = pageProducts.getContent();

        if(products.isEmpty()){
            throw new APIException("No Products Found !!!");
        }
        List<ProductDTO> productDTOS = products.stream().map(
                product -> modelMapper.map(product,ProductDTO.class)
        ).toList();
        ProductResponse repsonse = new ProductResponse();
        repsonse.setContent(productDTOS);
        repsonse.setPageNumber(pageProducts.getNumber());
        repsonse.setPageSize(pageSize);
        repsonse.setTotalElements(pageProducts.getTotalElements());
        repsonse.setTotalPages(pageProducts.getTotalPages());
        repsonse.setLastPage(pageProducts.isLast());
        return repsonse;
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

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productfromDB = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product not found !"));

        // upload image to server
        // get the file name of uplaoded image

//        path = "images/";
        String fileName = fileService.uploadImage(path, image);
        // updating the new file name to the product
        productfromDB.setImage(fileName);

        // save updated product
        productRepository.save(productfromDB);

        // return DTO, after mapping product to DTO
        return  modelMapper.map(productfromDB, ProductDTO.class);

    }


}
