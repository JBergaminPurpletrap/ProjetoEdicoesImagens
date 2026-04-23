package com.imageai.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class UpscaleService {

    public static void upscale(String inputPath, String outputPath) throws Exception {

        BufferedImage originalImage = ImageIO.read(new File(inputPath));

        if (originalImage == null) {
            throw new RuntimeException("Erro ao ler imagem para upscale!");
        }

        int width = originalImage.getWidth() * 2;
        int height = originalImage.getHeight() * 2;

        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(originalImage, 0, 0, width, height, null);

        g2d.dispose();

        ImageIO.write(scaledImage, "png", new File(outputPath));
    }
}