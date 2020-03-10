/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.suite.transportation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.webank.weid.exception.WeIdBaseException;
import com.webank.weid.suite.api.transportation.inf.BarCodeTransportation;

/**
 * 条形码传输协议抽象类定义.
 * 
 * @author v_wbgyang
 *
 */
public abstract class AbstractBarCodeTransportation 
    extends AbstractJsonTransportation
    implements BarCodeTransportation {
    
    private static final Logger logger =
        LoggerFactory.getLogger(AbstractBarCodeTransportation.class);
    
    /** 条形码最小宽度 */
    private static final int WIDTH = 300;

    /** 条形码高度 */
    private static final int HEIGHT = 50;
    
    /** 条码左右上下填充*/
    private static final int PADDING = 5;
    
    protected static final String IMG_FORMATE = "jpg";
    
    /**
     * 生成 图片缓冲.
     * 
     * @param content 条码内容
     * @param format 条码编码
     * @param correctionLevel 容错级别
     * @return 返回BufferedImage
     */
    protected BufferedImage generateBarCode(
        String content, 
        BarcodeFormat format, 
        ErrorCorrectionLevel correctionLevel) {
        
        try {
            logger.info("[generateBarCode] begin draw the barCode:{}", content);
            // 编码内容, 编码类型, 宽度, 高度, 设置参数
            BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(content, format, WIDTH, HEIGHT, getEncodeHint(correctionLevel));
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            
            //将条码绘制到白板中
            int  width = image.getWidth(); //白板的宽度
            int  height = image.getHeight() + PADDING * 2;//白板的高度
            BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outImage.createGraphics();
            // 抗锯齿
            setGraphics2D(g2d);
            // 设置白色
            setColorWhite(g2d);
            // 画条形码到新的面板
            g2d.drawImage(image, 0, PADDING, image.getWidth(), image.getHeight(), null);
            Color color=new Color(0, 0, 0);
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
    private void setGraphics2D(Graphics2D g2d){
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
    private void setColorWhite(Graphics2D g2d){
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0,0,600,600);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }
    
    /**
     * 设置条码参数.
     * 
     * @param correctionLevel 容错率
     * @return 返回hint数据
     */
    private Map<EncodeHintType, Object> getEncodeHint(ErrorCorrectionLevel correctionLevel) {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        hints.put(EncodeHintType.ERROR_CORRECTION, correctionLevel);
        return hints;
    }
}
