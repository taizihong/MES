package com.haoyu.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.haoyu.model.SysUser;
import com.haoyu.service.SysUserService;
import com.haoyu.util.MD5Util;

@Controller
public class UserController {

	@Resource
	private SysUserService sysUserService;

	@RequestMapping("/logout.page")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.getSession().invalidate();
		String path = "signin.jsp";
		response.sendRedirect(path);
	}

	@RequestMapping("/login.page")
	public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		SysUser sysUser = sysUserService.findByKeyword(username);
		String errorMsg = "";
		String ret = request.getParameter("ret");

		if (StringUtils.isBlank(username)) {
			errorMsg = "用户名不可以为空";
		} else if (StringUtils.isBlank(password)) {
			errorMsg = "密码不可以为空";
		} else if (sysUser == null) {
			errorMsg = "查询不到指定的用户";
		} else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))) {
			errorMsg = "用户名或密码错误";
		} else if (sysUser.getStatus() != 1) {
			errorMsg = "用户已被冻结，请联系管理员";
		} else {
			// login success
			request.getSession().setAttribute("user", sysUser);
			if (StringUtils.isNotBlank(ret)) {
				response.sendRedirect(ret);
			} else {
				response.sendRedirect("/sys/admin/index.page");
				// TODO
				return;
			}
		}

		request.setAttribute("error", errorMsg);
		request.setAttribute("username", username);
		if (StringUtils.isNotBlank(ret)) {
			request.setAttribute("ret", ret);
		}
		String path = "signin.jsp";
		request.getRequestDispatcher(path).forward(request, response);
	}
}
