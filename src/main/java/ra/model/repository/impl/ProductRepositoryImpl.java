package ra.model.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.model.entity.Product;
import ra.model.repository.IProductRepository;

import java.util.Date;
import java.util.List;
@Repository
public class ProductRepositoryImpl implements IProductRepository {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public List<Product> getAllProducts() {
        Session session = sessionFactory.openSession();
        try {
            // Sử dụng JPQL với JOIN FETCH để nạp danh sách hình ảnh phụ
            String hql = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.imageList";
            List<Product> productList = session.createQuery(hql).list();
            return productList;
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return null;
    }

    @Override
    public Boolean addProduct(Product product) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // Cập nhật thời gian tạo và cập nhật
            Date currentDate = new Date();
            product.setCreateTime(currentDate);
            product.setUpdateTime(currentDate);
            session.save(product);
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
    public Boolean updateProduct(Product product) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // Cập nhật thời gian cập nhật
            product.setUpdateTime(new Date());
            session.update(product);
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
    public Boolean deleteProduct(Integer proId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Product product = session.get(Product.class, proId);
            if (product != null) {
                session.delete(product);
                session.getTransaction().commit();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public Product getProductById(Integer proId) {
        Session session = sessionFactory.openSession();
        try {
            // Sử dụng JPQL với JOIN FETCH để nạp danh sách hình ảnh phụ
            String hql = "SELECT p FROM Product p LEFT JOIN FETCH p.imageList WHERE p.productId = :productId";
            Product product = (Product) session.createQuery(hql)
                    .setParameter("productId", proId)
                    .uniqueResult();
            return product;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public List<Product> searchProduct(String productName) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            String hql = "FROM Product WHERE productName LIKE :productName";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("productName", "%" + productName + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
        }
    }
}
