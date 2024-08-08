package ra.model.service;

import ra.model.entity.ProductImage;

import java.util.List;

public interface IProductImageService {
    List<ProductImage> findAll();
    Boolean save(ProductImage productImage);
    Boolean delete(Integer productImgId);
}
