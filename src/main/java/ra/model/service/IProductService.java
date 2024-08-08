package ra.model.service;

import ra.model.entity.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Boolean addProduct(Product product);
    Boolean updateProduct(Product product);
    Boolean deleteProduct(Integer proId);
    Product getProductById(Integer proId);
    List<Product> searchProduct(String productName);
}
