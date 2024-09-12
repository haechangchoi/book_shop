package com.fastcampus.ch4.service.cart;

import com.fastcampus.ch4.dto.cart.CartProductDetailDto;

import java.util.List;

public interface CartService {
    Integer add(Integer cart_seq, String isbn, String prod_type_code, String userId) throws Exception;
    Integer createOrGetCart(Integer cartSeq, String userId);
    int addCartProduct(Integer cartSeq, String isbn, String prod_type_code, String userId) throws Exception;
    List<CartProductDetailDto> getItemList(Integer cartSeq, String userId);
    int updateItemQuantity(Integer cartSeq, String isbn, String prod_type_code, Integer quantity, String userId);
    public int deleteCartProduct(Integer cartSeq, String isbn, String prod_type_code);
}
