package com.example.vacancyclassroom;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> url_List=new ArrayList<>();
    private ArrayList<Lecture> mysql_List=new ArrayList<>();
    private String htmlPageUrl = "http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O07&sub=1O&search_open_yr_trm=20191"; //파싱할 홈페이지의 URL주소
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat="";
    String myJSON;
    JSONArray lectures = null;
    int cnt=0;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_CLASSROOM = "classroom";
    private static final String TAG_CODE = "code";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TIME = "time";

    public void add_url(){
        //인문대-고고인류학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1108&sub=11&search_open_yr_trm=20191");
        //인문대-국어국문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1101&sub=11&search_open_yr_trm=20191");
        //인문대-노어노문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=110B&sub=11&search_open_yr_trm=20191");
        //인문대-독어독문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1104&sub=11&search_open_yr_trm=20191");
        //인문대-문화콘텐츠개발융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=110H&sub=11&search_open_yr_trm=20191");
        //인문대-불어불문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1103&sub=11&search_open_yr_trm=20191");
        //인문대-사학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1106&sub=11&search_open_yr_trm=20191");
        //인문대-영어영문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1102&sub=11&search_open_yr_trm=20191");
        //인문대-인문대
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=11&sub=11&search_open_yr_trm=20191");
        //인문대-일어일문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1109&sub=11&search_open_yr_trm=20191");
        //인문대-중어중문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1105&sub=11&search_open_yr_trm=20191");
        //인문대-철학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1107&sub=11&search_open_yr_trm=20191");
        //인문대-한문학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=110A&sub=11&search_open_yr_trm=20191");

        //사회과학대-IT정치융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120B&sub=12&search_open_yr_trm=20191");
        //사회과학대-디지털정보관리융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120A&sub=12&search_open_yr_trm=20191");
        //사회과학대-문헌정보학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1204&sub=12&search_open_yr_trm=20191");
        //사회과학대-사회복지학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1209&sub=12&search_open_yr_trm=20191");
        //사회과학대-사회복지학부 사회복지거시전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120902&sub=12&search_open_yr_trm=20191");
        //사회과학대-사회복지학부 사회복지미시전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120901&sub=12&search_open_yr_trm=20191");
        //사회과학대-사회학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1202&sub=12&search_open_yr_trm=20191");
        //사회과학대-신문방송학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1207&sub=12&search_open_yr_trm=20191");
        //사회과학대-심리정보융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=120C&sub=12&search_open_yr_trm=20191");
        //사회과학대-심리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1205&sub=12&search_open_yr_trm=20191");
        //사회과학대-정치외교학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1201&sub=12&search_open_yr_trm=20191");
        //사회과학대-지리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1203&sub=12&search_open_yr_trm=20191");

        //자연과학대학-물리학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130A&sub=13&search_open_yr_trm=20191");
        //자연과학대학-생명과학부 생명공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130705&sub=13&search_open_yr_trm=20191");
        //자연과학대학-생명과학부 생물학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130701&sub=13&search_open_yr_trm=20191");
        //자연과학대학-수학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1301&sub=13&search_open_yr_trm=20191");
        //자연과학대학-지구시스템과학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q&sub=13&search_open_yr_trm=20191");
        //자연과학대학-지구시스템과학부 지질학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q01&sub=13&search_open_yr_trm=20191");
        //자연과학대학-지구시스템과학부 천문대기과학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q02&sub=13&search_open_yr_trm=20191");
        //자연과학대학-지구시스템과학부 해양학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=130Q03&sub=13&search_open_yr_trm=20191");
        //자연과학대학-통계학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1304&sub=13&search_open_yr_trm=20191");
        //자연과학대학-화학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1302&sub=13&search_open_yr_trm=20191");

        //경상대학-경영학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403&sub=14&search_open_yr_trm=20191");
        //경상대학-경영학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403001&sub=14&search_open_yr_trm=20191");
        //경상대학-경영학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403002&sub=14&search_open_yr_trm=20191");
        //경상대학-경영학부C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1403003&sub=14&search_open_yr_trm=20191");
        //경상대학-경제통상학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404&sub=14&search_open_yr_trm=20191");
        //경상대학-경제통상학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404001&sub=14&search_open_yr_trm=20191");
        //경상대학-경제통상학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404002&sub=14&search_open_yr_trm=20191");
        //경상대학-경제통상학부C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1404003&sub=14&search_open_yr_trm=20191");
        //경상대학-비즈니스인텔리전스융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1407&sub=14&search_open_yr_trm=20191");

        //법과대학-법학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1508&sub=15&search_open_yr_trm=20191");
        //법과대학-법학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1502&sub=15&search_open_yr_trm=20191");

        //공과대학-건축학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160I&sub=16&search_open_yr_trm=20191");
        //공과대학-건축학부 건축공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160I01&sub=16&search_open_yr_trm=20191");
        //공과대학-건축학부 건축학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160I02&sub=16&search_open_yr_trm=20191");
        //공과대학-고분자공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1605&sub=16&search_open_yr_trm=20191");
        //공과대학-기계공학과A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1601001&sub=16&search_open_yr_trm=20191");
        //공과대학-기계공학과B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1601002&sub=16&search_open_yr_trm=20191");
        //공과대학-기계공학과C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1601003&sub=16&search_open_yr_trm=20191");
        //공과대학-기계공학부 기계공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160101&sub=16&search_open_yr_trm=20191");
        //공과대학-기계공학부 기계설계학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160102&sub=16&search_open_yr_trm=20191");
        //공과대학-섬유시스템공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1607&sub=16&search_open_yr_trm=20191");
        //공과대학-신소재공학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1609001&sub=16&search_open_yr_trm=20191");
        //공과대학-신소재공학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1609002&sub=16&search_open_yr_trm=20191");
        //공과대학-신소재공학부 금속신소재공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160903&sub=16&search_open_yr_trm=20191");
        //공과대학-신소재공학부 전자재료공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=160904&sub=16&search_open_yr_trm=20191");
        //공과대학-에너지공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1611&sub=16&search_open_yr_trm=20191");
        //공과대학-에너지공학부 신재생에너지전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=161101&sub=16&search_open_yr_trm=20191");
        //공과대학-에너지공학부 에너지변환전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=161102&sub=16&search_open_yr_trm=20191");
        //공과대학-응용화학공학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1612001&sub=16&search_open_yr_trm=20191");
        //공과대학-응용화학공학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1612002&sub=16&search_open_yr_trm=20191");
        //공과대학-응용화학공학부 응용화학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=161201&sub=16&search_open_yr_trm=20191");

        //농업생명과학대학-농산업학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170R&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-농업경제학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170A&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-농업토목.생물산업공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170T&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-농업토목.생물산업공학부 농업토목공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170T01&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-농업토목.생물산업공학부 생물산업기계공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170T02&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-바이오섬유소재학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170Q&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-산림과학.조경학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-산림과학.조경학부 임산공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S02&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-산림과학.조경학부 임학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S01&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-산림과학.조경학부 조경학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170S03&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-생물정보융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170V&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-스마트팜공학융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170U&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-식품공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-식품공학부 식품생물공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P01&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-식품공학부 식품소재공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P02&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-식품공학부 식품응용공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170P03&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-원예과학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170O&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-원예식품공학융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170W&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-응용생명과학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170B&sub=17&search_open_yr_trm=20191");
        //농업생명과학대학-응용생명과학부 식물생명과학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=170B01&sub=17&search_open_yr_trm=20191");

        //사범대학-통합과학교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=191G&sub=19&search_open_yr_trm=20191");
        //사범대학-가정교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190A&sub=19&search_open_yr_trm=20191");
        //사범대학-가정교육과(기술,가정) 기술,가정교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190B&sub=19&search_open_yr_trm=20191");
        //사범대학-교육학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1901&sub=19&search_open_yr_trm=20191");
        //사범대학-국어교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1902&sub=19&search_open_yr_trm=20191");
        //사범대학-물리교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190H&sub=19&search_open_yr_trm=20191");
        //사범대학-사범대학
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=19&sub=19&search_open_yr_trm=20191");
        //사범대학-생물교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190J&sub=19&search_open_yr_trm=20191");
        //사범대학-수학교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1908&sub=19&search_open_yr_trm=20191");
        //사범대학-역사교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190E&sub=19&search_open_yr_trm=20191");
        //사범대학-영어교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1903&sub=19&search_open_yr_trm=20191");
        //사범대학-유럽어교육학부 독어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190D01&sub=19&search_open_yr_trm=20191");
        //사범대학-유럽어교육학부 불어교육전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190D02&sub=19&search_open_yr_trm=20191");
        //사범대학-윤리교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1907&sub=19&search_open_yr_trm=20191");
        //사범대학-일반사회교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190G&sub=19&search_open_yr_trm=20191");
        //사범대학-지구과학교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190K&sub=19&search_open_yr_trm=20191");
        //사범대학-지리교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190F&sub=19&search_open_yr_trm=20191");
        //사범대학-체육교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190C&sub=19&search_open_yr_trm=20191");
        //사범대학-화학교육과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=190I&sub=19&search_open_yr_trm=20191");

        //예술대학-국악학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1803&sub=18&search_open_yr_trm=20191");
        //예술대학-디자인학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1806&sub=18&search_open_yr_trm=20191");
        //예술대학-미술학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1804&sub=18&search_open_yr_trm=20191");
        //예술대학-음악학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1801&sub=18&search_open_yr_trm=20191");

        //의과대학-의예과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1F04&sub=1F&search_open_yr_trm=20191");
        //의과대학-의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1F01&sub=1F&search_open_yr_trm=20191");

        //치과대학-치의예과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1G02&sub=1G&search_open_yr_trm=20191");
        //치과대학-치의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1G01&sub=1G&search_open_yr_trm=20191");

        //수의과대학-수의예과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1A02&sub=1A&search_open_yr_trm=20191");
        //수의과대학-수의학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1A01&sub=1A&search_open_yr_trm=20191");

        //생활과학대학-식품영양학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B04&sub=1B&search_open_yr_trm=20191");
        //생활과학대학-아동학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B07&sub=1B&search_open_yr_trm=20191");
        //생활과학대학-아동학부 아동가족학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B0701&sub=1B&search_open_yr_trm=20191");
        //생활과학대학-아동학부 아동학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B0702&sub=1B&search_open_yr_trm=20191");
        //생활과학대학-의류학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1B03&sub=1B&search_open_yr_trm=20191");

        //자율전공부-인문사회자율전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1E01&sub=1E&search_open_yr_trm=20191");
        //자율전공부-자연과학자율전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1E02&sub=1E&search_open_yr_trm=20191");
        //자율전공부-자율전공부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1E&sub=1E&search_open_yr_trm=20191");

        //간호대학-간호학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1C01&sub=1C&search_open_yr_trm=20191");

        //IT대학-건설IT전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O07&sub=1O&search_open_yr_trm=20191");
        //IT대학-미디어아트전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O09&sub=1O&search_open_yr_trm=20191");
        //IT대학-빅데이터전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O06&sub=1O&search_open_yr_trm=20191");
        //IT대학-전기공학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O03&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부A
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01001&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부B
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01002&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부C
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01003&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부D
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01004&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부E
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01005&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부F
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01006&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부H
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O01007&sub=1O&search_open_yr_trm=20191");
        //IT대학-전자공학부 모바일공학전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O0101&sub=1O&search_open_yr_trm=20191");
        //IT대학-컴퓨터학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O02&sub=1O&search_open_yr_trm=20191");
        //IT대학-컴퓨터학부 글로벌소프트웨어융합전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O0204&sub=1O&search_open_yr_trm=20191");
        //IT대학-핀테크전공
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1O08&sub=1O&search_open_yr_trm=20191");

        //글로벌인재학부-글로벌인재학부
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1P03&sub=1P&search_open_yr_trm=20191");

        //약학대학-약학과
        url_List.add("http://my.knu.ac.kr/stpo/stpo/cour/listLectPln/list.action?search_open_crse_cde=1Q01&sub=1P&search_open_yr_trm=20191");





    }
    public void loadDB(){
        SQLiteDatabase db = openOrCreateDatabase(
                "lecture_list.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);

   //     db.execSQL("DROP TABLE IF EXISTS lecture;");
        db.execSQL("CREATE TABLE IF NOT EXISTS lecture"
                +"(code TEXT PRIMARY KEY,title TEXT,classroom TEXT,time TEXT);");
    /*    Cursor c= db.rawQuery("SELECT * FROM lecture",null);

        int i=0;
        while(c.moveToNext()){
         c.getString(0);
        }*/

        if(db!=null){
            db.close();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  add_url(); //url들 배열에 추가
    //    loadDB();//db 불러오기
      //  sending();//mysql로 보내기
       // compare();


        textviewHtmlDocument = (TextView)findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기

        Button htmlTitleButton = (Button)findViewById(R.id.button);
    /*    htmlTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //파싱시작
                System.out.println( (cnt+1) +"번째 파싱");
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
                cnt++;
            }
        });*/
    }
    public void sending(){

        InsertData task = new InsertData();
        //121.182.35.52
        task.execute("http://ec2-54-180-150-103.ap-northeast-2.compute.amazonaws.com/insert_lecture.php");
        //http://ec2-54-180-150-103.ap-northeast-2.compute.amazonaws.com/insert_lecture.php
    }

    public void compare(){

        getData("http://121.182.35.52/vacancyclassroom/select_lecture.php");
    }
    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params){
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();
                }catch (Exception e){
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result){
                myJSON = result;
                showList();
                SQLiteDatabase db = openOrCreateDatabase(
                        "lecture_list.db",
                        SQLiteDatabase.CREATE_IF_NECESSARY,
                        null);


                Cursor cursor=db.rawQuery("SELECT * FROM lecture",null);
                int i=0;
                while(cursor.moveToNext()){
                    String code=cursor.getString(0);
                    if(!code.equals(mysql_List.get(i).getCode()))
                    {
                        System.out.println(i+"번째 "+code+" "+cursor.getString(1));
                    }
                    i++;
                }
                if(db!=null)
                    db.close();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    protected void showList(){
        try{
            JSONObject jsonObj = new JSONObject(myJSON);
            lectures = jsonObj.getJSONArray(TAG_RESULTS);


            for(int i =0;i< lectures.length();i++){
                JSONObject c = lectures.getJSONObject(i);
                String code = c.getString(TAG_CODE);
                String title = c.getString(TAG_TITLE);
                String classroom = c.getString(TAG_CLASSROOM);
                String time = c.getString(TAG_TIME);
                mysql_List.add(new Lecture(code,title,classroom,time));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


        }


        @Override
        protected String doInBackground(String... params) {

            StringBuilder sb=new StringBuilder();;


            String serverURL = (String)params[0];

            SQLiteDatabase db = openOrCreateDatabase(
                    "lecture_list.db",
                    SQLiteDatabase.CREATE_IF_NECESSARY,
                    null);


            Cursor cursor=db.rawQuery("SELECT * FROM lecture",null);
           // cursor.moveToPosition(2019);
            int i=0;
            int j=0;
                try {

                    while(cursor.moveToNext()) {
                        if(i%8000==0)
                        {
                            Thread.sleep(5000);
                        }
                        i++;
                        if(cursor.getString(0).equals("") || cursor.getString(1).equals("") || cursor.getString(2).equals("") || cursor.getString(3).equals(""))
                                j++;
                        System.out.println(i+"번째 sql 전송 "+cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(3));
                        String postParameters = "code=" + cursor.getString(0) + "&title=" + cursor.getString(1) + "&classroom=" + cursor.getString(2) + "&time=" + cursor.getString(3);
                        URL url = new URL(serverURL);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                        httpURLConnection.setReadTimeout(30000);
                        httpURLConnection.setConnectTimeout(30000);
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.connect();

                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(postParameters.getBytes("UTF-8"));
                        outputStream.flush();
                        outputStream.close();


                        int responseStatusCode = httpURLConnection.getResponseCode();

                        InputStream inputStream;
                        if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                            inputStream = httpURLConnection.getInputStream();
                        } else {
                            inputStream = httpURLConnection.getErrorStream();
                        }

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        sb = new StringBuilder();
                        String line = null;

                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }

                        Thread.sleep(100);
                        bufferedReader.close();

                    }
                    System.out.println(j);
                    if(db!=null){
                        db.close();
                    }
                    return sb.toString();


                } catch (Exception e) {
                    System.out.println("-------------------------------------------------"+e.getMessage());
                    return new String("Error: " + e.getMessage());
                }

        }
    }
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                SQLiteDatabase db = openOrCreateDatabase(
                        "lecture_list.db",
                        SQLiteDatabase.CREATE_IF_NECESSARY,
                        null);

                //Cursor c= db.rawQuery("SELECT * FROM lecture",null);

                for(int i=0;i<url_List.size();i++) {//url 리스트 만큼 반복
                    Document doc = Jsoup.connect(url_List.get(i)).get();
                    System.out.println( (i+1) +"번째 페이지");

                    //테스트1
                    Elements titles = doc.select("td.th4");//과목코드
                    Elements titles2 = doc.select("td.th5");//과목이름
                    Elements titles3 = doc.select("td.th11");//강의실
                    Elements titles4 = doc.select("td.th17");//강의시간


                    System.out.println("-------------------------------------------------------------");
                    for (int j = 0; j < titles.size(); j++) {
                        //System.out.println("title: " + e.text());
                        //htmlContentInStringFormat += e.text().trim() + "\n";
                        db.execSQL("REPLACE INTO lecture (code,title,classroom,time) VALUES('" + titles.get(j).text().trim() + "','" + titles2.get(j).text().trim()
                                + "','" + titles3.get(j).text().trim() + "','" + titles4.get(j).text().trim() + "')");
                    }

                    System.out.println("-------------------------------------------------------------");

                    Cursor c = db.rawQuery("SELECT * FROM lecture", null);
                    while (c.moveToNext()) {
                        htmlContentInStringFormat += c.getString(0) + " " + c.getString(1) + " " +
                                c.getString(2) + " " + c.getString(3) + "\n";
                    }
                }
                if(db!=null){
                    db.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentInStringFormat);
        }
    }
}
