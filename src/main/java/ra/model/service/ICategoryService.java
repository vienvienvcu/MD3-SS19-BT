package ra.model.service;

import ra.model.entity.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
    Boolean addCategory(Category category);
    Category getCategoryById(Integer CateId);
}
