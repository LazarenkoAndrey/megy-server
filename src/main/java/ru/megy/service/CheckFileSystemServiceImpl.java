package ru.megy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.megy.service.entity.ResultCheckFileSystem;

import java.io.File;
import java.util.List;

@Service
@ConfigurationProperties(prefix="checkFileSystem")
public class CheckFileSystemServiceImpl implements CheckFileSystemService {
    private static final Logger logger = LoggerFactory.getLogger(CheckFileSystemServiceImpl.class);
    private static final long GB_VALUE = 1024L * 1024L * 1024L;

    private List<String> dirForCheckFreeSpace;
    private Long thresholdForCheckFreeSpace;

    public ResultCheckFileSystem checkFreeSpace() throws Exception {
        logger.info("checkFreeSpace");

        if(dirForCheckFreeSpace==null || dirForCheckFreeSpace.size()==0) {
            throw new IllegalArgumentException("sfileserver.checkFreeSpace.dir is empty");
        }

        if(thresholdForCheckFreeSpace==null) {
            throw new IllegalArgumentException("sfileserver.checkFreeSpace.threshold is empty");
        }

        boolean result = true;
        StringBuffer sb = new StringBuffer();

        for(String dirPath : dirForCheckFreeSpace) {
            File dir = new File(dirPath.trim());
            long freeSpace = dir.getFreeSpace();
            if (freeSpace < thresholdForCheckFreeSpace * GB_VALUE) {
                result = false;
                sb.append("free space: ").append(freeSpace / GB_VALUE ).append("GB, path: ").append(dirPath).append("\r\n");
            }
        }

        return new ResultCheckFileSystem(result, sb.toString());
    }

    public List<String> getDirForCheckFreeSpace() {
        return dirForCheckFreeSpace;
    }

    public void setDirForCheckFreeSpace(List<String> dirForCheckFreeSpace) {
        this.dirForCheckFreeSpace = dirForCheckFreeSpace;
    }

    public Long getThresholdForCheckFreeSpace() {
        return thresholdForCheckFreeSpace;
    }

    public void setThresholdForCheckFreeSpace(Long thresholdForCheckFreeSpace) {
        this.thresholdForCheckFreeSpace = thresholdForCheckFreeSpace;
    }
}
