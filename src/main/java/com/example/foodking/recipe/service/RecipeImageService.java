package com.example.foodking.recipe.service;

import com.example.foodking.exception.CommondException;
import com.example.foodking.exception.ExceptionCode;
import com.example.foodking.recipe.domain.RecipeInfo;
import com.example.foodking.recipe.repository.RecipeInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.example.foodking.recipe.service.RecipeService.isMyRecipe;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class RecipeImageService {

    @Value("${file.dir}")
    private String fileDir;

    private final RecipeInfoRepository recipeInfoRepository;

    @Transactional
    public String saveImage(MultipartFile recipeImage, Long recipeInfoId, Long userId) {
        RecipeInfo recipeInfo = findRecipeInfoById(recipeInfoId);

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_FILE);

        if(recipeImage == null)
            throw new CommondException(ExceptionCode.INVALID_SAVE_FILE);

        String originName = recipeImage.getOriginalFilename();
        if(originName.length() <= 0 || originName ==null)
            throw new CommondException(ExceptionCode.INVALID_SAVE_FILE);

        //기존에 이미지가 등록되어있으면 기존 이미지를 삭제한 뒤 새로운 이미지를 추가한다.
        if(recipeInfo.getRecipeImage() != null){
            File file = new File(recipeInfo.getRecipeImage());
            file.delete();
        }

        // 파일 이름으로 쓸 uuid 생성, 동일한 파일명이 들어왔을 때 파일명의 중복을 피하기위해 UUID를 사용.
        String uuid = UUID.randomUUID().toString();

        // 확장자 추출(ex : .png)
        String extension = originName.substring(originName.lastIndexOf("."));

        // 파일을 불러올 때 사용할 파일 경로
        String savedPath = fileDir + uuid + extension;
        try {
            // 실제로 로컬에 파일 저장. (MultipartFile타입의 인스턴스).transferTo(new File(파일의 경로))
            recipeImage.transferTo(new File(savedPath));
        }catch (IOException ex){
            throw new CommondException(ExceptionCode.FILE_IOEXCEPTION);
        }
        // 데이터베이스에 파일 정보 저장
        recipeInfo.addRecipeImage(savedPath);
        recipeInfoRepository.save(recipeInfo);
        return savedPath;
    }

    @Transactional
    public void deleteImage(Long recipeInfoId,Long userId){
        RecipeInfo recipeInfo = findRecipeInfoById(recipeInfoId);

        isMyRecipe(userId,recipeInfo.getUser(),ExceptionCode.ACCESS_FAIL_FILE);

        if(recipeInfo.getRecipeImage() ==null){
            throw new CommondException(ExceptionCode.NOT_EXIST_FILE);
        }
        File file = new File(recipeInfo.getRecipeImage());
        file.delete();
        recipeInfo.deleteRecipeImage();
        recipeInfoRepository.save(recipeInfo);
    }

    private RecipeInfo findRecipeInfoById(Long recipeInfoId){
        return recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));
    }
}
