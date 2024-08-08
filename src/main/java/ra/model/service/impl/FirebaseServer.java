package ra.model.service.impl;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.model.service.IFirebaseService;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:firebase_config.properties")
public class FirebaseServer implements IFirebaseService {
    @Autowired
    private ServletContext servletContext;

    @Value("${bucket_name}")
    String buck_name;

    @Autowired
    private Storage storage;

    @Override
    public String uploadLocal(MultipartFile fileUpload) {
        // Xác định đường dẫn thư mục
        String path = servletContext.getRealPath("/resources/images");
        File directory = new File(path);

        // Tạo thư mục nếu không tồn tại
        if (!directory.exists()) {
            boolean created = directory.mkdirs(); // Thử tạo thư mục, bao gồm các thư mục con nếu cần
            if (!created) {
                throw new RuntimeException("Không thể tạo thư mục: " + directory.getAbsolutePath());
            }
        }

        // Tạo đường dẫn đầy đủ cho tệp
        String localPath = directory.getAbsolutePath() + File.separator + fileUpload.getOriginalFilename();
        File dest = new File(localPath);

        // Lưu tệp vào thư mục
        try {
            Files.write(dest.toPath(), fileUpload.getBytes(), StandardOpenOption.CREATE);
            return uploadFirebase(localPath);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu tệp: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFirebase(String localPath) {
        Path path = Paths.get(localPath);
        String fileName = path.getFileName().toString();
        BlobId blobId = BlobId.of(buck_name, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        blobInfo = blobInfo.toBuilder().setAcl(acls).build();

        try {
            Blob blob = storage.create(blobInfo, Files.readAllBytes(path));
            return blob.getMediaLink();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload lên Firebase: " + e.getMessage(), e);
        }
    }

}

