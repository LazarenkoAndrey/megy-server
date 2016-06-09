package ru.megy.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicLong;

public class FUtils {
    private static final Logger logger = LoggerFactory.getLogger(FUtils.class);

    public static boolean fileComparison(Path filePath1, Path filePath2) {
        File file1 = filePath1.toFile();
        File file2 = filePath2.toFile();

        try {
            return FileUtils.contentEquals(file1, file2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static long getCountItems(Path root) {
        AtomicLong count = new AtomicLong(0);
        try {
            Files.walk(root)
                .forEach(path -> count.addAndGet(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return count.get();
    }

    public static long getSizeItems(Path root) {
        AtomicLong size = new AtomicLong(0);
        try {
            Files.walk(root)
                .map(path -> path.toFile())
                .filter(file -> file.isFile())
                .forEach(file -> size.addAndGet(file.length()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return size.get();
    }

    public static String sha512(Path filePath) {
        try(InputStream fis = Files.newInputStream(filePath)) {
            String hash = DigestUtils.sha512Hex(fis);
            return hash;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha512(SortedSet<String> hashList) {
        MessageDigest messageDigest = DigestUtils.getSha512Digest();
        try {
            for(String hash : hashList) {
                messageDigest = DigestUtils.updateDigest(messageDigest, hash);
            }
            byte[] digest = messageDigest.digest();
            return Hex.encodeHexString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getImageTime(Path path) {
        Date imageTime = null;
        try {
            ImageMetadata metadata = Imaging.getMetadata(path.toFile());
            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                TiffField tiffField = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_DATE_TIME);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                imageTime = sdf.parse(tiffField.getStringValue());
            } else {
                throw new IOException("Image isn't jpeg");
            }
        } catch (Exception e) {
            logger.warn("Error read metadata from image {}", path);
        }

        return imageTime;
    }

}
