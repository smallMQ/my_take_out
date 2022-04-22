package com.smallmq.controller;

import com.smallmq.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/common")
@Slf4j
@ConfigurationProperties(prefix = "image")
public class CommonController {

    private String FILE_PATH;

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    public void setFILE_PATH(String FILE_PATH) {
        this.FILE_PATH = FILE_PATH;
    }

    // 文件上传
    @PostMapping("/upload")
    public Response upload(MultipartFile file) throws IOException {
        // 获取系统时间戳
        String time = System.currentTimeMillis() + "";
        // 生成随机uuid
        String uuid = java.util.UUID.randomUUID().toString();
        // 生成文件名
        String fileName = time + uuid + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        file.transferTo(new File(FILE_PATH + fileName));
        System.out.println(FILE_PATH + file.getOriginalFilename() );
        log.info("文件上传");
        return Response.success(fileName);
    }

    // 文件下载
    @GetMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(FILE_PATH + name);
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        // flush
        outputStream.flush();
        // close
        fileInputStream.close();
        outputStream.close();
    }
}
