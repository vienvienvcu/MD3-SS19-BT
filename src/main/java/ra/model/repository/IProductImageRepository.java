package ra.model.repository;

import ra.model.entity.ProductImage;

import java.util.List;

public interface IProductImageRepository {
    List<ProductImage> findAll();
    Boolean save(ProductImage productImage);
    Boolean delete(Integer productImgId);
    List<ProductImage> findByProductId(Integer proId);

}
