# Olic
경북대학교 빈 강의실 찾기 애플리케이션
> Kyungpook National University Empty Classroom Finding Application 

<img src=./img/olic_icon.png width="250">


## About
------------------
- __Google Play :__ https://play.google.com/store/apps/details?id=com.jun.vacancyclassroom
- __Developer E-mail :__ er65119@naver.com


## Project
--------------------
경북대학교에 재학 중인 학생들이 현재 수업이 없는 빈 강의실을 편하게 찾아갈 수 있도록 강의실별 시간표를 제공해주는 안드로이드 애플리케이션입니다.  
> It is an Android application that provides a timetable for each classroom so that students attending Kyungpook National University can easily visit empty classrooms without classes.

## Develop Environment
--------------------
- __OS :__ 
  - Android
- __Language :__ 
  - Java
- __Library :__ 
  - Jsoup
- __Use Tech Stack :__ 
  - Room
  - ViewModel
  - LiveData
  - Databinding
  - MVVM
  
## Functions
--------------------
### 1. Lectureroom Search 강의실 검색
- 강의실 검색 페이지에서 원하는 강의실을 검색할 수 있습니다.
- 즐겨찾기에 추가 혹은 삭제를 원한다면 항목을 클릭하면 자동으로 추가, 한 번 더 클릭하면 삭제됩니다.  

>- The class want in the search page, classroom can be retrieved.
>- Click an item if you want to delete or add to the favorites are automatically added, deleted by clicking one more time.

<img src=./img/1_mockup.png width="500">  


### 2. Favorites 즐겨찾기
- 즐겨찾기 화면에서 즐겨찾기에 추가한 강의실들의 목록을 볼 수 있습니다.
- 항목별 우측에 있는 색깔 표시기가 강의실 수업 여부를 알려줍니다. (빨강 : 수업 있음, 초록 : 수업 없음)
- 시간 설정 기능을 이용하여 현재 시각이 아닌 다른 시간대에 강의실 수업 여부도 알려줍니다.  

>- On the Favorites screen, you can view a list of rooms you have added to your favorites.
>- A color indicator on the right side of each item will tell you if you are in a classroom. (Red: Class; Green: No Class)
>- Using the time setting feature, it also tells you if a class is available in a different time zone than the current time.

<img src=./img/2_mockup.png width="500">  

### 3. Building Search 건물 검색
- 건물별로 강의실들을 확인할 수 있습니다.
- 예를 들어 IT융복합관의 강의실들을 전부 확인하고 싶다면 IT융복합관을 검색하여서 클릭하면 IT융복합관의 모든 강의실의 수업 여부를 확인할 수 있습니다.  

>- Classrooms can be checked by building.
>- For example, if you want to check all the classrooms in the IT Convergence Center, search and click on the IT Convergence Center to see if all the classrooms in the IT Convergence Center are occupied.  

<img src=./img/3_mockup.png width="500">  
<img src=./img/time_table_mockup.png width="500">  
  
### 4. Course Status 수강현황
- 수강신청 기간에 수강신청에 실패했을 경우, 본인이 원하는 강의의 수강인원이 빠지는 것을 확인하기 위해 만든 기능입니다.
- 강의실과 마찬가지로 수강신청이 가능한 상태면 초록, 불가능하면 빨강으로 색깔이 표시됩니다.  

>- This function was created to check that if the enrollment fails during the course registration period, the number of students in the class you want will be omitted.
>- Similar to the classroom, color will be green if the class is available, or red if not.
  
<img src=./img/4_mockup.png width="500">  
  
## 5. Synchronization 동기화
- 매 학기가 시작되는 3월(1학기), 7월(여름 계절학기), 9월(2학기), 1월(겨울 계절학기)이 되면 자동으로 시간표가 업데이트됩니다.
- 수동으로 동기화를 원할 시 오른쪽 위의 동기화 버튼을 클릭하면 시간표가 업데이트됩니다.
- 수강현황 화면에서 동기화 버튼을 클릭할 시 수강현황이 업데이트됩니다.  

>- The timetable is automatically updated in March (1st semester), July (summer seasonal semester), September (2nd semester), and January (winter seasonal semester).
>- If you want to sync manually, click the Sync button in the upper right corner to update the timetable.
>- Click the Sync button on the course status screen to update the course status.
