package ink.ybl.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> excptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")) {
            String[] errMsgArr = ex.getMessage().split(" ");
            String msg = errMsgArr[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }


    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> excptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
