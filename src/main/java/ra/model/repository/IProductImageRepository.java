package ra.model.repository;

import ra.model.entity.ProductImage;

import java.util.List;

public interface IProductImageRepository {
    List<ProductImage> findAll();

}
