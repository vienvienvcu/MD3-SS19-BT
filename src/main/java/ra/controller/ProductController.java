package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.model.entity.Category;
import ra.model.entity.Product;
import ra.model.entity.ProductImage;
import ra.model.repository.IProductImageRepository;
import ra.model.repository.impl.CategoryRepositoryImpl;
import ra.model.repository.impl.ProductRepositoryImpl;
import ra.model.service.ICategoryService;
import ra.model.service.IFirebaseService;
import ra.model.service.IProductService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private CategoryRepositoryImpl categoryRepository;

    @Autowired
    private ProductRepositoryImpl productRepository;

    @Autowired
    private IProductImageRepository productImageRepository;

    @Autowired
    private IFirebaseService firebaseService;



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


    @RequestMapping(value = "/insertProduct", method = RequestMethod.POST)
    public String insertProduct(@Validated @ModelAttribute("product") Product product,
                                BindingResult result,
                                @RequestParam("filePrimary") MultipartFile filePrimary,
                                @RequestParam("secondaryImages") MultipartFile[] listSecondImages,
                                Model model) {

        // Nạp danh sách danh mục để hiển thị lại
        model.addAttribute("categoryList", categoryRepository.getAllCategories());

        // Nếu có lỗi từ BindingResult, trả lại trang với thông báo lỗi
        if (result.hasErrors()) {
            return "admin/insertProduct";
        }

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
            return "admin/insertProduct";
        }
    }

    @RequestMapping(value = "/initUpdateProduct/{id}")
    public String initUpdateProduct(@PathVariable Integer id, Model model) {
        Product productUpdate = productRepository.getProductById(id);
        List<Category> categoryList = categoryRepository.getAllCategories();
        model.addAttribute("product", productUpdate);
        model.addAttribute("categoryList", categoryList);
        return "admin/updateProduct";
    }

    @RequestMapping(value = "/updateProduct", method = RequestMethod.POST)
    public String updateProduct(@ModelAttribute("product") Product product,
                                @RequestParam("filePrimary") MultipartFile filePrimary,
                                @RequestParam("secondaryImages") MultipartFile[] listSecondImages,
                                Model model) {

        try {
            // Tìm sản phẩm hiện có
            Product existingProduct = productRepository.getProductById(product.getProductId());
            if (existingProduct == null) {
                model.addAttribute("errorMessage", "Sản phẩm không tồn tại.");
                return "admin/updateProduct";
            }
            // Cập nhật thông tin sản phẩm
            existingProduct.setProductName(product.getProductName());
            existingProduct.setProductPrice(product.getProductPrice());
            existingProduct.setColor(product.getColor());
            existingProduct.setSize(product.getSize());
            existingProduct.setStock(product.getStock());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setStatus(product.getStatus());
//            existingProduct.setProductImage(filePrimary!=null && filePrimary.getSize()!=0 ? firebaseService.uploadLocal(filePrimary) :existingProduct.getProductImage());


            // Xử lý ảnh chính
            if (filePrimary != null && !filePrimary.isEmpty()) {
                String primaryImageUrl = firebaseService.uploadLocal(filePrimary); // Tải ảnh lên dịch vụ
                existingProduct.setProductImage(primaryImageUrl);
            }
            // Nếu không có ảnh mới, giữ nguyên ảnh cũ

            // Xử lý ảnh phụ
            List<ProductImage> updatedImageList = new ArrayList<>();
            for (MultipartFile image : listSecondImages) {
                if (!image.isEmpty()) {
                    String imageUrl = firebaseService.uploadLocal(image); // Tải ảnh lên dịch vụ
                    ProductImage productImage = new ProductImage();
                    productImage.setImageName(imageUrl);
                    productImage.setProduct(existingProduct);
                    updatedImageList.add(productImage);
                }
            }

            // Xóa ảnh phụ cũ và cập nhật ảnh phụ mới
            for (ProductImage image : existingProduct.getImageList()) {
                productImageRepository.delete(image.getImageId()); // Xóa ảnh khỏi cơ sở dữ liệu
            }
            existingProduct.getImageList().clear(); // Xóa danh sách ảnh phụ trong sản phẩm
            existingProduct.setImageList(updatedImageList); // Thêm ảnh phụ mới vào sản phẩm

            // Cập nhật danh mục
            if (product.getCategory() != null) {
                Category category = categoryRepository.getCategoryById(product.getCategory().getCategoryId());
                existingProduct.setCategory(category);
            }

            // Cập nhật sản phẩm
            productRepository.updateProduct(existingProduct);

            return "redirect:/loadProduct"; // Chuyển hướng đến danh sách sản phẩm
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log lỗi
            model.addAttribute("errorMessage", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            model.addAttribute("product", product);
            return "admin/updateProduct";
        }
    }


    @RequestMapping(value = "deleteProduct/{id}")
    public String deleteProduct(@PathVariable Integer id, Model model) {
        try {
            // Lấy sản phẩm cần xóa
            Product product = productRepository.getProductById(id);
            if (product != null) {
                // Xóa sản phẩm
                productRepository.deleteProduct(id);

                return "redirect:/loadProduct";
            } else {
                model.addAttribute("errorMessage", "Sản phẩm không tồn tại.");
                return "admin/productList";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "admin/productList";
        }
    }

    @RequestMapping(value = "detailProduct/{id}")
    public String detailProduct(@PathVariable Integer id, Model model) {
        try {
            // Lấy thông tin sản phẩm theo ID
            Product product = productRepository.getProductById(id);

            // Kiểm tra nếu sản phẩm không tồn tại
            if (product == null) {
                model.addAttribute("errorMessage", "Sản phẩm không tồn tại.");
                return "admin/productList"; // Trở về danh sách sản phẩm nếu sản phẩm không tồn tại
            }

            // Thêm sản phẩm vào model để truyền đến view
            model.addAttribute("product", product);

            // Truyền danh sách danh mục đến view nếu cần (hoặc có thể không cần tùy thuộc vào giao diện của bạn)
            List<Category> categoryList = categoryRepository.getAllCategories();
            model.addAttribute("categoryList", categoryList);

            // Trả về trang chi tiết sản phẩm
            return "admin/detailProduct";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
            return "admin/productList"; // Trở về danh sách sản phẩm nếu có lỗi
        }
    }
    @RequestMapping(value = "/searchProductByName", method = RequestMethod.GET)
    public String searchProductByName(@RequestParam("productName") String productName, Model model) {
        try {
            // Tìm kiếm sản phẩm theo tên
            List<Product> productList = productRepository.searchProduct(productName);

            if (!productList.isEmpty()) {
                // Thêm danh sách sản phẩm vào model để hiển thị trên view
                model.addAttribute("productList", productList);
                return "admin/productList"; // Trang hiển thị danh sách sản phẩm
            } else {
                // Nếu không có sản phẩm nào tìm thấy, thêm thông báo lỗi vào model
                model.addAttribute("errorMessage", "Không tìm thấy sản phẩm nào với tên '" + productName + "'.");
                return "admin/productList"; // Trang hiển thị danh sách sản phẩm (hoặc trang lỗi tùy thuộc vào bạn)
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log lỗi
            model.addAttribute("errorMessage", "Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
            return "admin/productList"; // Trang hiển thị danh sách sản phẩm (hoặc trang lỗi tùy thuộc vào bạn)
        }
    }


}
