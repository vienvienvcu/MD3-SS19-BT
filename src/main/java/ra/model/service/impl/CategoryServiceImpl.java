package ra.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.model.entity.Category;
import ra.model.repository.ICategoryRepository;
import ra.model.service.ICategoryService;

import java.util.List;
@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private ICategoryRepository categoryRepository;
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    @Override
    public Boolean addCategory(Category category) {
        return categoryRepository.addCategory(category);
    }

    @Override
    public Category getCategoryById(Integer CateId) {
        return categoryRepository.getCategoryById(CateId);
    }


}
