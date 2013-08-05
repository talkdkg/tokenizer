/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.xaloon.core.impl.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.inject.Named;

import org.xaloon.core.api.image.ImageResizer;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;

/**
 * @author vytautas r.
 */
@Named("imageResizer")
public class DefaultImageResizer implements ImageResizer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String IMAGE_TYPE_PNG = "PNG";

	@Override
	public InputStream resize(InputStream is, int newWidth, int newHeight, boolean isFixed) throws IOException {
		BufferedImage temp = ImageIO.read(is);

		return resizeInternal(temp, newWidth, newHeight, isFixed);
	}

	@Override
	public InputStream resize(URL url, int newWidth, int newHeight, boolean isFixed) throws IOException {
		BufferedImage temp = ImageIO.read(url);

		return resizeInternal(temp, newWidth, newHeight, isFixed);
	}

	private InputStream resizeInternal(BufferedImage image, int newWidth, int newHeight, boolean isFixed) throws IOException {
		Image resizedImage;

		// Current size
		int iWidth = image.getWidth(null);
		int iHeight = image.getHeight(null);

		// New size
		int resizedWidth = newWidth;
		int resizedHeight = newHeight;

		// Calculate new size
		if (iWidth > iHeight || isFixed) {
			resizedHeight = (newWidth * iHeight) / iWidth;
			if (resizedHeight < newHeight) { // if height is lower than required
				resizedHeight = newHeight;
				resizedWidth = (newHeight * iWidth) / iHeight;// e.g., 700x273->158x61
			}
			resizedImage = image.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
		} else {
			resizedWidth = (newWidth * iWidth) / iHeight;
			resizedImage = image.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
		}

		// Create the buffered image.
		BufferedImage bufferedImage = new BufferedImage(resizedImage.getWidth(null), resizedImage.getHeight(null), BufferedImage.TYPE_INT_RGB);

		// Copy image to buffered image.
		Graphics g = bufferedImage.createGraphics();
		g.drawImage(resizedImage, 0, 0, null);
		g.dispose();
		if (isFixed) {
			int x = 0;
			int y = 0;
			if (resizedWidth > newWidth) {
				x = (resizedWidth - newWidth) / 2;
			} else {
				y = (resizedHeight - newHeight) / 2;
			}
			BufferedImage dest = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			g = dest.getGraphics();
			g.drawImage(bufferedImage, 0, 0, newWidth, newHeight, x, y, x + newWidth, y + newHeight, null);
			g.dispose();

			return convertToInputStream(dest);

		}
		// Encodes image as a JPEG data stream
		return convertToInputStream(bufferedImage);
	}

	private InputStream convertToInputStream(BufferedImage bufferedImage) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		PNGEncodeParam.RGB pngEncodeParam = new PNGEncodeParam.RGB();
		ImageEncoder encoder = ImageCodec.createImageEncoder(IMAGE_TYPE_PNG, outputStream, pngEncodeParam);
		encoder.encode(bufferedImage);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
}
