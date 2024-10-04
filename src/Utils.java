import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Utils {

    // Method untuk kompresi gambar
    public static byte[] compressImage(BufferedImage image, float quality) throws IOException {
        // Mengatur output ke ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Mendapatkan ImageWriter untuk format JPEG
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        // Mengatur parameter kompresi
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);  // 0.0f (kualitas terendah) hingga 1.0f (kualitas tertinggi)

        // Menulis gambar ke output
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
        ios.close();
        writer.dispose();

        return baos.toByteArray();  // Mengembalikan byte array gambar terkompresi
    }
}
