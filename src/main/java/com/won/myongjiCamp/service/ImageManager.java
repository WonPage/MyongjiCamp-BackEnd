package com.won.myongjiCamp.service;

import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.Image;
import com.won.myongjiCamp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageManager {

    final private ImageRepository imageRepository;

    @Value("${file.dir}")
    private String storePath; //파일 저장할 경로

    //확장자 추출
    private String extractExt(String uploadFileName) {
        int pos = uploadFileName.lastIndexOf(".");
        return uploadFileName.substring(pos + 1);
    }

    //저장되는 파일 이름 결정 ( 같은 사진을 올리면 중복 오류
    public String createStoreFileName(String uploadFileName) {
        String ext = extractExt(uploadFileName); //확장자 가져오기
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //저장되는 경로 반환
    public String getImagePath(String storedFileName, String ext) {
        String imagePath = storePath + ext + "/" + storedFileName;
        return imagePath;
    }

    //이미지 저장
    public Image saveImage(MultipartFile multipartFile, CompleteBoard completeBoard) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        String uploadFileName = multipartFile.getOriginalFilename();
        String storedFileName = createStoreFileName(uploadFileName);
        String ext = extractExt(uploadFileName);

        multipartFile.transferTo(new File(getImagePath(storedFileName, ext)));

        Image savedImage = Image.builder()
                .uploadFileName(uploadFileName)
                .storedFileName(storedFileName)
                .board(completeBoard)
                .build();

        return imageRepository.save(savedImage);
    }

    //전체 이미지 저장
    public List<Image> saveImages(List<MultipartFile> multipartFiles, CompleteBoard completeBoard) throws IOException {
        List<Image> imageList = new ArrayList<>();

        for(MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                imageList.add(saveImage(multipartFile, completeBoard));
            }
        }
        return imageList;
    }
}
