package ra.model.repository;

import ra.model.entity.Category;

import java.util.List;

public interface ICategoryRepository {
    List<Category> getAllCategories();
    Boolean addCategory(Category category);
    Category getCategoryById(Integer CateId);
}
