import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;

public class Client {

    public static void main(String[] args) {
        try {
            // Koneksi ke server
            Socket socket = new Socket("localhost", 5000);  // Ganti "localhost" dengan IP server
            System.out.println("Terhubung ke server!");

            // Stream untuk menerima gambar dan mengirim perintah
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Membuat frame GUI untuk menampilkan gambar
            JFrame frame = new JFrame("Remote Desktop Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel imageLabel = new JLabel();
            frame.add(imageLabel);
            frame.setSize(800, 600);
            frame.setVisible(true);
            frame.setLayout(new BorderLayout());

            // Tambahkan tombol shutdown
            JButton shutdownButton = new JButton("Shutdown Server");
            frame.add(shutdownButton, BorderLayout.SOUTH);

            shutdownButton.addActionListener(e -> {
                try {
                    // Kirim perintah shutdown ke server
                    writer.write("SHUTDOWN\n");
                    writer.flush();
                    System.out.println("Perintah shutdown dikirim!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // Tombol kirim file
            JButton fileTransferButton = new JButton("Kirim File");
            frame.add(fileTransferButton, BorderLayout.NORTH);

            fileTransferButton.addActionListener(e -> {
                try {
                    writer.write("FILE_TRANSFER\n");
                    writer.flush();
                    sendFile(socket, "path/to/your/file.txt");  // Ganti dengan path file yang ingin dikirim
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // Tombol jalankan aplikasi
            JButton runAppButton = new JButton("Jalankan Aplikasi");
            frame.add(runAppButton, BorderLayout.EAST);

            runAppButton.addActionListener(e -> {
                try {
                    writer.write("RUN_APPLICATION\n");
                    writer.flush();
                    System.out.println("Perintah menjalankan aplikasi dikirim!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            while (true) {
                // Baca gambar dari stream
                BufferedImage image = ImageIO.read(dataInputStream);

                // Tampilkan gambar di GUI
                if (image != null) {
                    imageLabel.setIcon(new ImageIcon(image));
                    frame.repaint();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFile(Socket socket, String filePath) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        FileInputStream fileInputStream = new FileInputStream(filePath);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) > 0) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        dataOutputStream.flush();
        fileInputStream.close();
        System.out.println("File berhasil dikirim ke server");
    }
}
