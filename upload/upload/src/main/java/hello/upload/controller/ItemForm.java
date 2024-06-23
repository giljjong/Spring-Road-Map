package hello.upload.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 용도를 달리하기 위해 같은 필드로 객체 생성
@Data
public class ItemForm {

    private Long itemId;
    private String itemName;
    private List<MultipartFile> imageFiles;
    private MultipartFile attachFile;
}
