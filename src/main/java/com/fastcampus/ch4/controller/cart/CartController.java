package com.fastcampus.ch4.controller.cart;

import com.fastcampus.ch4.domain.cart.ItemInfoDto;
import com.fastcampus.ch4.dto.cart.CartProductDetailDto;
import com.fastcampus.ch4.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
=== 기능 목록
1. 장바구니 목록
- 주문하기 버튼 => 주문 완료 페이지로 이동하기
- 나중에 주문 버튼 => 루트 페이지로 이동하기
2. 장바구니 상품 추가
3. 장바구니 상품 삭제
4. 장바구니 상품 수량 변경

=== 기능 요구 사항

1. 장바구니 목록 (GET /cart)
- 회원 / 비회원 에 해당하는 값 추출하기 => 반환값 : cart_seq 이나 userId
-- 회원 고객 장바구니 찾기
--- session 에서 userId 를 찾는다.
---- session 에 userId 가 있으면? => userId 저장
---- session 에 userId 가 없으면? => 비회원 고객 장바구니 찾기 단계를 진행한다.

-- 비회원 고객 장바구니 찾기
--- cookie 에 담긴 cart_seq 를 찾는다.
---- cookie 에 cart_seq 가 있으면? => cart_seq 저장
---- cookie 에 cart_seq 가 없으면? => 장바구니 추가하기 단계로 진행

- 장바구니 조회
-- getItemList(cart_seq, userId)

- 결과 반환

2. 장바구니 상품 추가 (POST : /cart/proudct)
Response : 실행결과 (페이지 X)

- 회원 / 비회원 에 해당하는 값 추출하기 => 반환값 : cart_seq 이나 userId
-- 회원 고객 장바구니 찾기
--- session 에서 userId 를 찾는다.
---- session 에 userId 가 있으면? => userId 저장
---- session 에 userId 가 없으면? => 비회원 고객 장바구니 찾기 단계를 진행한다.
-- 비회원 고객 장바구니 찾기
--- cookie 에 담긴 cart_seq 를 찾는다.
---- cookie 에 cart_seq 가 있으면? => cart_seq 저장
---- cookie 에 cart_seq 가 없으면? => 장바구니 추가하기 단계로 진행

- 장바구니 추가하기 => 선행 결과 : cart_seq 나 userId / 반환값 : cart_seq
-- 회원 고객 ( 조건 : userId 가 있어야 한다. )
--- 서비스 createOrGetCart(null, userId) 호출 => retrun : cart_seq
-- 비회원 고객 ( 조건 : (cart_seq == null & userId=null) || (cart_seq != null && userId == null))
--- 서비스 createOrGetCart(cart_seq, null) 호출 => retrun : cart_seq
--- 서비스 createOrGetCart(null, null) 호출 => retrun : cart_seq

- 장바구니 상품 추가하기 => 반환값 : cart_seq / 필요한 값 : cart_seq, isbn, prod_type_code
-- 서비스 addCartProduct(cart_seq, isbn, prod_type_code) 를 호출한다.
-- 반환값이 0 이면 에러메시지를 반환한다.
-- 반환값이 1 이면 성공메시지를 반환한다. (HTTP STATUS 가능)
--- (추가) 성공 시 장바구니 페이지로 이동할지 여부를 물어본다.

3. 장바구니 상품 삭제 (DELETE : /cart/product)
- 삭제 api 로 요청을 받는다.
-- 필요한 값 : cart_seq, isbn, prod_type_code

- 장바구니 상품을 삭제한다.
-- 삭제 성공 : redirect:/cart/list
-- 삭제 실패 : 실패 메시지

4. 장바구니 수량 변경 (PATCH : /cart/product/quantity?isplus="true")
- 변경 api 로 요청을 받는다.
-- 필요한 값 : cart_seq, isbn, prod_type_code

- 서비스 메서드 요청 = 장바구니 상품 수량 변경
- 성공 메시지 반환
 */

@Controller
@RequestMapping(value = "/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @ExceptionHandler(Exception.class)
    public String handleException(Model model) {
        String message = "예기치 못한 동작이 발생했습니다. 다시 시도해주세요.";
        model.addAttribute("message", message);
        return "exception/exceptionPage";
    }


    @GetMapping("/list")
    public String cartList(HttpServletRequest request, Model model) {
        // 회원 / 비회원 에 해당하는 값 추출하기
        Integer cartSeq = null;
        String userId;
        // 회원 = session 에서 관리
        HttpSession session = request.getSession();
        userId = (String) session.getAttribute("id");

        // 비회원 Cookie
        if (userId == null) {
            Cookie[] cookies = request.getCookies();

            Optional<Integer> optCartSeq = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("cartSeq"))
                    .map(cookie -> Integer.valueOf(cookie.getValue()))
                    .findFirst();

            if (optCartSeq.isPresent()) {
                cartSeq = optCartSeq.get();
            }
        }

        // 장바구니 조회
        List<CartProductDetailDto> itemList = cartService.getItemList(cartSeq, userId);

        // 결과 반환
        model.addAttribute("itemList", itemList);

        return "cart/cartList";
    }

    @PostMapping("/add")
    public String addCart(
            String isbn, String product_type,
            HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        /*
        필요한 변수 : cartSeq, isbn, prodTypeCode

        - 회원 검사
        - cart 생성 및 조회
        - cart Proudct 추가
        - 장바구니 품목을 추가할 때 cookie 로 생성해서 추가하도록 한다.
         */

        // 회원 / 비회원 에 해당하는 값 추출하기
        Integer cartSeq = null;
        String userId;
        // 회원 = session 에서 관리
        HttpSession session = request.getSession();
        userId = (String) session.getAttribute("id");

        // 비회원 Cookie
        if (userId == null) {
            Cookie[] cookies = request.getCookies();

            Optional<Integer> optCartSeq = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("cartSeq"))
                    .map(cookie -> Integer.valueOf(cookie.getValue()))
                    .findFirst();

            if (optCartSeq.isPresent()) {
                cartSeq = optCartSeq.get();
            }
        }

        cartSeq = cartService.createOrGetCart(cartSeq, userId);

        // cookie 셋팅
        if (userId == null) {
            Cookie cookie = new Cookie("cartSeq", String.valueOf(cartSeq));
            cookie.setDomain("localhost");
            cookie.setPath("/");
            cookie.setMaxAge(60*60*24);
            response.addCookie(cookie);
        }

        int result = cartService.addCartProduct(cartSeq, isbn, product_type, userId);
        return "redirect:/cart/list";
    }

    @PostMapping("/product/quantity")
    public String updateQuantity(ItemInfoDto infoDto, HttpServletRequest request, Model model) {
        /*
        필요한 변수 : cartSeq, isbn, prodTypeCode, itemQuantity, userId

        필요한 서비스 : updateItemQuantity

        반환 : update 성공여부, 업데이트된 수치
         */

        // userId 추출하기
        String userId;
        // 회원 = session 에서 관리
        HttpSession session = request.getSession();
        userId = (String) session.getAttribute("id");

        Integer updateItemQuantity = infoDto.getItemQuantity();
        Integer cartSeq = infoDto.getCartSeq();
        String isbn = infoDto.getIsbn();
        String prodTypeCode = infoDto.getProdTypeCode();

        // updateItemQuantity
        int resultItemQuantity = cartService.updateItemQuantity(cartSeq, isbn, prodTypeCode, updateItemQuantity, userId);

        return "redirect:/cart/list";
    }

    @PostMapping("/product/delete")
    public String deleteCartProduct(ItemInfoDto infoDto, HttpServletRequest request, Model model) {
        // userId 추출하기
        String userId;
        // 회원 = session 에서 관리
        HttpSession session = request.getSession();
        userId = (String) session.getAttribute("id");

        Integer cartSeq = infoDto.getCartSeq();
        String isbn = infoDto.getIsbn();
        String prodTypeCode = infoDto.getProdTypeCode();

        // delete
        int deleteResult = cartService.deleteCartProduct(cartSeq, isbn, prodTypeCode);

        return "redirect:/cart/list";
    }
}

