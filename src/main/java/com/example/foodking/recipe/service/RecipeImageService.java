package com.example.foodking.recipe.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.foodking.recipe.service.RecipeService.isMyRecipe;

@Service
@Transactional(readOnly = true)
public class RecipeImageService {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final RecipeInfoRepository recipeInfoRepository;

    @Autowired
    public RecipeImageService(RecipeInfoRepository recipeInfoRepository, AmazonS3 amazonS3,
                              @Value("${cloud.aws.s3.bucket}") String bucketName){
        this.recipeInfoRepository = recipeInfoRepository;
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Transactional
    public void addImage(MultipartFile newImage, Long recipeInfoId, Long userId) {
        RecipeInfo recipeInfo = findRecipeInfoById(recipeInfoId);

        isMyRecipe(userId, recipeInfo.getUser(), ExceptionCode.ACCESS_FAIL_FILE);

        if(newImage == null)
            throw new CommondException(ExceptionCode.INVALID_SAVE_FILE);

        String originalFileName = newImage.getOriginalFilename();
        if(originalFileName.length() <= 0 || originalFileName ==null)
            throw new CommondException(ExceptionCode.INVALID_SAVE_FILE);

        //기존에 이미지가 등록되어있으면 기존 이미지를 삭제한 뒤 새로운 이미지를 추가한다.
        if(recipeInfo.getRecipeImage() != null){
            deleteImageS3(recipeInfo.getRecipeImage());
        }

        // 파일을 불러올 때 사용할 파일 경로
        String fileName = FileNameGenerator.createFileName(originalFileName);
        String savedUrl = saveImageS3(newImage, fileName);

        // 데이터베이스에 파일 정보 저장
        recipeInfo.addRecipeImage(savedUrl);
        recipeInfoRepository.save(recipeInfo);
    }

    @Transactional
    public void deleteImage(Long recipeInfoId, Long userId){
        RecipeInfo recipeInfo = findRecipeInfoById(recipeInfoId);

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_FILE);

        if(recipeInfo.getRecipeImage() ==null){
            throw new CommondException(ExceptionCode.NOT_EXIST_FILE);
        }
       
        deleteImageS3(recipeInfo.getRecipeImage());
        recipeInfo.deleteRecipeImage();
        recipeInfoRepository.save(recipeInfo);
    }

    private String saveImageS3(MultipartFile newImage, String fileName){

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(newImage.getSize());
        metadata.setContentType(newImage.getContentType());

        try{
            amazonS3.putObject(bucketName, fileName, newImage.getInputStream(), metadata);
        }catch (IOException e){
            throw new CommondException(ExceptionCode.FILE_IOEXCEPTION);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private void deleteImageS3(String imageUrl){

        try {
            // URL에서 버킷 이름과 키 추출
            URL url = new URL(imageUrl);
            String bucket = url.getHost().split("\\.")[0];
            String key = url.getPath().substring(1); // 첫 '/' 제거

            // S3에서 파일 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (MalformedURLException e) {
            throw new CommondException(ExceptionCode.NOT_EXIST_FILE);
        }
    }

    private RecipeInfo findRecipeInfoById(Long recipeInfoId){
        return recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));
    }
}
