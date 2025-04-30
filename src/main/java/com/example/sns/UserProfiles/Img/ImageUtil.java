package com.example.sns.UserProfiles.Img;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtil {

    private static final int WIDTH = 200; // 리사이징할 이미지 너비
    private static final int HEIGHT = 200; // 리사이징할 이미지 높이

    /**
     * 업로드된 이미지를 200x200 크기로 리사이징하여 바이트 배열로 반환하는 메서드
     *
     * @param file 업로드된 이미지 파일 (MultipartFile)
     * @return 리사이징된 이미지의 바이트 배열
     * @throws IOException 이미지 처리 중 오류 발생 시 예외 발생
     */
    public static byte[] resizeImage(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedImage originalImage = ImageIO.read(inputStream);

        if (originalImage == null) {
            throw new IOException("이미지를 읽을 수 없습니다.");
        }

        // 새로운 크기의 BufferedImage 생성 (RGB 타입)
        BufferedImage resizedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // 고품질 리사이징을 위해 렌더링 힌트 설정
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, WIDTH, HEIGHT, null);
        g2d.dispose();

        // 리사이징된 이미지를 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }

    /**
     * 리사이징된 이미지를 파일로 저장하는 메서드
     *
     * @param file       업로드된 이미지 파일 (MultipartFile)
     * @param outputPath 저장할 파일 경로
     * @throws IOException 이미지 저장 중 오류 발생 시 예외 발생
     */
    public static void saveResizedImage(MultipartFile file, String outputPath) throws IOException {
        byte[] resizedImageBytes = resizeImage(file);
        File outputFile = new File(outputPath);

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(resizedImageBytes);
        }
    }
}
