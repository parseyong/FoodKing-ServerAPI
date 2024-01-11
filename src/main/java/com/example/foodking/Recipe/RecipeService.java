package com.example.foodking.Recipe;

import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.Recipe.DTO.SaveRecipeReqDTO;
import com.example.foodking.Recipe.Ingredient.DTO.SaveIngredientReqDTO;
import com.example.foodking.Recipe.Ingredient.Ingredient;
import com.example.foodking.Recipe.Ingredient.IngredientRepository;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfo;
import com.example.foodking.Recipe.RecipeInfo.RecipeInfoRepository;
import com.example.foodking.Recipe.RecipeWayInfo.DTO.SaveRecipeWayInfoReqDTO;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfo;
import com.example.foodking.Recipe.RecipeWayInfo.RecipeWayInfoRepository;
import com.example.foodking.User.User;
import com.example.foodking.User.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class RecipeService {

    @Value("${file.dir}")
    private String fileDir;

    private final UserRepository userRepository;
    private final RecipeInfoRepository recipeInfoRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeWayInfoRepository recipeWayInfoRepository;

    @Transactional
    public Long addRecipe(SaveRecipeReqDTO saveRecipeReqDTO, Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = SaveRecipeReqDTO.toRecipeInfoEntity(saveRecipeReqDTO,user);
        List<Ingredient> ingredientList = SaveRecipeReqDTO.toIngredientListEntity(saveRecipeReqDTO.getSaveIngredientReqDTOList(),recipeInfo);
        List<RecipeWayInfo> recipeWayInfoList = SaveRecipeReqDTO.toRecipeWayInfoListEntity(saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList(),recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        ingredientRepository.saveAll(ingredientList);
        recipeWayInfoRepository.saveAll(recipeWayInfoList);
        return  recipeInfo.getRecipeInfoId();
    }

    @Transactional
    public void updateRecipe(SaveRecipeReqDTO saveRecipeReqDTO, Long userId,Long recipeInfoId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        List<Ingredient> ingredientList = recipeInfo.getIngredientList();
        List<RecipeWayInfo> recipeWayInfoList = recipeInfo.getRecipeWayInfoList();

        changeRecipeInfo(recipeInfo,saveRecipeReqDTO);
        changeRecipeWayInfoList(saveRecipeReqDTO.getSaveRecipeWayInfoReqDTOList(),recipeWayInfoList,recipeInfo);
        changeIngredientList(saveRecipeReqDTO.getSaveIngredientReqDTOList(),ingredientList,recipeInfo);

        recipeInfoRepository.save(recipeInfo);
        ingredientRepository.saveAll(ingredientList);
        recipeWayInfoRepository.saveAll(recipeWayInfoList);
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

    @Transactional
    public void deleteImage(Long recipeInfoId){
        RecipeInfo recipeInfo = recipeInfoRepository.findById(recipeInfoId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_RECIPEINFO));

        if(recipeInfo.getRecipeImage() ==null){
            throw new CommondException(ExceptionCode.NOT_EXIST_FILE);
        }
        File file = new File(recipeInfo.getRecipeImage());
        file.delete();
        recipeInfo.deleteRecipeImage();
        recipeInfoRepository.save(recipeInfo);
    }

    public void changeRecipeInfo(RecipeInfo recipeInfo, SaveRecipeReqDTO saveRecipeReqDTO){
        recipeInfo.changeCalogy(saveRecipeReqDTO.getCalogy());
        recipeInfo.changeRecipeInfoType(saveRecipeReqDTO.getRecipeInfoType());
        recipeInfo.changeCookingTime(saveRecipeReqDTO.getCookingTime());
        recipeInfo.changeIngredientCost(saveRecipeReqDTO.getIngredentCost());
        recipeInfo.changeRecipeName(saveRecipeReqDTO.getRecipeName());
        recipeInfo.changeRecipeTip(saveRecipeReqDTO.getRecipeTip());
    }

    public void changeRecipeWayInfoList(List<SaveRecipeWayInfoReqDTO> saveRecipeWayInfoReqDTOList , List<RecipeWayInfo> recipeWayInfoList,
                                               RecipeInfo recipeInfo){
        int i=0;
        int indx = Math.min(saveRecipeWayInfoReqDTOList.size(), recipeWayInfoList.size());
        for(; i<indx;i++){
            recipeWayInfoList.set(i,changeRecipeWayInfo(saveRecipeWayInfoReqDTOList.get(i),recipeWayInfoList.get(i)));
        }

        // 조리순서가 추가된 경우
        if(saveRecipeWayInfoReqDTOList.size() > recipeWayInfoList.size()){
            for(; i< saveRecipeWayInfoReqDTOList.size();i++){
                RecipeWayInfo recipeWayInfo = SaveRecipeWayInfoReqDTO.toEntity(saveRecipeWayInfoReqDTOList.get(i),recipeInfo);
                recipeWayInfoList.add(i,recipeWayInfo);
            }
        }

        // 조리순서가 줄어든 경우
        if(saveRecipeWayInfoReqDTOList.size() < recipeWayInfoList.size()){
            int size = recipeWayInfoList.size();
            for(; i< size;i++){
                recipeWayInfoRepository.delete(recipeWayInfoList.get(indx));
                recipeWayInfoList.remove(indx);
            }
        }

    }

    public void changeIngredientList(List<SaveIngredientReqDTO> saveIngredientReqDTOList, List<Ingredient> ingredientList,
                                            RecipeInfo recipeInfo){
        int i=0;
        int indx = Math.min(saveIngredientReqDTOList.size(), ingredientList.size());
        for(; i<indx;i++){
            ingredientList.set(i,changeIngredient(saveIngredientReqDTOList.get(i),ingredientList.get(i)));
        }

        // 조리순서가 추가된 경우
        if(saveIngredientReqDTOList.size() > ingredientList.size()){
            for(; i< saveIngredientReqDTOList.size();i++){
                Ingredient ingredient = SaveIngredientReqDTO.toEntity(saveIngredientReqDTOList.get(i),recipeInfo);
                ingredientList.add(i,ingredient);
            }
        }

        // 조리순서가 줄어든 경우
        if(saveIngredientReqDTOList.size() < ingredientList.size()){
            int size = ingredientList.size();
            for(; i< size;i++){
                ingredientRepository.delete(ingredientList.get(indx));
                ingredientList.remove(indx);
            }
        }
    }

    public Ingredient changeIngredient(SaveIngredientReqDTO newInfo,Ingredient ingredient){
        ingredient.changeIngredientName(newInfo.getIngredientName());
        ingredient.changeIngredientAmount(newInfo.getIngredientAmount());
        return ingredient;
    }

    public RecipeWayInfo changeRecipeWayInfo(SaveRecipeWayInfoReqDTO newInfo, RecipeWayInfo recipeWayInfo){
        recipeWayInfo.changeRecipeWay(newInfo.getRecipeWay());
        return recipeWayInfo;
    }
}
