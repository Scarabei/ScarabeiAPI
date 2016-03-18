package com.jfixby.red.desktop.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.jfixby.cmns.api.color.Color;
import com.jfixby.cmns.api.color.Colors;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.desktop.ImageAWTComponent;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.file.FileInputStream;
import com.jfixby.cmns.api.file.FileOutputStream;
import com.jfixby.cmns.api.image.ArrayColorMap;
import com.jfixby.cmns.api.image.ArrayColorMapSpecs;
import com.jfixby.cmns.api.image.ColorMap;
import com.jfixby.cmns.api.image.EditableColorMap;
import com.jfixby.cmns.api.image.GrayMap;
import com.jfixby.cmns.api.image.ImageProcessing;
import com.jfixby.cmns.api.io.InputStream;
import com.jfixby.cmns.api.io.OutputStream;
import com.jfixby.cmns.api.log.L;

public class RedImageAWT implements ImageAWTComponent {

    @Override
    public BufferedImage readFromFile(File image_file) throws IOException {
	Debug.checkNull("image_file", image_file);
	FileInputStream is = image_file.newInputStream();
	BufferedImage bad_image = readFromStream(is);
	if (bad_image == null) {
	    L.d("Failed to read image", image_file);
	    L.d("    exists", image_file.exists());
	    L.d("      hash", image_file.calculateHash());
	    L.d("      size", image_file.getSize());
	    File parent = image_file.getFileSystem().newFile(image_file.getAbsoluteFilePath().parent());
	    parent.listChildren().print();
	    throw new IOException("Failed to read image: " + image_file);
	}
	is.close();
	return bad_image;
    }

    @Override
    public BufferedImage readFromStream(InputStream is) throws IOException {
	java.io.InputStream java_is = is.toJavaInputStream();
	BufferedImage bad_image = ImageIO.read(java_is);
	return bad_image;
    }

    @Override
    public void writeToFile(java.awt.Image java_image, File file, String file_type, int image_mode) throws IOException {
	Debug.checkNull("java_image", java_image);
	Debug.checkNull("file", file);
	Debug.checkNull("file_type", file_type);

	FileOutputStream os = file.newOutputStream();
	this.writeToStream(java_image, os, file_type, image_mode);
	os.close();
    }

    @Override
    public void writeToFile(java.awt.Image java_image, File file, String file_type) throws IOException {
	writeToFile(java_image, file, file_type, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public ArrayColorMap readAWTColorMap(java.io.InputStream java_is) throws IOException {
	BufferedImage bad_image = ImageIO.read(java_is);
	if (bad_image == null) {
	    L.d("Failed to read image", java_is);

	    throw new IOException("Failed to read image: " + java_is);
	}
	return this.newAWTColorMap(bad_image);
    }

    @Override
    public BufferedImage toAWTImage(ColorMap image_function) {
	int h = image_function.getHeight();
	int w = image_function.getWidth();
	BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	int[] data = ((DataBufferInt) im.getRaster().getDataBuffer()).getData();
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		int K = i + j * w;
		Color color_c = image_function.valueAt(i, j);
		data[K] = color_c.toInteger();
	    }
	}
	return im;
    }

    static final Vector<String> palette = new Vector<String>();
    static String next_line_L = "\n";
    static float delta;
    static {
	boolean use_grayscale_symbols = true;
	if (use_grayscale_symbols) {
	    // String color0 = "█";
	    // String color1 = "▓";
	    // String color2 = "▒";
	    // String color3 = "░";
	    // String color4 = " ";
	    palette.add("█");
	    palette.add("▓");
	    palette.add("▒");
	    palette.add("░");
	    palette.add(" ");
	} else {
	    // int N = ASCI_palette.length();
	    // for (int i = 0; i < N; i++) {
	    // palette.add(ASCI_palette.charAt(i) + "");
	    // }
	    // palette.add("█");
	    // palette.add("▓");
	    // palette.add("▓");
	    // palette.add("▒");
	    // palette.add("▒");
	    // palette.add("░");
	    // palette.add("░");
	    // palette.add(" ");
	    // palette.add(" ");

	}

	delta = 1f / palette.size();

    }

    private static String palette(float gray) {
	int index = (int) ((gray) * (1f / delta));
	if (index == palette.size()) {
	    index--;
	}
	if (index < 0) {
	    index = 0;
	}
	String val = palette.get(index);
	return val;
    }

    public static String toString(EditableColorMap argb) {
	String result = "[" + argb.getWidth() + ";" + argb.getHeight() + "]" + next_line_L;

	// Log.d("delta", delta);

	for (int j = -1; j < argb.getHeight() + 1; j++) {
	    String line = "";
	    for (int i = -1; i < argb.getWidth() + 1; i++) {

		Color color = argb.valueAt(i, j);

		// Log.d("gray", gray);

		String val = palette(color.getGrayscaleValue());

		// line = line + "[" + val + "]";
		line = line + val + val;
	    }
	    result = result + line + next_line_L;
	}
	return result;
    }

    @Override
    public ArrayColorMap readAWTColorMap(File image_file) throws IOException {
	FileInputStream is = image_file.newInputStream();
	java.io.InputStream java_is = is.toJavaInputStream();
	ArrayColorMap map = this.readAWTColorMap(java_is);
	java_is.close();
	is.close();
	return map;
    }

    @Override
    public ArrayColorMap newAWTColorMap(BufferedImage img) {
	Debug.checkNull(img);

	ArrayColorMapSpecs specs = ImageProcessing.newArrayColorMapSpecs();
	specs.setWidth(img.getWidth());
	specs.setHeight(img.getHeight());
	specs.setDefaultColor(Colors.BLACK());

	ArrayColorMap array = ImageProcessing.newArrayColorMap(specs);

	for (int j = 0; j < array.getHeight(); j++) {
	    for (int i = 0; i < array.getWidth(); i++) {
		int rgb = img.getRGB(i, j);
		array.setValue(i, j, Colors.newColor(rgb));
	    }
	}

	return array;
    }

    @Override
    public void writeToFile(ColorMap image, File image_file, String file_type) throws IOException {
	this.writeToFile(this.toAWTImage(image), image_file, file_type);
    }

    @Override
    public BufferedImage toAWTImage(GrayMap image_function) {
	int h = image_function.getHeight();
	int w = image_function.getWidth();
	BufferedImage im = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
	byte[] data = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();
	for (int j = 0; j < h; j++) {
	    for (int i = 0; i < w; i++) {
		int K = i + j * w;
		float color_c = image_function.valueAt(i, j);
		data[K] = (byte) (255 * color_c);
		// im.setRGB(i, j, rgb);
	    }
	}
	return im;
    }

    @Override
    public void writeToStream(Image java_image, OutputStream outputStream, String file_type, int awtImageMode)
	    throws IOException {
	int width = java_image.getWidth(null);
	int height = java_image.getHeight(null);
	BufferedImage out = new BufferedImage(width, height, awtImageMode);
	Graphics2D g2 = out.createGraphics();
	g2.drawImage(java_image, 0, 0, null);

	java.io.OutputStream java_os = outputStream.toJavaOutputStream();
	ImageIO.write(out, file_type, java_os);
	outputStream.flush();
    }

}