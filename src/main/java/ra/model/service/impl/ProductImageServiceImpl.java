package ra.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.model.entity.ProductImage;
import ra.model.repository.IProductImageRepository;
import ra.model.service.IProductImageService;

import java.util.List;
@Service
public class ProductImageServiceImpl implements IProductImageService {
    @Autowired
    private IProductImageRepository productImageRepository;
    @Override
    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }

    @Override
    public Boolean save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public Boolean delete(Integer productImgId) {
        return productImageRepository.delete(productImgId);
    }
}
