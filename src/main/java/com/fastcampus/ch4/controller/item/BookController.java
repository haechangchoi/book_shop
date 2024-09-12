package com.fastcampus.ch4.controller.item;

import com.fastcampus.ch4.dto.item.BookDto;
import com.fastcampus.ch4.dto.item.PageHandler;
import com.fastcampus.ch4.dto.item.BookSearchCondition;
import com.fastcampus.ch4.service.item.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    BookService bookService;

    // 도서 저장
    @PostMapping("/write")
    public String write(BookDto bookDto, Model m, HttpSession session, RedirectAttributes rattr) {

        // TODO: 관리자 아이디(상품 등록자) bookDto에 저장
//        String writer = (String)session.getAttribute("id");
//        bookDto.setRegi_id(writer);
        // userService.getAdmin(writer);

        try {
            // isbn 숫자값인지 검증
//            isbnCheck(bookDto.getIsbn());
            // 정가나 할인율에 음수 불가. 할인율 100이상 불가.
//            if(!registerValueCheck(bookDto)) throw new IllegalArgumentException("Invalid value");
            //TODO 데이터 바인더 오류 - null이 들어갈 수 없는  타입을 빈 칸으로 둔 경우. 컨트롤러에 넘어오지 않음. 자스로 한번 검증시키고 컨트롤러에서 이중 검증.

            int rowCnt = bookService.write(bookDto); // insert
            System.out.println(rowCnt);
            if(rowCnt != 1)
                throw new Exception("Write Failed"); // 상품 등록 실패

            rattr.addFlashAttribute("msg", "WRT_OK");

            return "redirect:/book/list";
        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute(bookDto); // 작성하던 내용
            m.addAttribute("mode", "new");
            m.addAttribute("msg", "WRT_ERR");
            return "item/book"; // 실패하면 상품 등록 화면 보여주기
        }
    }

    // 도서등록 페이지로 연결
    @GetMapping("/write")
    public String write(Model m){
        // mode가 new이면 readonly 해제. 즉 글쓰기 모드.
        m.addAttribute("mode", "new");
        return "item/book";
    }

    // 도서 삭제 후 보고있던 도서리스트 페이지로 이동
    @PostMapping("/remove")
    public String remove(Integer page, Integer pageSize, String order_criteria, String order_direction, String isbn, Model m, HttpSession session, RedirectAttributes rattr) {
        // 세션에 저장된 작성자 id가져오기
//         String writer = (String)session.getAttribute("id");
        // TODO: 관리자 아이디(상품 등록자) 체크해서 삭제로 구현.

        try {
            // 메세지를 한번만 뜨게 할 때 쓰는게 RedirectAttribute(세션을 이용. 한번 이용했다가 지워버림)
            rattr.addAttribute("page", page);
            rattr.addAttribute("pageSize", pageSize);
            rattr.addAttribute("order_criteria", order_criteria);
            rattr.addAttribute("order_direction", order_direction);

            // 해당 도서 삭제
            int rowCnt = bookService.remove(isbn);

            //삭제가 안된 경우
            if (rowCnt != 1) throw new Exception("book remove error");

            rattr.addFlashAttribute("msg", "DEL_OK");
            return "redirect:/book/list";

        } catch (Exception e) {
            e.printStackTrace();
            rattr.addFlashAttribute("msg", "DEL_ERR");
        }

        return "redirect:/book/list";
    }

    // 도서 상세 페이지 매핑
    @GetMapping("/read")
    public String read(Integer page, Integer pageSize, String order_criteria, String order_direction, String isbn, Model m) {
        try {
            // bookDto 읽어와서 book.jsp에 bookDto, page, pageSize넘겨주기
            BookDto bookDto = bookService.read(isbn);
            // m.addAttribute("bookDto",bookDto);와 동일
            m.addAttribute(bookDto);
            m.addAttribute("page", page);
            m.addAttribute("pageSize", pageSize);
            m.addAttribute("order_criteria", order_criteria);
            m.addAttribute("order_direction", order_direction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "item/book";
    }

    @GetMapping("list")
    public String list(BookSearchCondition bsc, Model m, HttpServletRequest request) {
        // 로그인 유효성 검증

        try {
            // 총 게시물 개수
            int totalCnt = bookService.getSearchResultCnt(bsc);
            PageHandler pageHandler = new PageHandler(totalCnt, bsc);

            // bookService 호출
            List<BookDto> list = bookService.getSearchResultPage(bsc);

            // 도서 리스트 뷰에 list, pageHandler전달
            m.addAttribute("list", list);
            m.addAttribute("ph", pageHandler);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 도서 리스트 뷰
        return "item/bookList";
    }

    private void isbnCheck(String isbn) throws Exception {
        try {
            Integer.parseInt(isbn);
            if(isbn.length() != 13) throw new IllegalArgumentException();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid integer value: " + isbn);
        }
    }
    private boolean registerValueCheck(BookDto bookDto) {
        if(bookDto.getPapr_pric() >= 0 && bookDto.getE_pric() >= 0 &&
                bookDto.getPapr_disc() >= 0 && bookDto.getPapr_disc() <= 100 &&
                bookDto.getE_disc() >= 0 && bookDto.getE_disc() <= 100) return true;
        return false;
    }
}
