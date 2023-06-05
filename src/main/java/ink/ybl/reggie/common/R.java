package ink.ybl.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> {
    private Integer code; // 编码：成功或者失败(200成功，其它失败)

    private String msg; // 接口信息

    private T data; // 返回的数据

    private Map map = new HashMap(); // 动态数据

    // 请求成功返回
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    // 请求错误返回
    public static <T> R<T> error(String message) {
        R<T> r = new R<T>();
        r.msg = message;
        r.code = 0;
        return r;
    }

    // 添加动态数据
    public R<T> add(String key, Objects value) {
        this.map.put(key, value);
        return this;
    }
}
