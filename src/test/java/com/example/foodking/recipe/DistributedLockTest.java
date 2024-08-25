package com.example.foodking.recipe;

import com.example.foodking.recipe.repository.RecipeInfoRepository;
import com.example.foodking.recipe.service.RecipeService;
import com.example.foodking.reply.common.ReplySortType;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "SPRING_PROFILES_ACTIVE",
        matches = "local"
)
public class DistributedLockTest {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeInfoRepository recipeInfoRepository;

    @Test
    @Disabled
    // 비용이 높은 테스트이기 떄문에 CI-CD중에 테스트를 하지 않기위해 비활성화했습니다. 필요 시 수동으로 테스트하세요.
    void 동시성100명_테스트() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 분산락 적용 메서드 호출
                    recipeService.findRecipe(1L,1L, ReplySortType.LATEST,any(),any());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}
