package com.dry3.service.Impl;

import com.dry3.service.IFileService;
import com.dry3.util.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by dry3
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        //获得上传文件扩展名后缀
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
        //生成上传文件目标文件名
        String targetFileName = UUID.randomUUID().toString() + fileExtensionName;
        //开始上传文件
        logger.info("开始上传文件,上传的文件名:{},上传的路径:{},新文件名:{}", fileName, path, targetFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, targetFileName);
        try {
            file.transferTo(targetFile);
            //文件上传成功
            //将upload文件夹中文件上传到FTP服务器中
            if (!FTPUtil.uploadFile(Lists.newArrayList(targetFile))) {
                targetFileName = null;
            }
            //删除upload文件夹下新增文件
            if (!targetFile.delete()) {
                logger.error("文件删除失败");
            }
        } catch (IOException e) {
            logger.error("文件上传异常", e);
            return null;
        }
        return targetFileName;
    }
    /*
           test
    public static void main(String[] args) {
        String fileName = "abc.abc.abc.jps";
        System.out.println(fileName.substring(fileName.lastIndexOf(".")));
    }*/


}
