package ra.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.model.entity.Product;
import ra.model.service.IProductService;

import java.util.List;
@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private IProductService productService;

    @Override
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Override
    public Boolean addProduct(Product product) {
        return productService.addProduct(product);
    }

    @Override
    public Boolean updateProduct(Product product) {
        return productService.updateProduct(product);
    }

    @Override
    public Boolean deleteProduct(Integer proId) {
        return productService.deleteProduct(proId);
    }

    @Override
    public Product getProductById(Integer proId) {
        return productService.getProductById(proId);
    }

    @Override
    public List<Product> searchProduct(String productName) {
        return productService.searchProduct(productName);
    }
}
