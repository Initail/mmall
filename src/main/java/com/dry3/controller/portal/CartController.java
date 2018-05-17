package com.dry3.controller.portal;import com.dry3.common.Const;import com.dry3.common.ResponseCode;import com.dry3.common.ServerResponse;import com.dry3.pojo.User;import com.dry3.service.ICartService;import com.dry3.vo.CartVo;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.ResponseBody;import javax.servlet.http.HttpSession;/** * Created by dry3 */@Controller@RequestMapping("/cart/")public class CartController {    @Autowired    private ICartService iCartService;    @RequestMapping(value = "add.do")    @ResponseBody    public ServerResponse<CartVo> addCart(HttpSession session, Integer productId, Integer count) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.add(user.getId(), productId, count);    }    @RequestMapping(value = "update.do")    @ResponseBody    public ServerResponse<CartVo> updateCart(HttpSession session, Integer productId, Integer count) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.update(user.getId(), productId, count);    }    @RequestMapping(value = "delete_products.do")    @ResponseBody    public ServerResponse<CartVo> deleteProductsInCart(HttpSession session, String productIds) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.deleteProductsInCart(user.getId(), productIds);    }    @RequestMapping(value = "list.do")    @ResponseBody    public ServerResponse<CartVo> deleteProductsInCart(HttpSession session) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.list(user.getId());    }    @RequestMapping(value = "select.do")    @ResponseBody    public ServerResponse  selectProduct(HttpSession session, Integer productId) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.selectProduct(Const.Cart.CHECKED, user.getId(), productId);    }    @RequestMapping(value = "un_select.do")    @ResponseBody    public ServerResponse<CartVo> unSelectProduct(HttpSession session, Integer productId) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.unSelectProduct(Const.Cart.UN_CHECKED, user.getId(), productId);    }    @RequestMapping(value = "select_all.do")    @ResponseBody    public ServerResponse  selectAllProduct(HttpSession session) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.selectAllProduct(Const.Cart.CHECKED, user.getId());    }    @RequestMapping(value = "un_select_all.do")    @ResponseBody    public ServerResponse<CartVo> unSelectAllProduct(HttpSession session) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.unSelectAllProduct(Const.Cart.UN_CHECKED, user.getId());    }    @RequestMapping(value = "get_cart_product_count.do")    @ResponseBody    public ServerResponse<Integer> getCartProductCount(HttpSession session) {        User user = (User) session.getAttribute(Const.CURRENT_USER);        if (user == null) {            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());        }        return iCartService.getCartProductCount(user.getId());    }}