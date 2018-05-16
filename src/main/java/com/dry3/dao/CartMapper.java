package com.dry3.dao;import com.dry3.pojo.Cart;import org.apache.ibatis.annotations.Param;import java.util.List;public interface CartMapper {    int deleteByPrimaryKey(Integer id);    int insert(Cart record);    int insertSelective(Cart record);    Cart selectByPrimaryKey(Integer id);    int updateByPrimaryKeySelective(Cart record);    int updateByPrimaryKey(Cart record);    Cart selectByUserIdProductId(@Param("userId") Integer userId, @Param("userId") Integer productId);    List<Cart> selectByUserId(Integer userId);    int selectAllCheckedByUserId(Integer userId);    int deleteProductsInCart(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);    int updateCartCheckedByUserIdProductId(@Param("checked") Integer checked, @Param("userId") Integer userId, @Param("productId") Integer productId);    int selectTotalCountOfProductInCart(Integer userId);    List<Cart> selectCheckedCartByUserId(Integer userId);}