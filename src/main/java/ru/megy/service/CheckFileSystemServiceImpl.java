package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.megy.service.entity.ResultCheckFileSystem;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

@Service
@ConfigurationProperties(prefix="checkFileSystem")
public class CheckFileSystemServiceImpl implements CheckFileSystemService {
    private static final Logger logger = LoggerFactory.getLogger(CheckFileSystemServiceImpl.class);
    private static final long GB_VALUE = 1024L * 1024L * 1024L;

    @NotNull
    private List<String> directories;
    @NotNull
    private Long thresholdGByte;

    @Override
    public ResultCheckFileSystem checkFreeSpace() {
        boolean result = true;
        StringBuffer sb = new StringBuffer();

        for(String dirPath : directories) {
            File dir = new File(dirPath.trim());
            long freeSpace = dir.getFreeSpace();
            if (freeSpace < thresholdGByte * GB_VALUE) {
                result = false;
                sb.append("free space: ").append(freeSpace / GB_VALUE ).append("GB, path: ").append(dirPath).append("\r\n");
            }
        }

        return new ResultCheckFileSystem(result, sb.toString());
    }

    public List<String> getDirectories() {
        return directories;
    }

    public void setDirectories(List<String> directories) {
        this.directories = directories;
    }

    public Long getThresholdGByte() {
        return thresholdGByte;
    }

    public void setThresholdGByte(Long thresholdGByte) {
        this.thresholdGByte = thresholdGByte;
    }
}
