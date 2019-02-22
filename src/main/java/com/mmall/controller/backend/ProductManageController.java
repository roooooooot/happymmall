package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Description 产品管理后台控制器
 * @Author 肖广贞
 * @Date 2018/11/30
 * @Version 1.0
 **/
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse addProduct(HttpSession session, Product product) {
        // 判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.addOrUpdate(product);
        }
        return ServerResponse.createByErrorMessage("此用户不是管理员,请使用管理员用户登录");
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        // 判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        }
        return ServerResponse.createByErrorMessage("此用户不是管理员,请使用管理员用户登录");
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        // 判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("此用户不是管理员,请使用管理员用户登录");
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) {
        // 判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.listProducts(pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("此用户不是管理员,请使用管理员用户登录");
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) {
        // 判断用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("此用户不是管理员,请使用管理员用户登录");
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file", required = false) MultipartFile file, HttpSession session, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getServletContext().getRealPath("upload");

            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);

            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("此用户不是管理员,请使用管理员用户登录");
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map uploadRichTextImg(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        Map result = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            result.put("success", false);
            result.put("msg", "用户未登录");
            return result;
        }
        // 富文本中对于返回值有自己的要求,我们使用的是simditor，所以按照simditor的要求返回
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                result.put("success", false);
                result.put("msg", "文件上传失败");
                return result;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            result.put("success", true);
            result.put("msg", "文件上传成功");
            result.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return result;
        }
        result.put("success", false);
        result.put("msg", "不是管理员");
        return result;
    }
}
