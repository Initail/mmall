package com.dry3.service;import com.dry3.common.ServerResponse;import com.dry3.vo.OrderVo;import com.github.pagehelper.PageInfo;import java.util.Map;/** * Created by dry3 */public interface IOrderService {    ServerResponse<Map> pay(Long orderNo, String path, Integer userId);    ServerResponse aliCallback(Map<String,String> params);    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);    ServerResponse createOrder(Integer userId,Integer shippingId);    ServerResponse<String> cancel(Integer userId,Long orderNo);    ServerResponse getOrderCartProduct(Integer userId);    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);    //backend    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);    ServerResponse<OrderVo> manageDetail(Long orderNo);    ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);    ServerResponse<String> manageSendGoods(Long orderNo);}