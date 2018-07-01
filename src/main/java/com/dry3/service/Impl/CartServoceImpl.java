package com.dry3.service.Impl;

import com.dry3.common.Const;
import com.dry3.common.ResponseCode;
import com.dry3.common.ServerResponse;
import com.dry3.dao.CartMapper;
import com.dry3.dao.ProductMapper;
import com.dry3.pojo.Cart;
import com.dry3.pojo.Product;
import com.dry3.service.ICartService;
import com.dry3.util.BigDecimalUtil;
import com.dry3.util.PropertiesUtil;
import com.dry3.vo.CartProductVo;
import com.dry3.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by dry3
 */
@Service("iCartService")
public class CartServoceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart == null) {
            Cart cartItem = new Cart();
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setQuantity(count);
            cartMapper.insert(cartItem);
        } else {
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);

        }
        return list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Cart cart = cartMapper.selectByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return list(userId);
    }

    public ServerResponse<CartVo> deleteProductsInCart(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        //若productIdList为null 则删除购物车所有商品
        cartMapper.deleteProductsInCart(userId, productIdList);
        return list(userId);
    }


    public ServerResponse<CartVo> list(Integer userId) {
        return ServerResponse.createBySuccess(this.getCartVoLimit(userId));
    }

    public ServerResponse<CartVo> selectProduct(Integer checked, Integer userId, Integer productId) {
        if (productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        cartMapper.updateCartCheckedByUserIdProductId(checked, userId, productId);
        return list(userId);
    }


    public ServerResponse<CartVo> unSelectProduct(Integer checked, Integer userId, Integer productId) {
        if (productId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        cartMapper.updateCartCheckedByUserIdProductId(checked, userId, productId);
        return list(userId);
    }

    public ServerResponse<CartVo> selectAllProduct(Integer checked, Integer userId) {
        cartMapper.updateCartCheckedByUserIdProductId(checked, userId, null);
        return list(userId);
    }


    public ServerResponse<CartVo> unSelectAllProduct(Integer checked, Integer userId) {
        cartMapper.updateCartCheckedByUserIdProductId(checked, userId, null);
        return list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        return ServerResponse.createBySuccess(cartMapper.selectTotalCountOfProductInCart(userId));
    }


    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    CartProductVo cartProductVo = new CartProductVo();
                    cartProductVo.setId(cartItem.getId());
                    cartProductVo.setUserId(cartItem.getUserId());
                    cartProductVo.setProductId(cartItem.getProductId());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());
                    int buyLimitCount = 0;
                    if (cartItem.getQuantity() > product.getStock()) {
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        buyLimitCount = product.getStock();
                        //更新购物车中库存数量
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartForQuantity.setId(cartItem.getId());
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    } else {
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                        buyLimitCount = cartItem.getQuantity();
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                    if (cartItem.getChecked() == Const.Cart.CHECKED)
                        //判断如果勾选则添加总价
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                    cartProductVoList.add(cartProductVo);
                }
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        cartVo.setAllChecked(this.getAllChecked(userId));
        return cartVo;
    }
    private boolean getAllChecked(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectAllCheckedByUserId(userId) == 0;
    }
}
