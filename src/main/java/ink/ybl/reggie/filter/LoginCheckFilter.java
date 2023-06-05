package ink.ybl.reggie.filter;

import com.alibaba.fastjson.JSON;
import ink.ybl.reggie.common.BaseContext;
import ink.ybl.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的url
        String requestURI = request.getRequestURI();

        // 定义不需要处理的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
        };
        // 2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3、如果不需要，则直接通过
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {

            // 获取用户id
            Long empId = (Long) request.getSession().getAttribute("employee");
            // 存储用户id
            BaseContext.setCurrentId(empId);

            // 放行 （一定要先存储用户id再放行）
            filterChain.doFilter(request, response);
            return;
        }
        // 判断手机端登录状态
        if (request.getSession().getAttribute("user") != null) {

            // 获取用户id
            Long userId = (Long) request.getSession().getAttribute("user");
            // 存储用户id
            BaseContext.setCurrentId(userId);

            // 放行 （一定要先存储用户id再放行）
            filterChain.doFilter(request, response);
            return;
        }

        // 5、如果未登录则返回未登录结果,通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
