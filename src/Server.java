import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.*;
import java.io.*;

public class Server {

    // Method untuk menangkap layar
    public static BufferedImage captureScreen() throws AWTException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenCapture = robot.createScreenCapture(screenRect);
        return screenCapture;
    }

    // Method untuk mematikan komputer
    public static void shutdown() throws IOException {
        String shutdownCommand;

        // Perintah shutdown untuk Windows
        if (System.getProperty("os.name").startsWith("Windows")) {
            shutdownCommand = "shutdown -s -t 0";  // -t 0 berarti segera shutdown
        } else {
            // Perintah shutdown untuk Linux/Mac
            shutdownCommand = "sudo shutdown -h now";
        }

        Runtime.getRuntime().exec(shutdownCommand);
    }

    public static void receiveFile(Socket clientSocket, String savePath) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        }
        System.out.println("File berhasil diterima dan disimpan di " + savePath);
    }

    public static void runApplication(String command) throws IOException {
        Runtime.getRuntime().exec(command);
        System.out.println("Aplikasi dijalankan: " + command);
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            // Menjalankan server di port 5000
            serverSocket = new ServerSocket(5000);
            System.out.println("Menunggu koneksi dari client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client terhubung!");

            // Stream untuk mengirim gambar ke client dan menerima perintah
            OutputStream outputStream = clientSocket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                // Tangkap layar
                BufferedImage image = captureScreen();

                // Kirim gambar ke client
                ImageIO.write(image, "jpg", dataOutputStream);
                dataOutputStream.flush();

                // Cek jika ada perintah dari client
                if (reader.ready()) {
                    String command = reader.readLine();
                    if (command.equals("SHUTDOWN")) {
                        System.out.println("Menerima perintah shutdown");
                        shutdown();  // Jalankan perintah shutdown
                        break;
                    } else if (command.equals("FILE_TRANSFER")) {
                        System.out.println("Menerima file dari client...");
                        receiveFile(clientSocket, "path/to/save/file.txt");  // Tentukan path penyimpanan file
                    } else if (command.equals("RUN_APPLICATION")) {
                        System.out.println("Menjalankan aplikasi...");
                        runApplication("notepad.exe");  // Ganti dengan aplikasi yang ingin dijalankan
                    }
                }
                
                Thread.sleep(100);  // Delay untuk mengurangi beban
            }

            // Tutup socket client setelah selesai
            clientSocket.close();
            System.out.println("Koneksi client ditutup");

        } catch (IOException | AWTException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Pastikan serverSocket ditutup di dalam blok finally
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    System.out.println("ServerSocket ditutup");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
