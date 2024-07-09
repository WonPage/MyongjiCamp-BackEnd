package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.CompleteDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.Image;
import com.won.myongjiCamp.repository.CompleteRepository;
import com.won.myongjiCamp.repository.ImageRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompleteService {

    final private CompleteRepository completeRepository;
    final private ImageRepository imageRepository;
    final private S3ImageService s3ImageService;

    @Transactional
    public WriteCompleteResponseDto create(CompleteDto completeDto, Member member) {
        // 이미지 개수 검사
        if (completeDto.getImages().size() > 5) {
            throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
        }

        List<MultipartFile> images = completeDto.getImages();
        List<String> imageUrls = new ArrayList<>();
        List<Image> saveImageList = new ArrayList<>();

        if(images != null && !images.isEmpty()){
            for(MultipartFile image : images){
                String imageUrl = s3ImageService.upload(image);
                imageUrls.add(imageUrl);
                saveImageList.add(Image.builder().url(imageUrl).build());
            }
        }

        CompleteBoard completeBoard = new CompleteBoard();
        completeBoard.setTitle(completeDto.getTitle());
        completeBoard.setContent(completeDto.getContent());
        completeBoard.setMember(member);
        completeRepository.save(completeBoard);

        for(Image image : saveImageList){
            completeBoard.addImage(image);
        }

        WriteCompleteResponseDto writeCompleteResponseDto = WriteCompleteResponseDto.builder()
                .boardId(completeBoard.getId())
                .writerId(member.getId())
                .imageUrls(imageUrls)
                .build();

        return writeCompleteResponseDto;
    }

    @Transactional
    public WriteCompleteResponseDto update(Long id, CompleteDto completeDto) {
        CompleteBoard completeBoard = completeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        completeBoard.setTitle(completeDto.getTitle());
        completeBoard.setContent(completeDto.getContent());

        // 기존 이미지 삭제
        List<Image> existingImages = imageRepository.findByBoard(completeBoard);
        for (Image image : existingImages) {
            try {
                s3ImageService.deleteImageFromS3(image.getUrl());
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제 실패: " + image.getUrl());
            }
        }
        completeBoard.getImages().clear(); // 이미지 리스트 클리어

        // 새로운 이미지 업로드 및 추가
        List<MultipartFile> newImages = completeDto.getImages();
        List<String> newImageUrls = new ArrayList<>();
        List<Image> newImageList = new ArrayList<>();

        if (newImages != null && !newImages.isEmpty()) {
            if (newImages.size() > 5) {
                throw new IllegalArgumentException("최대 5개의 이미지만 업로드할 수 있습니다.");
            }

            for (MultipartFile image : newImages) {
                String imageUrl = s3ImageService.upload(image);
                newImageUrls.add(imageUrl);
                newImageList.add(Image.builder().url(imageUrl).build());
            }
        }

        // 새로운 이미지를 게시글에 추가
        for (Image image : newImageList) {
            completeBoard.addImage(image);
        }

        completeBoard.setModifiedDate(new Timestamp(System.currentTimeMillis()));

        WriteCompleteResponseDto writeCompleteResponseDto = WriteCompleteResponseDto.builder()
                .boardId(completeBoard.getId())
                .writerId(completeBoard.getMember().getId())
                .imageUrls(newImageUrls)
                .build();

        return writeCompleteResponseDto;
    }

    @Transactional
    public void delete(Long id) {
        CompleteBoard completeBoard = completeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        List<Image> images = imageRepository.findByBoard(completeBoard);
        for (Image image : images) {
            try {
                s3ImageService.deleteImageFromS3(image.getUrl());
            } catch (Exception e) {
                throw new IllegalArgumentException("이미지 삭제 실패: " + image.getUrl());
            }
        }
        completeRepository.deleteById(id);
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WriteCompleteResponseDto {
        private Long writerId; //글 쓴 사람 id
        private Long boardId; //글 id
        private List<String> imageUrls; //이미 url들
    }
}

