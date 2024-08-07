package ra.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(name = "product_name",length = 100)
    @NotBlank(message = "Product name cannot is empty")
    private String productName;

    @Column(name = "product_image")
    @NotBlank(message = "Image name cannot be empty")
    private String productImage;


    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than zero")
    @Column(name = "product_price")
    private Double productPrice;

    @Column(name ="color")
    @NotBlank(message = "Color cannot be null")
    private String color;

    @Column(name = "size" )
    @NotBlank(message = "size cannot be null")
    private String size;

    @Column(name = "stock")
    @NotNull(message = "Stock cannot be null")
    @Min(value = 0, message = "Stock must be zero or positive")
    private Integer stock;

    @Column(name = "description")
    @Size(max = 500, message = "Description can be up to 500 characters long")
    private String description;

    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @PastOrPresent(message = "Creation time must be in the past or present")
    private Date createTime;

    @Column(name = "update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Update time must be in the past or present")
    private Date updateTime;

    @Column(name = "status")
    @NotNull(message = "Status cannot be empty")
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> imageList;
}
