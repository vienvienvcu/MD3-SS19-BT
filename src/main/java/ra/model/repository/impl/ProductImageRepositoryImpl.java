package ra.model.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.model.entity.ProductImage;
import ra.model.repository.IProductImageRepository;

import java.util.List;
@Repository
public class ProductImageRepositoryImpl implements IProductImageRepository {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public List<ProductImage> findAll() {
        Session session = sessionFactory.openSession();
        try {
            List productImagesList = session.createQuery("from ProductImage").list();
            return productImagesList;

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
        return null;
    }
}
