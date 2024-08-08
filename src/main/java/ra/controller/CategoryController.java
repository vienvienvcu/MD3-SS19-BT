package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ra.model.entity.Category;
import ra.model.repository.impl.CategoryRepositoryImpl;


import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryRepositoryImpl categoryRepository;

    @RequestMapping(value = "/loadCategory")
    public String loadCategory(Model model) {
        List<Category> categoryList = categoryRepository.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "admin/categoryList";
    }

    @GetMapping(value = {"/initInsertCategory"})
    public String initInsertCategory(Model model) {
        Category newCategory = new Category();
        model.addAttribute("category", newCategory);
        return "admin/categoryInsert";
    }

    @RequestMapping(value = {"/insertCategory"})
    public String insertCategory(@Validated @ModelAttribute("category") Category category,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin/categoryInsert";
        }
        try {
            // Lưu category vào cơ sở dữ liệu
            categoryRepository.addCategory(category);
            // Chuyển hướng đến trang danh sách category hoặc trang thành công
            return "redirect:/loadCategory";
        } catch (Exception e) {
            // Xử lý lỗi (ví dụ: lưu vào cơ sở dữ liệu không thành công)
            model.addAttribute("errorMessage", "Lỗi khi lưu danh mục: " + e.getMessage());
            return "admin/categoryInsert";
        }
    }


}
