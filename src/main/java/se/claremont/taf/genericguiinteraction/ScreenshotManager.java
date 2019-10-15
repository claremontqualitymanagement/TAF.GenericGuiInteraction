package se.claremont.taf.genericguiinteraction;

import se.claremont.taf.core.logging.LogFolder;
import se.claremont.taf.core.logging.LogLevel;
import se.claremont.taf.core.support.SupportMethods;
import se.claremont.taf.core.testcase.TestCase;
import se.claremont.taf.core.testrun.TestRun;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ScreenshotManager {

    private TestCase testCase;

    public ScreenshotManager(TestCase testCase){
        if(testCase == null) testCase = new TestCase();
        this.testCase = testCase;
    }

    public static void takeScreenshot(TestCase testCase) {
        ScreenshotManager g = new ScreenshotManager(testCase);
        g.takeScreenshot();
    }


    private void logDesktopScreenshot(String filePath) {
        String htmlFilePath = filePath.replace("\\", "/");
        String[] parts = htmlFilePath.split("/");
        htmlFilePath = parts[parts.length - 1];
        testCase.logDifferentlyToTextLogAndHtmlLog(LogLevel.INFO, "Saved desktop screenshot as '" + filePath + "'.",
                "Saved desktop screenshot as <a href=\"" + htmlFilePath + "\" target=\"_blank\">" +
                        "<span class=\"screenshotfile\">" + filePath + "</span></a><br>" +
                        "<a href=\"" + htmlFilePath + "\" target=\"_blank\">" +
                        "<img src=\"" + htmlFilePath + "\" alt=\"browser screenshot\" class=\"screenshot\">" +
                        "</a>");
    }


    /**
     * Saves a screenshot of all screens and writes its save path to the test case log.
     */
    public void takeScreenshot() {
        BufferedImage screenShot = GrabAllScreens();
        String fileName = saveScreenshotToFile(screenShot);
        logDesktopScreenshot(fileName);
    }

    private String saveScreenshotToFile(BufferedImage screenShot) {
        String filePath = LogFolder.testRunLogFolder + testCase.testName + TestRun.getFileCounter() + ".png";
        TestRun.increaseFileCounter();
        try {
            if (screenShot == null) {
                testCase.log(LogLevel.INFO, "Could not take desktop screenshot.");
                return null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenShot, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            SupportMethods.saveToFile(imageInByte, filePath);
        } catch (Exception e) {
            testCase.log(LogLevel.INFO, "Could not save desktop screenshot. Error: " + e.toString());
            return null;
        }
        return filePath;
    }


    private BufferedImage GrabAllScreens() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        Rectangle allScreenBounds = new Rectangle();
        for (GraphicsDevice screen : screens) {
            Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();

            allScreenBounds.width += screenBounds.width;
            allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
        }

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            testCase.log(LogLevel.DEBUG, "Could not start Robot framework for taking desktop screenshot.");
            return null;
        }
        BufferedImage screenShot = robot.createScreenCapture(allScreenBounds);
        return screenShot;
    }

}
