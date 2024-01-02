package com.example.foodking.RecipeInfo;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Ingredient.Ingredient;
import com.example.foodking.Ingredient.IngredientRepository;
import com.example.foodking.RecipeInfo.DTO.AddRecipeReqDTO;
import com.example.foodking.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeInfoService {

    @Value("${file.dir}")
    private String fileDir;

    private final UserRepository userRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeWayInfoRepository recipeWayInfoRepository;

    @Transactional
    public Long addRecipeInfo(AddRecipeReqDTO addRecipeReqDTO,Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = AddRecipeReqDTO.toRecipeInfoEntity(addRecipeReqDTO,user);
        List<Ingredient> ingredientList = AddRecipeReqDTO.toIngredientListEntity(addRecipeReqDTO.getAddIngredientReqDTOList(),recipeInfo);
        List<RecipeWayInfo> recipeWayInfoList = AddRecipeReqDTO.toRecipeWayInfoListEntity(addRecipeReqDTO.getAddRecipeWayInfoReqDTOList(),recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        ingredientRepository.saveAll(ingredientList);
        recipeWayInfoRepository.saveAll(recipeWayInfoList);
        return  recipeInfo.getRecipeInfoId();
    }

    @Transactional
    public String addImage(MultipartFile recipeImage,Long recipeInfoId) {
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

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
}
