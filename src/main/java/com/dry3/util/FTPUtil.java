package com.dry3.util;import org.apache.commons.net.ftp.FTPClient;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import java.io.File;import java.io.FileInputStream;import java.io.IOException;import java.util.List;/** * Created by dry3 */public class FTPUtil {    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);    private static final String FTP_IP = PropertiesUtil.getProperty("ftp.server.ip");    private static final String FTP_USER = PropertiesUtil.getProperty("ftp.user");    private static final String FTP_PWD = PropertiesUtil.getProperty("ftp.pass");    private String ip;    private int port;    private String user;    private String pwd;    private FTPClient ftpClient;    public FTPUtil(String ip, int port, String user, String pwd) {        this.ip = ip;        this.port = port;        this.user = user;        this.pwd = pwd;    }    //对外开放方法    public static boolean uploadFile(List<File> fileList) throws IOException {        FTPUtil ftpUtil = new FTPUtil(FTP_IP,21,FTP_USER,FTP_PWD);        logger.info("开始上传文件至FTP服务器");        boolean uploaded = ftpUtil.uploadFile("img",fileList);        logger.info("结束上传,上传结果:{}", uploaded);        return uploaded;    }    private boolean uploadFile(String removePath, List<File> fileList) throws IOException {        boolean uploaded = true;        FileInputStream fis = null;        //连接FTP服务器        if (uploaded = connectServer(this.ip,this.port,this.user,this.pwd)) {            try {                ftpClient.changeWorkingDirectory(removePath);                ftpClient.setControlEncoding("UTF-8");                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);                ftpClient.setBufferSize(1024);                ftpClient.enterLocalPassiveMode();                for (File fileItem : fileList) {                    fis = new FileInputStream(fileItem);                    ftpClient.storeFile(fileItem.getName(),fis);                }            } catch (IOException e) {                uploaded = false;                logger.error("上传文件至FTP服务器异常",e);            } finally {                fis.close();                ftpClient.disconnect();            }        }        return uploaded;    }    private boolean connectServer(String ip, int port, String user, String pwd) {        boolean isSucces = false;        ftpClient = new FTPClient();        try {            ftpClient.connect(ip);            isSucces = ftpClient.login(user,pwd);        } catch (IOException e) {            logger.error("连接FTP服务器失败,请核对ip或账号密码",e);        }        return isSucces;    }    public String getIp() {        return ip;    }    public void setIp(String ip) {        this.ip = ip;    }    public int getPort() {        return port;    }    public void setPort(int port) {        this.port = port;    }    public String getUser() {        return user;    }    public void setUser(String user) {        this.user = user;    }    public String getPwd() {        return pwd;    }    public void setPwd(String pwd) {        this.pwd = pwd;    }}