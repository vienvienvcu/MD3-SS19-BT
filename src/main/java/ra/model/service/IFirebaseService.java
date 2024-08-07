package ra.model.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFirebaseService {
    String uploadLocal(MultipartFile fileUpload);
    String uploadFirebase(String localPath);
}
