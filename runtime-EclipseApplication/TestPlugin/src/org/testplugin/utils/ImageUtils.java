package org.testplugin.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtils {

	public static byte[] getBytesFromImage(Image image)
	{
		BufferedImage bu = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);

		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		try
		{
			ImageIO.write(bu,"png", bas);
			byte[] data = bas.toByteArray();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] getBytesFromFile(File file) throws IOException
	{
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE)
		{
			throw new IOException("File is too large "+file.getName());
		}
		byte[] bytes = new byte[(int)length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0)
		{
			offset += numRead;
		}

		if (offset < bytes.length)
		{
			throw new IOException("Could not completely read file "+file.getName());
		}

		is.close();
		return bytes;
	} 	

	public static Image getImage(byte[] bytes, boolean isThumbnail, String imageType) throws IOException
	{
	   ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

       Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName( imageType );
       
       //ImageIO is a class containing static convenience methods for locating ImageReaders
       //and ImageWriters, and performing simple encoding and decoding. 
       ImageReader reader = readers.next();
	   Object source = bis; // File or InputStream, it seems file is OK

	   ImageInputStream iis = ImageIO.createImageInputStream(source);
	   //Returns an ImageInputStream that will take its input from the given Object

       reader.setInput(iis, true);
       ImageReadParam param = reader.getDefaultReadParam();

       Image image = reader.read(0, param);
       return image;
	}  

    public static String store(Image image, String fileType, String imageName) throws IOException {
        File imageDir = new File("fotos");
        if (!imageDir.exists())
        	imageDir.mkdirs();
        
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), 
        												image.getHeight(null),
        												BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
        File imageFile = new File(imageDir, imageName);
        ImageIO.write(bufferedImage, fileType, imageFile);
         
        return imageFile.getPath();
    }
}