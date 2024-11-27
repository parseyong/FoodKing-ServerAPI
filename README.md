<p align="center">
  <img src="https://github.com/user-attachments/assets/1393b9a3-1e60-4d2a-bdc7-22798b9b28dd" width="400px">
</p>

<p align="center">
  <b style="font-size:18px;">한식, 양식, 일식, 중식 상관없이 모든 음식에 대한 레시피를 공유하는 서비스입니다.</b><br>
  <b style="font-size:18px;">자신만의 레시피를 공유하거나 필요한 레시피를 손쉽게 찾아 요리할 수 있습니다.</b>
  <br>
  <br>
  <b style="font-size:18px;">FoodKing은 대규모 트래픽을 가정하여 개발된 서비스입니다.</b>
</p>

<br>
<br>

## 🍳FoodKing의 주요 관심사
1. Git Action과 DockerHub를 통한 CI/CD 자동화 구축
2. 쿼리튜닝과 비정규화를 통한 790%의 조회성능 향상
3. Ngrinder을 통한 성능 분석
4. Nginx를 통한 프록시 서버 구축 & Scale Out과 LoadBalancing
5. N+1쿼리 지양
6. 양방향 연관관계 Mapping 지양
7. Master-Slave Replication 을 통한 DB부하 분산
8. Redisson의 분산락을 통한 동시성 제어
9. SOLID원칙을 통한 객체지향적인 코드 개발
10. 207개의 Unit Test code를 통한 프로젝트 안정성 강화
11. 지속적인 리팩토링과 일관된 코드컨벤션을 통한 Clean Code 지향
12. 사용자 인증,인가의 문제점 개선
13. Caching을 통한 조회성능 향상
14. Layered Architecture
15. Restful한 API
<br>

## 🍳트러블 슈팅
- [FoodKing #1] 쿼리튜닝 - 레시피 단건조회(<strong>210%</strong>),페이징조회(<strong>790%</strong>) 로직의 성능을 향상시킨 이야기<br>
  https://psy217300.tistory.com/200<br><br>
- [FoodKing #2] 사용자 인증 - Token인증 적용 및 문제점을 해결해가는 과정<br>
  https://psy217300.tistory.com/201<br><br>
- [FoodKing #3] 분산락을 통해 동시성 제어를 해본 이야기 - 분산락을 선택한 이유<br>
  https://psy217300.tistory.com/202<br><br>
- [FoodKing #4] 캐싱 - 어느 곳에 캐싱을 적용해야 할까?<br>
  https://psy217300.tistory.com/208<br><br>
- [FoodKing #5] 캐시 갱신방법 - keys * ,scan<br>
  https://psy217300.tistory.com/209<br><br>
- [FoodKing #6] Docker와 Git Action을 통한 CI/CD 자동화 구축<br>
  https://psy217300.tistory.com/225<br>
<br>

## 🍳쿼리튜닝 후 성능비교

- <strong>약 790% 성능향상</strong> <br>
- 로컬에서 성능측정을 했기때문에 절대적인 TPS값은 낮게 측정되었으며 timeOut으로인한 에러또한 발생했습니다.<br>
- 자세한 설명은 트러블 슈팅 #1번을 참고하시면 감사하겠습니다. <br>
 <br>
<p align="center">
  
  <img src="https://github.com/user-attachments/assets/1f80f115-e006-4523-b39f-981492c47bf2" width="800px"><br>
  <strong>튜닝 전 TPS</strong>
  <br>
  
  <img src="https://github.com/user-attachments/assets/d0c9f201-db3b-4f05-8841-7ff5f9d5c377" width="800px"><br>
  <strong>튜닝 후 TPS</strong>
</p>

<br>

## 🍳사용 기술
- Java 17
- SpringBoot 2.7.5<br>
- JPA<br>
- QueryDsl<br>
- MariaDB<br>
- Redis<br>
- Docker<br>
- Ngrinder<br>
- Gradle<br>
<br>

## 🍳FoodKing 구조도

![웹 아키텍쳐 구조](https://github.com/user-attachments/assets/a9df8fe6-ff9a-414d-9cd3-b9245f88cf4e)
<br>

## 🍳Foodking ERD

https://www.erdcloud.com/d/cDhCYyzgXiLxH5EJa
![image](https://github.com/user-attachments/assets/0dc9fb3f-3ddc-4ccb-97e9-acf6cab8c545)
<br>

## 🍳Front

![image](https://github.com/user-attachments/assets/5a482f5d-ea72-4661-87c9-dced46c6ff7c)
![image](https://github.com/user-attachments/assets/6db6a19a-5da5-4280-9920-68da5a76f5fb)
![image](https://github.com/user-attachments/assets/b5495a9e-71c2-4f0f-84e4-01eaf8090941)
![image](https://github.com/user-attachments/assets/ea26c372-f860-4d8e-8423-23c4073cde61)
![image](https://github.com/user-attachments/assets/992a3c7d-07fb-4977-bbbb-98f7c92f2e9f)













