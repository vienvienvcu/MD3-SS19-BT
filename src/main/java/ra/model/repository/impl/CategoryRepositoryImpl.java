package ra.model.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.model.entity.Category;
import ra.model.repository.ICategoryRepository;

import java.util.List;
@Repository
public class CategoryRepositoryImpl implements ICategoryRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Category> getAllCategories() {
        Session session = sessionFactory.openSession();
        try {
           List Categorylist = session.createQuery("from Category").list();
           return Categorylist;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
        return null;
    }

    @Override
    public Boolean addCategory(Category category) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(category);
            session.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return false;
    }

    @Override
    public Category getCategoryById(Integer CateId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Category category = (Category) session.get(Category.class, CateId);
            session.getTransaction().commit();
            return category;
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().commit();
        }finally {
            session.close();
        }
        return null;
    }
}
