package com.webank.weid.suite.api.transportation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.ErrorCode;
import com.webank.weid.exception.WeIdBaseException;

public class ScanCodeUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScanCodeUtils.class);
    
    private static final String CHARSET = StandardCharsets.UTF_8.toString();

    private static final String FORMAT_NAME = "JPG";

    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;

    // LOGO宽度
    private static final int LOGO_WIDTH = 60;

    // LOGO高度
    private static final int LOGO_HEIGHT = 60;
        
    /** 条形码最小宽度. */
    private static final int BAR_CODE_WIDTH = 300;

    /** 条形码高度. */
    private static final int BAR_CODE_HEIGHT = 50;
    
    /** 条码左右上下填充. */
    private static final int BAR_CODE_PADDING = 5;
        
    /**
     * 生成不带文字的条形码并保存到指定文件中.
     *
     * @param content 条形码字符串
     * @param format 条形码编码格式
     * @param errorCorrectionLevel 容错级别
     * @param destPath 条形码图片保存文件路径
     * @return code of ErrorCode
     */
    public static Integer generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel errorCorrectionLevel, 
        String destPath
    ) {
        try {
            FileOutputStream outputStream = new FileOutputStream(destPath);
            generateBarCode(content, format, errorCorrectionLevel, outputStream);
            outputStream.flush();
            outputStream.close();
            return ErrorCode.SUCCESS.getCode();
        } catch (IOException e) {
            logger.error("[generateBarCode] generate barCode error.", e);
            return ErrorCode.UNKNOW_ERROR.getCode();
        }
    }

    /**
     * 生成不带文字的条形码并将条形码的字节输入到字节输出流中.
     *
     * @param content 条形码字符串
     * @param format 条形码编码格式
     * @param errorCorrectionLevel 容错级别
     * @param stream 字节输出流
     * @return code of ErrorCode
     */
    public static Integer generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel errorCorrectionLevel, 
        OutputStream stream
    ) {
        try {
            BufferedImage generateBarCode = createBarCodeImage(
                content, 
                format,
                errorCorrectionLevel
            );
            ImageIO.write(generateBarCode, FORMAT_NAME, stream);
            return ErrorCode.SUCCESS.getCode();
        } catch (IOException e) {
            logger.error("[generateBarCode] generate barCode error.", e);
            return ErrorCode.UNKNOW_ERROR.getCode();
        }
    }
    
    /**
     * 生成不带LOGO的二维码并保存到指定文件中.
     *
     * @param content 二维码字符串
     * @param destPath 二维码图片保存文件路径
     * @param errorCorrectionLevel 容错级别
     * @return code of ErrorCode
     */
    public static Integer generateQrCode(
        String content,
        ErrorCorrectionLevel errorCorrectionLevel,
        String destPath) {

        try {
            qrCodeEncode(content, null, destPath, errorCorrectionLevel, false);
            return ErrorCode.SUCCESS.getCode();
        } catch (WriterException e) {
            logger.error("generateQrCode into file WriterException.", e);
        } catch (IOException e) {
            logger.error("generateQrCode into file IOException.", e);
        }
        return ErrorCode.UNKNOW_ERROR.getCode();
    }

    /**
     * 生成不带LOGO的二维码并将二维码的字节输入到字节输出流中.
     *
     * @param content 二维码字符串
     * @param errorCorrectionLevel 容错级别
     * @param stream 字节输出流
     * @return code of ErrorCode
     */
    public static Integer generateQrCode(
        String content,
        ErrorCorrectionLevel errorCorrectionLevel,
        OutputStream stream) {

        try {
            BufferedImage image = createImage(content, null, errorCorrectionLevel, false);
            ImageIO.write(image, FORMAT_NAME, stream);
            return ErrorCode.SUCCESS.getCode();
        } catch (WriterException e) {
            logger.error("generateQrCode into OutputStream WriterException.", e);
        } catch (IOException e) {
            logger.error("generateQrCode into OutputStream IOException.", e);
        }
        return ErrorCode.UNKNOW_ERROR.getCode();
    }
    
    private static BufferedImage createImage(
        String content,
        String imgPath,
        ErrorCorrectionLevel errorCorrectionLevel,
        boolean needCompress)
        throws WriterException, IOException {

        Map<EncodeHintType, Object> hints = getEncodeHint(errorCorrectionLevel);
        
        BitMatrix bitMatrix = new MultiFormatWriter()
            .encode(
                content, 
                BarcodeFormat.QR_CODE, 
                QRCODE_SIZE, 
                QRCODE_SIZE, 
                hints
            );
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (StringUtils.isBlank(imgPath)) {
            return image;
        }
        // 插入图片
        insertImage(image, imgPath, needCompress);
        return image;
    }

    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress)
        throws IOException {

        File file = new File(imgPath);
        if (!file.exists()) {
            logger.error("imgPath:[{}] is not exists.", imgPath);
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > LOGO_WIDTH) {
                width = LOGO_WIDTH;
            }
            if (height > LOGO_HEIGHT) {
                height = LOGO_HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    private static void qrCodeEncode(
        String content,
        String imgPath,
        String destPath,
        ErrorCorrectionLevel errorCorrectionLevel,
        boolean needCompress)
        throws WriterException, IOException {

        BufferedImage image = createImage(content, imgPath, errorCorrectionLevel, needCompress);
        ImageIO.write(image, FORMAT_NAME, new File(destPath));
    }
    
    /**
     * 生成 图片缓冲.
     * 
     * @param content 条码内容
     * @param format 条码编码
     * @param correctionLevel 容错级别
     * @return 返回BufferedImage
     */
    private static BufferedImage createBarCodeImage(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel correctionLevel
    ) {
        
        try {
            logger.info("[generateBarCode] begin draw the barCode:{}", content);
            // 编码内容, 编码类型, 宽度, 高度, 设置参数
            BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(
                    content, 
                    format, 
                    BAR_CODE_WIDTH, 
                    BAR_CODE_HEIGHT, 
                    getEncodeHint(correctionLevel)
                );
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            //将条码绘制到白板中
            int  width = image.getWidth(); //白板的宽度
            int  height = image.getHeight() + BAR_CODE_PADDING * 2;//白板的高度
            BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outImage.createGraphics();
            // 抗锯齿
            setGraphics2D(g2d);
            // 设置白色
            setColorWhite(g2d);
            // 画条形码到新的面板
            g2d.drawImage(image, 0, BAR_CODE_PADDING, image.getWidth(), image.getHeight(), null);
            Color color = new Color(0, 0, 0);
            g2d.setColor(color);
            g2d.dispose();
            outImage.flush();
            logger.info("[generateBarCode] generate BarCode successfully.");
            return outImage;
        } catch (WriterException e) {
            logger.error("[generateBarCode] generate BarCode fail.", e);
            throw new WeIdBaseException(e.getMessage(), e);
        }
    }


    /**
     * 设置 Graphics2D 属性  (抗锯齿).
     * 
     * @param g2d  Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
            RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(s);
    }

    /**
     * 设置背景为白色.
     * 
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setColorWhite(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, 600, 600);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }
    
    /**
     * 设置条码参数.
     * 
     * @param errorCorrectionLevel 容错率
     * @return 返回hint数据
     */
    private static Map<EncodeHintType, Object> getEncodeHint(
        ErrorCorrectionLevel errorCorrectionLevel
    ) {
        
        Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, 1);
        return hints;
    }
}
