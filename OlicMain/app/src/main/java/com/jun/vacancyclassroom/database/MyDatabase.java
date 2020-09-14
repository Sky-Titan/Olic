package com.jun.vacancyclassroom.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.WorkerThread;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jun.vacancyclassroom.interfaces.UpdateCallback;
import com.jun.vacancyclassroom.model.Building;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.model.SearchLecture;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

@Database(version = 1, entities = {Lecture.class, LectureRoom.class, Building.class, SearchLecture.class})
public abstract class MyDatabase extends RoomDatabase {

    public abstract MyDAO dao();
    private String semester="";
    private ArrayList<String> url_List=new ArrayList<>();
    private static MyDatabase instance;

    private static final String TAG = "MyDatabase";

    public synchronized static MyDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context, MyDatabase.class, "olic.db").build();
        }
        return instance;
    }

    //lecture 검색
    public Lecture searchLecture(String lecture_code)
    {
        Lecture lecture = null;
        try
        {
            String url = "http://my.knu.ac.kr/stpo/stpo/cour/lectReqCntEnq/list.action?lectReqCntEnq.search_subj_cde=" + lecture_code.substring(0, 7) + "&lectReqCntEnq.search_sub_class_cde=" + lecture_code.substring(7) + "&searchValue=" + lecture_code + "";

            final Document doc = Jsoup.connect(url).get();

            //System.out.println("main url : " + url);

            final Elements lectureCode = doc.select("td.subj_class_cde");//과목코드
            final Elements lectureName = doc.select("td.subj_nm");//과목이름
            final Elements lectureCredit = doc.select("td.unit");//학점
            final Elements lectureProfessor = doc.select("td.prof_nm");//강의교수
            final Elements lectureTime = doc.select("td.lect_wk_tm");//강의시간
            final Elements lectureQuota = doc.select("td.lect_quota");//수강정원
            final Elements lectureReq = doc.select("td.lect_req_cnt");//수강신청인원

            if(lectureCode.hasText())
            {
                lecture = new Lecture(lectureCode.get(0).text().trim(), lectureName.get(0).text().trim(), lectureCredit.get(0).text().trim(), lectureProfessor.get(0).text().trim()
                , lectureQuota.get(0).text().trim(), lectureReq.get(0).text().trim(),"", lectureTime.get(0).text().trim());
            }
            else//존재하지 않으면 삭제
                dao().deleteSearchLecture(new SearchLecture(lecture_code));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return lecture;
    }

    public int getUrlListSize()
    {
        return url_List.size();
    }

    //실행
    @WorkerThread
    public boolean doUpdate(int year, String semester, UpdateCallback callback) throws IOException, IllegalStateException
    {
        this.semester = year+semester;

        url_List.clear();
        //정규학기
        if(semester.equals("2") || semester.equals("1"))
            add_url();
        else//계절학기
            add_url2();


            for(int i=0;i<url_List.size();i++) {//url 리스트 만큼 반복
                Document doc = Jsoup.connect(url_List.get(i)).get();

                Log.i(TAG,(i + 1) + "번째 페이지");
                callback.OnUpdate(i);

                //테스트1
                Elements lectureCode = doc.select("td.th4");//과목코드
                Elements lectureName = doc.select("td.th5");//과목이름
                Elements lectureCredit = doc.select("td.th6");//이수학점
                Elements professor = doc.select("td.th9");//담당 교수
                Elements quota = doc.select("td.th12");//수강정원
                Elements peopleNumber = doc.select("td.th13");//신청인원
                Elements lectureRoom = doc.select("td.th11");//강의실
                Elements lectureTime = doc.select("td.th17");//강의시간

                for (int j = 0; j < lectureCode.size(); j++)
                {
                    if(!lectureRoom.get(j).text().trim().isEmpty() && !lectureRoom.get(j).text().trim().equals("-"))
                    {
                        dao().insertLecture(new Lecture(lectureCode.get(j).text().trim(), lectureName.get(j).text().trim()
                                ,lectureCredit.get(j).text().trim(), professor.get(j).text().trim(), quota.get(j).text().trim(),
                                peopleNumber.get(j).text().trim(),lectureRoom.get(j).text().trim() , lectureTime.get(j).text().trim()));

                        dao().insertLectureRoom(new LectureRoom(lectureRoom.get(j).text().trim()));

                        StringTokenizer strtok = new StringTokenizer(lectureRoom.get(j).text().trim(), "-");

                        if(strtok.hasMoreTokens())
                            dao().insertBuilding(new Building(strtok.nextToken()));
                    }
                }
            }

            return true;

    }

    //계절학기 링크
    private void add_url2(){
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_gubun=2");
    }

    //정규학기 링크
    private void add_url(){

        //첨성인기초 - 독서와토론
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1A01&search_open_yr_trm="+semester);
        //첨성인기초 - 사고 교육
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1A02&search_open_yr_trm="+semester);
        //첨성인기초 - 글쓰기
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1A03&search_open_yr_trm="+semester);
        //첨성인기초 - 실용영어
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1A04&search_open_yr_trm="+semester);
        //첨성인기초 - 소프트웨어
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1A05&search_open_yr_trm="+semester);
        //첨성인기초 - 인문.사회 - 언어와문학
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0101&search_open_yr_trm="+semester);
        //첨성인기초 - 인문.사회 - 사상과가치
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0102&search_open_yr_trm="+semester);
        //첨성인기초 - 인문.사회 - 역사와문학
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0103&search_open_yr_trm="+semester);
        //첨성인기초 - 인문.사회 - 사회와제도
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0104&search_open_yr_trm="+semester);
        //첨성인기초 - 인문.사회 - 외국어
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0105&search_open_yr_trm="+semester);
        //첨성인기초 - 자연과학 - 수리
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0201&search_open_yr_trm="+semester);
        //첨성인기초 - 자연과학 - 기초과학
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0202&search_open_yr_trm="+semester);
        //첨성인기초 - 자연과학 - 자연과환경
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1B0203&search_open_yr_trm="+semester);
        //첨성인일반
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=1C&search_open_yr_trm="+semester);
        //교직과목
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_div_cde=07&search_open_yr_trm="+semester);
        //군사학
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_subj_area_cde=46&search_open_yr_trm="+semester);

        //대학원 - FTA통상학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=21159&sub=211&search_open_yr_trm="+semester);
        //대학원 - 가정교육학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2113A&sub=211&search_open_yr_trm="+semester);
        //대학원 - 간호학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2112Z&sub=211&search_open_yr_trm="+semester);
        //대학원 - 건설방재공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=21147&sub=211&search_open_yr_trm="+semester);
        //대학원 - 건설환경에너지공학부 건축공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2115701&sub=211&search_open_yr_trm="+semester);
        //대학원 - 건설환경에너지공학부 토목공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2115702&sub=211&search_open_yr_trm="+semester);
        //대학원 - 건설환경에너지공학부 환경에너지공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2115703&sub=211&search_open_yr_trm="+semester);
        //대학원 - 건축학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=21156&sub=211&search_open_yr_trm="+semester);
        //대학원 - 경영학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2113D&sub=211&search_open_yr_trm="+semester);
        //대학원 - 경제학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=21112&sub=211&search_open_yr_trm="+semester);
        //대학원 - 고고인류학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2110B&sub=211&search_open_yr_trm="+semester);
        //대학원 - 고전번역학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=21165&sub=211&search_open_yr_trm="+semester);
        //대학원 - 공간정보학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2113U&sub=211&search_open_yr_trm="+semester);
        //대학원 - 과학교육학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2110Y&sub=211&search_open_yr_trm="+semester);
        //대학원 - 교육학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2112S&sub=211&search_open_yr_trm="+semester);
        //대학원 - 국악학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2112Q&sub=211&search_open_yr_trm="+semester);
        //대학원 - 국어교육학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2112V&sub=211&search_open_yr_trm="+semester);
        //대학원 - 국어국문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=21101&sub=211&search_open_yr_trm="+semester);
        //대학원 - 기계공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2111F&sub=211&search_open_yr_trm="+semester);
        //대학원 - 기능물질공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2113I&sub=211&search_open_yr_trm="+semester);

        //교육대학원 - 가정교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000T&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 간호교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000I&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 교육과정및교육공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220008&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 교육사회및평생교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220009&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 교육심리및교육평가전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220019&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 교육철학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220002&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 교육행정전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220003&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 국어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000A&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 기술교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000X&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 농업교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000V&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 물리교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000O&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 미술교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000Z&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 불어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000E&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 상담심리전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220005&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 생물교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000Q&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 수학교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000N&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 역사교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000G&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 영양교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2220011&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 영어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000C&sub=222&search_open_yr_trm="+semester);
        //교육대학원 - 윤리교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=222000L&sub=222&search_open_yr_trm="+semester);

        //행정대학원 - 공공관리전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2230008&sub=223&search_open_yr_trm="+semester);
        //행정대학원 - 법무.안전전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=223000A&sub=223&search_open_yr_trm="+semester);
        //행정대학원 - 정책.도시전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2230009&sub=223&search_open_yr_trm="+semester);
        //행정대학원 - 지방자치전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2230005&sub=223&search_open_yr_trm="+semester);
        //행정대학원 - 행정대학원
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=223&sub=223&search_open_yr_trm="+semester);

        //경영대학원 - CMBA전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=224003F&sub=224&search_open_yr_trm="+semester);
        //경영대학원 - 경영학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=224000D&sub=224&search_open_yr_trm="+semester);

        //보건대학원 - 보건관리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22505&sub=225&search_open_yr_trm="+semester);
        //보건대학원 - 역학및건강증진학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22504&sub=225&search_open_yr_trm="+semester);

        //산업대학원 - 산업공학과 건축공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010D&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 고분자공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010H&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 금속신소재공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010C&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 기계공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2260109&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 기술정책전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010L&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 무기재료공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010I&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 반도체공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2260106&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 섬유시스템공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010O&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 응용화학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010M&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 전기공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010F&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 제어및계측공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2260103&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 컴퓨터공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2260108&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 토목공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010E&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 통신공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2260104&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 화학공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010G&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 환경공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=226010K&sub=226&search_open_yr_trm="+semester);
        //산업대학원 - 산업공학과 회로및시스템전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2260102&sub=226&search_open_yr_trm="+semester);

        //국제대학원 - 국제문화학과 다문화학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280302&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제문화학과 외국어로서의한국어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280303&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제문화학과 한국문화학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280301&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제지역학과 국제정치학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280401&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제지역학과 미국학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280404&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제지역학과 북한학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280405&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제지역학과 일본학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280402&sub=228&search_open_yr_trm="+semester);
        //국제대학원 - 국제지역학과 중국학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2280403&sub=228&search_open_yr_trm="+semester);

        //농업생명융합대학원 - 농산물안전성학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22G0005&sub=22G&search_open_yr_trm="+semester);
        //농업생명융합대학원 - 농업자원학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22G0001&sub=22G&search_open_yr_trm="+semester);
        //농업생명융합대학원 - 농업정책및유통전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22G0009&sub=22G&search_open_yr_trm="+semester);
        //농업생명융합대학원 - 농촌개발전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22G0004&sub=22G&search_open_yr_trm="+semester);
        //농업생명융합대학원 - 식품산업공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22G0002&sub=22G&search_open_yr_trm="+semester);
        //농업생명융합대학원 - 환경조경학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22G0007&sub=22G&search_open_yr_trm="+semester);

        //정책정보대학원 - 도시및지역개발전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2290003&sub=229&search_open_yr_trm="+semester);
        //정책정보대학원 - 디지털정보관리전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=229000B&sub=229&search_open_yr_trm="+semester);
        //정책정보대학원 - 사회복지정책및행정전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=229000A&sub=229&search_open_yr_trm="+semester);
        //정책정보대학원 - 사회정책및NGO전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2290002&sub=229&search_open_yr_trm="+semester);
        //정책정보대학원 - 언론홍보전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2290005&sub=229&search_open_yr_trm="+semester);
        //정책정보대학원 - 정치리더십전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2290008&sub=229&search_open_yr_trm="+semester);
        //정책정보대학원 - 커뮤니티및사이버심리전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=2290006&sub=229&search_open_yr_trm="+semester);

        //수사과학대학원 - 과학수사학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22A03&sub=22A&search_open_yr_trm="+semester);
        //수사과학대학원 - 법의간호학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22A02&sub=22A&search_open_yr_trm="+semester);
        //수사과학대학원 - 법정의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=22A01&sub=22A&search_open_yr_trm="+semester);

        //법학전문대학원 - 법학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=23D01&sub=23D&search_open_yr_trm="+semester);

        //인문대-고고인류학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1108&sub=11&search_open_yr_trm="+semester);
        //인문대-국어국문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1101&sub=11&search_open_yr_trm="+semester);
        //인문대-노어노문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=110B&sub=11&search_open_yr_trm="+semester);
        //인문대-독어독문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1104&sub=11&search_open_yr_trm="+semester);
        //인문대-문화콘텐츠개발융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=110H&sub=11&search_open_yr_trm="+semester);
        //인문대-불어불문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1103&sub=11&search_open_yr_trm="+semester);
        //인문대-사학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1106&sub=11&search_open_yr_trm="+semester);
        //인문대-영어영문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1102&sub=11&search_open_yr_trm="+semester);
        //인문대-인문대
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=11&sub=11&search_open_yr_trm="+semester);
        //인문대-일어일문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1109&sub=11&search_open_yr_trm="+semester);
        //인문대-중어중문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1105&sub=11&search_open_yr_trm="+semester);
        //인문대-철학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1107&sub=11&search_open_yr_trm="+semester);
        //인문대-한문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=110A&sub=11&search_open_yr_trm="+semester);

        //사회과학대-IT정치융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120B&sub=12&search_open_yr_trm="+semester);
        //사회과학대-디지털정보관리융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120A&sub=12&search_open_yr_trm="+semester);
        //사회과학대-문헌정보학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1204&sub=12&search_open_yr_trm="+semester);
        //사회과학대-사회복지학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1209&sub=12&search_open_yr_trm="+semester);
        //사회과학대-사회복지학부 사회복지거시전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120902&sub=12&search_open_yr_trm="+semester);
        //사회과학대-사회복지학부 사회복지미시전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120901&sub=12&search_open_yr_trm="+semester);
        //사회과학대-사회학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1202&sub=12&search_open_yr_trm="+semester);
        //사회과학대-신문방송학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1207&sub=12&search_open_yr_trm="+semester);
        //사회과학대-심리정보융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120C&sub=12&search_open_yr_trm="+semester);
        //사회과학대-심리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1205&sub=12&search_open_yr_trm="+semester);
        //사회과학대-정치외교학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1201&sub=12&search_open_yr_trm="+semester);
        //사회과학대-지리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1203&sub=12&search_open_yr_trm="+semester);

        //자연과학대학-물리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130A&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-생명과학부 생명공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130705&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-생명과학부 생물학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130701&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-수학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1301&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-지구시스템과학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-지구시스템과학부 지질학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q01&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-지구시스템과학부 천문대기과학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q02&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-지구시스템과학부 해양학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q03&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-통계학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1304&sub=13&search_open_yr_trm="+semester);
        //자연과학대학-화학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1302&sub=13&search_open_yr_trm="+semester);

        //경상대학-경영학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403&sub=14&search_open_yr_trm="+semester);
        //경상대학-경영학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403001&sub=14&search_open_yr_trm="+semester);
        //경상대학-경영학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403002&sub=14&search_open_yr_trm="+semester);
        //경상대학-경영학부C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403003&sub=14&search_open_yr_trm="+semester);
        //경상대학-경제통상학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404&sub=14&search_open_yr_trm="+semester);
        //경상대학-경제통상학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404001&sub=14&search_open_yr_trm="+semester);
        //경상대학-경제통상학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404002&sub=14&search_open_yr_trm="+semester);
        //경상대학-경제통상학부C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404003&sub=14&search_open_yr_trm="+semester);
        //경상대학-비즈니스인텔리전스융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1407&sub=14&search_open_yr_trm="+semester);

        //법과대학-법학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1508&sub=15&search_open_yr_trm="+semester);
        //법과대학-법학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1502&sub=15&search_open_yr_trm="+semester);

        //공과대학-건축학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160I&sub=16&search_open_yr_trm="+semester);
        //공과대학-건축학부 건축공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160I01&sub=16&search_open_yr_trm="+semester);
        //공과대학-건축학부 건축학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160I02&sub=16&search_open_yr_trm="+semester);
        //공과대학-고분자공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1605&sub=16&search_open_yr_trm="+semester);
        //공과대학-기계공학과A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1601001&sub=16&search_open_yr_trm="+semester);
        //공과대학-기계공학과B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1601002&sub=16&search_open_yr_trm="+semester);
        //공과대학-기계공학과C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1601003&sub=16&search_open_yr_trm="+semester);
        //공과대학-기계공학부 기계공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160101&sub=16&search_open_yr_trm="+semester);
        //공과대학-기계공학부 기계설계학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160102&sub=16&search_open_yr_trm="+semester);
        //공과대학-섬유시스템공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1607&sub=16&search_open_yr_trm="+semester);
        //공과대학-신소재공학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1609001&sub=16&search_open_yr_trm="+semester);
        //공과대학-신소재공학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1609002&sub=16&search_open_yr_trm="+semester);
        //공과대학-신소재공학부 금속신소재공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160903&sub=16&search_open_yr_trm="+semester);
        //공과대학-신소재공학부 전자재료공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160904&sub=16&search_open_yr_trm="+semester);
        //공과대학-에너지공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1611&sub=16&search_open_yr_trm="+semester);
        //공과대학-에너지공학부 신재생에너지전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=161101&sub=16&search_open_yr_trm="+semester);
        //공과대학-에너지공학부 에너지변환전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=161102&sub=16&search_open_yr_trm="+semester);
        //공과대학-응용화학공학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1612001&sub=16&search_open_yr_trm="+semester);
        //공과대학-응용화학공학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1612002&sub=16&search_open_yr_trm="+semester);
        //공과대학-응용화학공학부 응용화학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=161201&sub=16&search_open_yr_trm="+semester);

        //농업생명과학대학-농산업학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170R&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-농업경제학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170A&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-농업토목.생물산업공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170T&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-농업토목.생물산업공학부 농업토목공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170T01&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-농업토목.생물산업공학부 생물산업기계공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170T02&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-바이오섬유소재학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170Q&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-산림과학.조경학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-산림과학.조경학부 임산공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S02&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-산림과학.조경학부 임학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S01&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-산림과학.조경학부 조경학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S03&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-생물정보융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170V&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-스마트팜공학융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170U&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-식품공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-식품공학부 식품생물공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P01&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-식품공학부 식품소재공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P02&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-식품공학부 식품응용공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P03&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-원예과학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170O&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-원예식품공학융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170W&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-응용생명과학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170B&sub=17&search_open_yr_trm="+semester);
        //농업생명과학대학-응용생명과학부 식물생명과학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170B01&sub=17&search_open_yr_trm="+semester);

        //사범대학-통합과학교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=191G&sub=19&search_open_yr_trm="+semester);
        //사범대학-가정교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190A&sub=19&search_open_yr_trm="+semester);
        //사범대학-가정교육과(기술,가정) 기술,가정교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190B&sub=19&search_open_yr_trm="+semester);
        //사범대학-교육학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1901&sub=19&search_open_yr_trm="+semester);
        //사범대학-국어교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1902&sub=19&search_open_yr_trm="+semester);
        //사범대학-물리교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190H&sub=19&search_open_yr_trm="+semester);
        //사범대학-사범대학
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=19&sub=19&search_open_yr_trm="+semester);
        //사범대학-생물교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190J&sub=19&search_open_yr_trm="+semester);
        //사범대학-수학교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1908&sub=19&search_open_yr_trm="+semester);
        //사범대학-역사교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190E&sub=19&search_open_yr_trm="+semester);
        //사범대학-영어교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1903&sub=19&search_open_yr_trm="+semester);
        //사범대학-유럽어교육학부 독어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190D01&sub=19&search_open_yr_trm="+semester);
        //사범대학-유럽어교육학부 불어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190D02&sub=19&search_open_yr_trm="+semester);
        //사범대학-윤리교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1907&sub=19&search_open_yr_trm="+semester);
        //사범대학-일반사회교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190G&sub=19&search_open_yr_trm="+semester);
        //사범대학-지구과학교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190K&sub=19&search_open_yr_trm="+semester);
        //사범대학-지리교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190F&sub=19&search_open_yr_trm="+semester);
        //사범대학-체육교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190C&sub=19&search_open_yr_trm="+semester);
        //사범대학-화학교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190I&sub=19&search_open_yr_trm="+semester);

        //예술대학-국악학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1803&sub=18&search_open_yr_trm="+semester);
        //예술대학-디자인학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1806&sub=18&search_open_yr_trm="+semester);
        //예술대학-미술학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1804&sub=18&search_open_yr_trm="+semester);
        //예술대학-음악학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1801&sub=18&search_open_yr_trm="+semester);

        //의과대학-의예과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1F04&sub=1F&search_open_yr_trm="+semester);
        //의과대학-의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1F01&sub=1F&search_open_yr_trm="+semester);

        //치과대학-치의예과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1G02&sub=1G&search_open_yr_trm="+semester);
        //치과대학-치의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1G01&sub=1G&search_open_yr_trm="+semester);

        //수의과대학-수의예과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1A02&sub=1A&search_open_yr_trm="+semester);
        //수의과대학-수의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1A01&sub=1A&search_open_yr_trm="+semester);

        //생활과학대학-식품영양학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B04&sub=1B&search_open_yr_trm="+semester);
        //생활과학대학-아동학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B07&sub=1B&search_open_yr_trm="+semester);
        //생활과학대학-아동학부 아동가족학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B0701&sub=1B&search_open_yr_trm="+semester);
        //생활과학대학-아동학부 아동학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B0702&sub=1B&search_open_yr_trm="+semester);
        //생활과학대학-의류학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B03&sub=1B&search_open_yr_trm="+semester);

        //자율전공부-인문사회자율전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1E01&sub=1E&search_open_yr_trm="+semester);
        //자율전공부-자연과학자율전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1E02&sub=1E&search_open_yr_trm="+semester);
        //자율전공부-자율전공부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1E&sub=1E&search_open_yr_trm="+semester);

        //간호대학-간호학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1C01&sub=1C&search_open_yr_trm="+semester);

        //IT대학-건설IT전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O07&sub=1O&search_open_yr_trm="+semester);
        //IT대학-미디어아트전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O09&sub=1O&search_open_yr_trm="+semester);
        //IT대학-빅데이터전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O06&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전기공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O03&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01001&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01002&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01003&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부D
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01004&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부E
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01005&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부F
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01006&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부H
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01007&sub=1O&search_open_yr_trm="+semester);
        //IT대학-전자공학부 모바일공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O0101&sub=1O&search_open_yr_trm="+semester);
        //IT대학-컴퓨터학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O02&sub=1O&search_open_yr_trm="+semester);
        //IT대학-컴퓨터학부 글로벌소프트웨어융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O0204&sub=1O&search_open_yr_trm="+semester);
        //IT대학-핀테크전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O08&sub=1O&search_open_yr_trm="+semester);

        //글로벌인재학부-글로벌인재학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1P03&sub=1P&search_open_yr_trm="+semester);

        //약학대학-약학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1Q01&sub=1P&search_open_yr_trm="+semester);


    }
}
