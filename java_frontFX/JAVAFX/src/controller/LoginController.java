package controller;

import service.LoginService;

public class LoginController {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    // 登入，回傳是否成功
    public boolean login(String role, String id, String pass) {
        return loginService.login(role, id, pass);
    }

    // 登出
    public void logout() {
        loginService.logout();
    }

    // 是否已登入
    public boolean isLoggedIn() {
        return loginService.isLoggedIn();
    }

    // 取得角色
    public String getRole() {
        return loginService.getRole();
    }
}
