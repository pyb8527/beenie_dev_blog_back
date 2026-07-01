package com.beenie.backend.infrastructure.file;

import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * 업로드 이미지를 최대 너비 1200px 로 리사이즈하고 WebP 로 변환한다.
 * WebP ImageIO 플러그인을 사용할 수 없는 환경에서는 PNG 로 대체(fallback) 한다.
 */
@Slf4j
@Component
public class ImageProcessor {

    private static final int MAX_WIDTH = 1200;

    public ProcessedImage process(byte[] original) {
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(original));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }
        if (image == null) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }

        BufferedImage resized = resizeIfNeeded(image);

        byte[] webp = tryEncode(resized, "webp");
        if (webp != null) {
            return new ProcessedImage(webp, "image/webp", "webp");
        }

        byte[] png = tryEncode(resized, "png");
        if (png != null) {
            return new ProcessedImage(png, "image/png", "png");
        }
        throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    private BufferedImage resizeIfNeeded(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        if (width <= MAX_WIDTH) {
            return original;
        }
        int newWidth = MAX_WIDTH;
        int newHeight = (int) Math.round(height * (MAX_WIDTH / (double) width));

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resized;
    }

    private byte[] tryEncode(BufferedImage image, String formatName) {
        try {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
            if (!writers.hasNext()) {
                return null;
            }
            ImageWriter writer = writers.next();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(baos)) {
                writer.setOutput(ios);
                ImageWriteParam param = writer.getDefaultWriteParam();
                writer.write(null, new IIOImage(toRgbIfNeeded(image, formatName), null, null), param);
            } finally {
                writer.dispose();
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.warn("{} 인코딩 실패, 다음 포맷으로 대체합니다.", formatName, e);
            return null;
        }
    }

    private BufferedImage toRgbIfNeeded(BufferedImage image, String formatName) {
        if (!"webp".equalsIgnoreCase(formatName)) {
            return image;
        }
        return image;
    }
}
