package ra.model.repository;

import ra.model.entity.Product;

import java.util.List;

public interface IProductRepository {
    List<Product> getAllProducts();
    Boolean addProduct(Product product);
    Boolean updateProduct(Product product);
    Boolean deleteProduct(Integer proId);
    Product getProductById(Integer proId);
}
