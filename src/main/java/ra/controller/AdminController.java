package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ra.model.entity.Category;
import ra.model.entity.Product;
import ra.model.entity.ProductImage;
import ra.model.repository.impl.CategoryRepositoryImpl;
import ra.model.repository.impl.ProductRepositoryImpl;
import ra.model.service.IFirebaseService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private CategoryRepositoryImpl categoryRepository;
    @Autowired
    private ProductRepositoryImpl productRepository;

    @Autowired
    private IFirebaseService firebaseService;

    @RequestMapping(value = {"/", "/adminHome"})
    public String adminHome() {
        return "admin/index";
    }

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

    @RequestMapping(value = "/loadProduct")
    public String loadProduct(Model model) {
        List<Product> productList = productRepository.getAllProducts();
        model.addAttribute("productList", productList);
        return "admin/productList";
    }

    @RequestMapping(value = "/initInsertProduct")
    public String initInsertProduct(Model model) {
        Product newProduct = new Product();
        model.addAttribute("product", newProduct);
        List<Category> categoryList = categoryRepository.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "admin/insertProduct";
    }


    @RequestMapping(value = "/insertProduct")
    public String insertProduct(@Validated @ModelAttribute("product") Product product,
                                BindingResult result,
                                @RequestParam("filePrimary") MultipartFile filePrimary,
                                @RequestParam("secondaryImages") MultipartFile[] listSecondImages,
                                Model model) {
//
//        // Kiểm tra lỗi từ BindingResult
//        if (result.hasErrors()) {
//            model.addAttribute("categoryList", categoryRepository.getAllCategories()); // Nạp danh sách danh mục để hiển thị lại
//            return "admin/insertProduct";
//        }

        try {
            // Upload ảnh chính lên Firebase và lưu URL vào sản phẩm
            String primaryImageUrl = firebaseService.uploadLocal(filePrimary);
            product.setProductImage(primaryImageUrl);

            // Xử lý ảnh phụ
            List<String> secondaryImageUrls = new ArrayList<>();
            for (MultipartFile file : listSecondImages) {
                if (!file.isEmpty()) {
                    String imageUrl = firebaseService.uploadLocal(file);
                    secondaryImageUrls.add(imageUrl);
                }
            }

            // Lưu sản phẩm vào cơ sở dữ liệu
            productRepository.addProduct(product);

            // Tạo danh sách ảnh phụ cho sản phẩm
            List<ProductImage> productImages = new ArrayList<>();
            for (String imageUrl : secondaryImageUrls) {
                ProductImage productImage = new ProductImage();
                productImage.setImageName(imageUrl);
                productImage.setProduct(product);
                productImages.add(productImage);
            }
            product.setImageList(productImages);

            // Cập nhật sản phẩm với ảnh phụ
            productRepository.updateProduct(product);

            return "redirect:/loadProduct";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lưu sản phẩm: " + e.getMessage());
            model.addAttribute("categoryList", categoryRepository.getAllCategories()); // Nạp danh sách danh mục để hiển thị lại
            return "admin/insertProduct";
        }
    }

}
