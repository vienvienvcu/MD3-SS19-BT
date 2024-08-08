package ra.model.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.model.entity.ProductImage;
import ra.model.repository.IProductImageRepository;

import java.util.ArrayList;
import java.util.List;
@Repository
public class ProductImageRepositoryImpl implements IProductImageRepository {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public Boolean save(ProductImage productImage) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(productImage);
            session.getTransaction().commit();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return false;
    }

    @Override
    public Boolean delete(Integer productImgId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            // Tìm đối tượng ProductImage theo ID
            ProductImage productImage = session.get(ProductImage.class, productImgId);
            if (productImage != null) {
                // Xóa đối tượng ProductImage
                session.delete(productImage);
                session.getTransaction().commit();
                return true;
            } else {
                // Nếu không tìm thấy đối tượng, không thực hiện xóa
                session.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }



    @Override
    public List<ProductImage> findByProductId(Integer proId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<ProductImage> productImages = session.createQuery("from ProductImage where product.productId = :proId", ProductImage.class)
                    .setParameter("proId", proId)
                    .list();
            session.getTransaction().commit();
            return productImages;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }


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
