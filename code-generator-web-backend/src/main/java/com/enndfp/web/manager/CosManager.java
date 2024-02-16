package com.enndfp.web.manager;

import com.enndfp.web.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cos 对象存储操作
 *
 * @author Enndfp
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    // 复用对象
    private TransferManager transferManager;

    // bean 加载完成后执行
    @PostConstruct
    public void init() {
        // 执行初始化逻辑
        System.out.println("Bean initialized!");
        // 多线程并发上传下载
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        transferManager = new TransferManager(cosClient, threadPool);
    }

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     * @return 文件
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 下载对象到本地文件
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return 下载对象
     * @throws InterruptedException 中断异常
     */
    public Download download(String key, String localFilePath) throws InterruptedException {
        File downloadFile = new File(localFilePath);
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        Download download = transferManager.download(getObjectRequest, downloadFile);
        // 同步等待下载完成
        download.waitForCompletion();
        return download;
    }
}
