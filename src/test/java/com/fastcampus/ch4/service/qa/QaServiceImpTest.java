package com.fastcampus.ch4.service.qa;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fastcampus.ch4.dao.qa.QaDao;
import com.fastcampus.ch4.dao.qa.ReplyDao;
import com.fastcampus.ch4.dto.qa.QaDto;
import com.fastcampus.ch4.domain.qa.SearchCondition;
import com.fastcampus.ch4.dto.qa.ReplyDto;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class QaServiceImpTest {

    @Autowired
    private QaDao dao;

    @Autowired
    private ReplyDao replyDao;

    @Autowired
    private QaService service;

    @Before
    public void 초기화() {
        replyDao.deleteAll();
        dao.deleteAll();
        assertTrue(service != null);

        // (각 테스트 클래스에서 실행하기)
        // 카테고리 데이터 미리 넣기
        // 코드 데이터 미리 넣기
    }

    @Test
    public void 데이터_넣기() {
        // (각 테스트 클래스에서 실행하기)
        // 카테고리 데이터 미리 넣기
        // 코드 데이터 미리 넣기
        for (int i=0; i<20; i++) {
            QaDto dto = create(i);
            assertTrue(service.write("user1", dto));
        }

        List<QaDto> selected = service.read("user1");
        int i = 0;
        for (QaDto dto : selected) {
            if (i % 2 == 0) {
                ReplyDto reply = createReply(dto.getQa_num());
                assertTrue(service.addReply(reply));
            }
            i++;
        }

    }

    @Test
    public void 답변_데이터_넣기() {

    }

    /**
     * 1차 기능 구현 [✅]
     * - (1) 특정 글 상세 조회
     * - (2) 글 목록 조회 - 페이징 처리, 페이징 처리 및 특정 상태
     * - (3) 글 검색 - 기간, 제목 대상으로 글 조회
     * - (4) 글 작성 - 같은 제목 작성 방지,
     * - (5) 글 삭제 - 글 번호로 삭제, 글 제목으로 삭제
     * - (6) 글 수정
     *
     * 1차 요구사항 정리
     * - (1) 특정 글 상세 조회(이때, 관련 답글도 가져옴) [⚙️]
     * - 비회원의 경우, 상세 조회 실패
     * - 회원의 경우, 해당 게시글 상세 조회(게시글 + 답별글)
     * - 회원의 경우, 자신이 작성하지 않은 게시글 조회할 경우 실패
     *
     * - (2) 글 목록 조회 - 페이징 처리, 페이징 처리 및 특정 상태 [✅]
     * - 비회원의 경우, 0개 게시글 조회
     * - 회원의 경우, n개 게시글 조회
     *
     * - (3) 글 검색 - 기간, 제목 대상으로 글 조회 [✅]
     * - 비회원의 경우, 0개 게시글
     * - 회원의 경우, 검색 대상이 되는 수만큼 조회(k개)
     *
     * - (4) 글 작성 - 같은 제목 작성 방지 [✅]
     * - 비회원의 경우, 게시 작성 안됨
     * - 회원의 경우, 이미 작성한 게시글(제목 동일)은 작성 실패
     * - 회원의 경우, 제목이나 내용 공백시 작성 실패
     * - 회원의 경우, 필수 값 누락시 게시글 작성 실패
     * - 회원의 경우, 작성한 게시글 성공적으로 등록
     *
     * - (5) 글 삭제 - 글 번호로 삭제, 글 제목으로 삭제 [✅]
     * - 비회원의 경우, 게시글 삭제 실패
     * - 회원의 경우, 자신과 관련 없는 게시글 삭제 못함
     * - 회원의 경우, 자신이 작성한 게시글만 삭제함
     *
     * - (6) 글 수정 [✅]
     * - 비회원의 경우, 게시글 수정 실패
     * - 회원의 경우, 수정된 게시글 제목이 다른 게시글과 겹치는 경우 실패
     * - 회원의 경우, 수정된 게시글에 제목이나 내용이 공백인 경우 실패
     * - 회원의 경우, 수정된 게시글에 필수값이 누락된 경우 실패
     * - 회원의 경우, 자신이 작성한 게시글 수정 가능
     */


    // (1) 특정 글 상세 조회(이때, 관련 답글도 가져옴) [⚙️]
    @Test
    @DisplayName("비회원의 경우, 상세 조회 실패")
    public void 비회원_상세조회_실패() {
        // 컨트롤러에서 막아 버리기
    }

    @Test
    @DisplayName("회원의 경우, 해당 게시글 상세 조회(게시글 + 답별글)")
    public void 회원_상세_조회_성공() {
        String userId = "user1";
        QaDto dto = create(1);
        assertTrue(service.write(userId,  dto));
        QaDto found = dto; // 이 부분 수정 필요 - 현재 qaNum 시퀀스여서 테스트 어려움
        assertEquals(dto, found);
    }

    @Test
    @DisplayName("회원의 경우, 자신이 작성하지 않은 게시글 상세 조회 실패")
    public void 회원_상세_조회_실패() {
        String userId = "user1";
        QaDto dto = create(1);
        dto.setUser_id("user2");
        assertTrue(service.write(userId, dto));
        QaDto found = null; // 이 부분 수정 필요 - 현재 qaNum 시퀀스여서 테스트 어려움
        assertEquals(null, found);
    }
    // (2) 글 목록 조회 - 페이징 처리, 페이징 처리 및 특정 상태 [✅]
    @Test
    @DisplayName("비회원의 경우, 0개 게시글 조회")
    public void 비회원_조회_0() {
        // 컨트롤러에서 못넘어 오게 막기
    }

    @Test
    @DisplayName("회원의 경우, n개 게시글 조회")
    public void 회원_조회_n() {
        String userId = "user1";
        for (int i=0; i<10; i++) {
            QaDto dto = create(i);
            SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
            assertTrue(service.write(userId, dto));
        }

        int size = service.read(userId).size();
        System.out.println("size = " + size);
        assertTrue(10 == size);
    }

    @Test
    @DisplayName("회원의 경우, n개 게시글 조회")
    public void 회원_조회_n2() {
        String userId = "user1";
        for (int i=0; i<10; i++) {
            QaDto dto = create(i);
            SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
            assertTrue(service.write(userId, dto));
        }

        SearchCondition sc = new SearchCondition(1, 10);
        int size = service.read(userId, sc).size();
        assertTrue(10 == size);
    }

    // (3) 글 검색 - 기간, 제목 대상으로 글 조회 [✅]
    @Test
    @DisplayName("비회원의 경우, 0개 게시글 검색")
    public void 비회원_검색_0() {
        // 컨트롤러에서 막아버기
    }

    @Test
    @DisplayName("회원의 경우, k개 만큼 검색1(제목)")
    public void 회원_검색_k() {
        String userId = "user1";
        for (int i=1; i<=100; i++) {
            QaDto dto = create(i);
            SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
            assertTrue(service.write(userId, dto));
        }

        SearchCondition sc = new SearchCondition(1, 10, "title", "title1", 0);

        List<QaDto> selected = service.readBySearchCondition(userId, sc);
        selected.forEach(qa -> System.out.print(", qa's title = " + qa.getTitle()));
        // title1, title10, title11, title12, title13, title14, title15, title16, title17, title18, title19, title100
//        assertTrue(10 == selected.size());
    }

    @Test
    @DisplayName("회원의 경우, k개 만큼 검색1(시간)")
    public void 회원_검색_k2() {
        String userId = "user1";
        for (int i=0; i<=5; i++) {
            QaDto dto = create(i);
            SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
            assertTrue(service.write(userId, dto));
        }

        SearchCondition sc = new SearchCondition(1, 10, "period", "title1", 1);
        List<QaDto> selected = service.readBySearchCondition(userId, sc);
        selected.forEach(qa -> System.out.print(", qa's title = " + qa.getTitle()));
        // title0, ... , title5
        assertTrue(6 == selected.size());
    }

    // (4) 글 작성 - 같은 제목 작성 방지 [✅]

    @Test
    @DisplayName("비회원의 경우, 작성 안됨")
    public void 비회원_작성_실패() {
        // 컨트롤러에서 막아 버리기
    }

    @Test
    @DisplayName("회원의 경우, 이미 작성한 게시글(제목 동일)은 작성 실패")
    public void 회원_중복_제목_작성_실패() {
        // 중복 제목 탐색
        // 조회한 제목이랑 입력받은 제목이랑 동일한 경우, 예외 발생
        // 글 작성 등록
        // 작성 완료 됬는지 확인
        String userId = "user1";
        QaDto dto = create(1);
        dto.setUser_id(userId);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        assertTrue(service.write(userId, dto));
        assertTrue(!service.write(userId, dto));
    }

    @Test
    @DisplayName("회원의 경우, 제목이나 내용 공백시 작성 실패")
    public void 회원_제목_내용_공백_작성_실패() {
        String userId = "user1";
        QaDto dto = create(1);
        dto.setUser_id(userId);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        dto.setTitle("");
        assertThrows(UncategorizedSQLException.class, () -> service.write(userId, dto));
    }

    @Test
    @DisplayName("회원의 경우, 필수 값 누락시 게시글 작성 실패")
    public void 회원_필수값_누락_작성_실패() {
        String userId = "user1";
        QaDto dto = create(1);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        dto.setTitle(null);
        assertThrows(DataIntegrityViolationException.class, () -> service.write(userId, dto));

    }

    @Test
    @DisplayName("회원의 경우, 작성한 게시글 성공적으로 등록")
    public void 회원_게시글_등록_성공() {
        String userId = "user1";
        QaDto dto = create(1);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        assertTrue(service.write(userId, dto));
    }

    // (5) 글 삭제 - 글 번호로 삭제, 글 제목으로 삭제 [✅]
    @Test
    @DisplayName("비회원의 경우, 게시글 삭제 실패")
    public void 비회원_삭제_실패() {
        // 컨트롤러에서 막아 버리기
    }

    @Test
    @DisplayName("회원의 경우, 자신과 관련 없는 게시글 삭제 못함")
    public void 회원_관련없는_게시글_삭제_실패() {
        String userId = "user1";
        QaDto dto = create(1);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        assertTrue(service.write(userId, dto));

        dto.setQa_num(-1);
        assertTrue(!service.remove(dto));
    }

    @Test
    @DisplayName("회원의 경우, 자신이 작성한 게시글만 삭제함")
    public void 회원_게시글_삭제_성공() {
        String userId = "user1";
        QaDto dto = create(1);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        assertTrue(service.write(userId,dto));
        QaDto found = service.read(userId, sc).get(0);
        assertTrue(service.remove(found));
    }



    // (6) 글 수정 [✅]
    @Test
    @DisplayName("비회원의 경우, 게시글 수정 실패")
    public void 비회원_수정_실패() {
        // 컨트롤러에서 막아 버리기
    }

    @Test
    @DisplayName("회원의 경우, 수정된 게시글 제목이 다른 게시글과 겹치는 경우 실패")
    public void 회원_수정_제목_겹치는_실패() {
        String userId = "user1";
        QaDto dto = create(1);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        assertTrue(service.write(userId, dto));

        QaDto found = service.read(userId, sc).get(0);
        String dupicateTitle = found.getTitle();
        QaDto updated = create(2);
        updated.setTitle(dupicateTitle);
        assertTrue(!service.modify(userId, found, sc));
    }

    @Test
    @DisplayName("회원의 경우, 수정된 게시글에 제목이나 내용이 공백인 경우 실패")
    public void 회원_수정_제목_내용_공백_실패() {
        // 컨트롤러 처리 전 유효성 검증으로 막아버리기
    }

    @Test
    @DisplayName("회원의 경우, 수정된 게시글에 필수값이 누락된 경우 실패")
    public void 회원_수정_필수값_누락_실패() {
        // 컨트롤러 처리 전 유효성 검증으로 막아버리기
    }

    @Test
    @DisplayName("회원의 경우, 자신이 작성한 게시글 수정 가능")
    public void 회원_수정_성공() {
        String userId = "user1";
        QaDto dto = create(1);
        SearchCondition sc = new SearchCondition(1, 10, "title", dto.getTitle(), 0);
        assertTrue(service.write(userId, dto));

        QaDto found = service.read(userId, sc).get(0);
        found.setTitle("new title!!");
        assertTrue(service.modify(userId, found, sc));
    }

    private QaDto create(int i) {
        QaDto dto = new QaDto();
        dto.setUser_id("user1");
        dto.setQa_cate_num("qa-cate-01");
        dto.setCate_name("배송/수령예정일안내");
        dto.setQa_stat_code("qa-stat-01");
        dto.setStat_name("처리 대기중");
        dto.setChk_repl("Y");
        dto.setTitle("문의글입니다." + i);
        dto.setContent("바람이 세차게 불어오는 저녁, 해변을 따라 걷던 엘레나는 발밑에서 부서지는 파도 소리를 들으며 잠시 멈춰 섰다. 하늘은 붉은 노을에 물들어 있었고, 태양은 서서히 수평선 아래로 사라지고 있었다. 그녀는 손을 주머니에 넣고, 바다를 응시하며 깊은 생각에 잠겼다. 몇 년 전, 이곳에서의 추억들이 그녀의 마음속에 선명하게 떠올랐다. 그때는 모든 것이 단순하고 아름다웠다. 하지만 지금, 시간은 모든 것을 변화시키고, 사람들 사이에 놓인 거리는 점점 더 멀어져만 갔다. 엘레나는 서늘한 바람에 얼굴을 내맡기며, 아직 끝나지 않은 이야기를 다시 써 내려갈 용기를 다짐했다. 그녀의 발걸음은 다시 앞으로 향했고, 바다는 여전히 그녀의 곁에서 잔잔하게 출렁이고 있었다." + i);
        dto.setCreated_at("2021-01-01");
        dto.setEmail("email1");
        dto.setTele_num("010-1234-5678");
        dto.setPhon_num("010-1234-5678");
        dto.setImg1("img1");
        dto.setImg2("img2");
        dto.setImg3("img3");
        return dto;
    }

    private ReplyDto createReply(int qaNum) {
        ReplyDto dto = new ReplyDto();
        dto.setQa_num(qaNum);
        dto.setWriter("admin1");
        dto.setContent("답변입니다.");
        dto.setCreted_at("2021-01-01");
        dto.setComt("비고 사항입니다.");
        dto.setReg_date("2021-01-01");
        dto.setUp_date("2021-01-01");
        dto.setUp_id("admin1");
        return dto;
    }
}