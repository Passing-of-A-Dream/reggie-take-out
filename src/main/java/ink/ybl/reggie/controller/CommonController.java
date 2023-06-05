package ink.ybl.reggie.controller;

import ink.ybl.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String uploadPath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 获取文件的扩展名
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        // 使用uuid作为文件名，防止生成的临时文件重复
        String fileName = java.util.UUID.randomUUID().toString() + extension;

        // 判断文件夹是否存在，不存在则创建
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(uploadPath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @return
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // 通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(uploadPath + name));
            // 通过输出流写出文件内容
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();

            // 关闭流
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
