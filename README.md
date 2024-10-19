## 🍳FoodKing 소개
<br>
한식, 양식, 일식, 중식 상관없이 모든 음식에 대한 레시피를 공유하는 서비스입니다.<br>
자신만의 레시피를 공유하거나 필요한 레시피를 손쉽게 찾아 요리할 수 있습니다.<br>

FoodKing은 대규모 트래픽을 가정하여 개발된 서비스입니다.<br>
<br>

## 🍳FoodKing의 주요 관심사
1. <strong>SOLID원칙</strong>을 통한 객체지향적인 코드개발<br>
2. 분산락을 통한 <strong>동시성 제어</strong><br>
3. 대규모 트래픽에 대비한 <strong>쿼리튜닝</strong><br>
4. <strong>Master-Slave Replication</strong> 을 통한 DB부하 분산<br>
5. nGrinder를 통한 <strong>성능 분석</strong><br>
6. <strong>N+1쿼리</strong> 지양
7. <strong>No Offset</strong>을 통한 페이징 성능 향상
8. <strong>비정규화</strong>를 통한 조회성능 향상
9. <strong>CI/CD 파이프라인 자동화</strong><br>
10. 지속적인 리팩토링을 통한 <strong>clean code</strong><br>
11. <strong>207개의 단위Test code</strong>를 통한 프로젝트 안정성 강화<br>
12. <strong>DockerHub</strong>을 통한 개발환경 통일<br>
13. <strong>캐싱</strong>을 통한 레시피 조회성능 향상<br>
14. <strong>사용자 인증</strong>의 문제점 개선<br>
15. <strong>캐시갱신</strong> 성능 향상<br>
16. <strong>Nginx</strong>을 통한 프록시서버
17. <strong>일관성</strong>있는 코드 컨벤션
18. <strong>Layered Architecture</strong>

## 🍳트러블 슈팅
- [FoodKing #1] 쿼리튜닝 - 레시피 단건조회(<strong>210%</strong>),페이징조회(<strong>790%</strong>) 로직의 성능을 향상시킨 이야기<br>
  https://psy217300.tistory.com/200<br><br>
- [FoodKing #2] 사용자 인증 - Token인증 적용 및 문제점을 해결해가는 과정<br>
  https://psy217300.tistory.com/201<br><br>
- [FoodKing #3] 분산락을 통해 동시성 제어를 해본 이야기 - 분산락을 선택한 이유<br>
  https://psy217300.tistory.com/202<br><br>
- [FoodKing #4] 캐싱 - 어느 곳에 캐싱을 적용해야 할 까?<br>
  https://psy217300.tistory.com/208<br><br>
- [FoodKing #5] 캐시 갱신방법 - keys * ,scan<br>
  https://psy217300.tistory.com/209<br><br>
- [FoodKing #6] Docker와 Git Action을 통한 CI/CD 자동화 구축<br>
  https://psy217300.tistory.com/225<br>

## 🍳사용 기술
- Java 17
- SpringBoot 2.7.5<br>
- JPA<br>
- MariaDB<br>
- Redis<br>
- Docker<br>
- Ngrinder<br>
- Gradle<br>

## 🍳FoodKing 구조도

![웹 아키텍쳐 구조](https://github.com/user-attachments/assets/a9df8fe6-ff9a-414d-9cd3-b9245f88cf4e)

## 🍳Foodking ERD

https://www.erdcloud.com/d/cDhCYyzgXiLxH5EJa
![image](https://github.com/user-attachments/assets/0dc9fb3f-3ddc-4ccb-97e9-acf6cab8c545)

## 🍳Front

![image](https://github.com/user-attachments/assets/5a482f5d-ea72-4661-87c9-dced46c6ff7c)
![image](https://github.com/user-attachments/assets/6db6a19a-5da5-4280-9920-68da5a76f5fb)
![image](https://github.com/user-attachments/assets/b5495a9e-71c2-4f0f-84e4-01eaf8090941)
![image](https://github.com/user-attachments/assets/ea26c372-f860-4d8e-8423-23c4073cde61)
![image](https://github.com/user-attachments/assets/992a3c7d-07fb-4977-bbbb-98f7c92f2e9f)













