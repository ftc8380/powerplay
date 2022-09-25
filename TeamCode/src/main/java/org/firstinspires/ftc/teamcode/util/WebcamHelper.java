// region Java Imports
package org.firstinspires.ftc.teamcode.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.system.ContinuationSynchronizer;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
// endregion

public class WebcamHelper {

    public String deviceName;
    public int segments = 3;
    int[] pixels;

    private double compression = 1.0;

    private CameraManager cameraManager;
    private WebcamName cameraName;
    private Camera camera;
    private CameraCaptureSession cameraCaptureSession;

    private static int captureCounter = 0;
    public File captureDirectory = AppUtil.ROBOT_DATA_DIR;

    private EvictingBlockingQueue<Bitmap> frameQueue;
    private Handler callbackHandler;

    public WebcamHelper (String cameraName, HardwareMap hardwareMap) {

        this.deviceName = cameraName;
        this.cameraName = hardwareMap.get(WebcamName.class, cameraName);

        callbackHandler = CallbackLooper.getDefault().getHandler();
        cameraManager = ClassFactory.getInstance().getCameraManager();
        initiate();
    }

    /**
     * Initialize the camera
     */
    private void initiate () {

        // How many frames to store in memory before discarding
        int capacity = 2;

        // Discard excess frames from memory
        frameQueue = new EvictingBlockingQueue<>(new ArrayBlockingQueue<>(capacity));
        frameQueue.setEvictAction(Bitmap::recycle);

        AppUtil.getInstance().ensureDirectoryExists(captureDirectory);

        openCamera();
        if (camera == null) return;

        startCamera();

    }

    /**
     * Get a single bitmap frame from the webcam
     * @return returns a bitmap containing the taken image
     */
    public Bitmap getFrame () {

        Bitmap bmp;

        do {
            bmp = frameQueue.poll();
        } while(bmp == null);

        return bmp;
    }

    /**
     * Stop and turn off the camera
     */
    public void stopCamera () {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.stopCapture();
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (camera != null) {
            camera.close();
            camera = null;
        }
    }

    /**
     * Turns on the camera
     */
    public void openCamera () {
        if (camera != null) return;

        // Timeout camera if it takes longer than 10 seconds
        Deadline deadline = new Deadline(10, TimeUnit.SECONDS);

        camera = cameraManager.requestPermissionAndOpenCamera(deadline, cameraName, null);
    }

    /**
     * Start accepting frames from the camera
     */
    private void startCamera () {
        if (cameraCaptureSession != null) return;

        final int imageFormat = ImageFormat.YUY2;

        CameraCharacteristics cameraCharacteristics = cameraName.getCameraCharacteristics();

        final Size size = cameraCharacteristics.getDefaultSize(imageFormat);
        final int fps = cameraCharacteristics.getMaxFramesPerSecond(imageFormat, size);

        final ContinuationSynchronizer<CameraCaptureSession> synchronizer = new ContinuationSynchronizer<>();
        try {
            camera.createCaptureSession(Continuation.create(callbackHandler, new CameraCaptureSession.StateCallbackDefault() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        final CameraCaptureRequest captureRequest = camera.createCaptureRequest(imageFormat, size, fps);
                        session.startCapture(captureRequest,
                                (session1, request, cameraFrame) -> {
                                    Bitmap bmp = captureRequest.createEmptyBitmap();
                                    cameraFrame.copyToBitmap(bmp);
                                    frameQueue.offer(bmp);
                                },
                                Continuation.create(callbackHandler, (session12, cameraCaptureSequenceId, lastFrameNumber) -> {})
                        );
                        synchronizer.finish(session);
                    } catch (CameraException | RuntimeException e) {
                        session.close();
                        synchronizer.finish(null);
                    }
                }
            }));
        } catch (CameraException | RuntimeException e) {
            synchronizer.finish(null);
        }

        // Wait for all the asynchrony to complete
        try {
            synchronizer.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        cameraCaptureSession = synchronizer.getValue();
    }

    public void setCompression(double c) {
        this.compression = c;
    }

    /**
     * Get the index of the highest number in an array
     * @param  sections  array of integers
     * @return           index of the highest number in the array
     */
    public int highestInArray (int[] sections) {

        int index = 0;

        for (int i = 0; i < sections.length; i++) {
            index = sections[i] > sections[index] ? i : index;
        }

        return index;
    }

    /**
     * Save the bitmap as a JPG image
     * @param  bmp  bitmap to save
     * @return      boolean value to determine if the file was successfully saved
     */
    public boolean saveBitmap (Bitmap bmp) {
        File file = new File(captureDirectory, String.format(Locale.getDefault(), "webcam-frame-%d.jpg", captureCounter++));

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            return true;
        } catch(Exception ignored) {}

        return false;
    }

    /**
     * Scale down a bitmap by a percent
     */
    public Bitmap scaleBitmap (Bitmap bmp, double imageCompression) {
        int newWidth = (int) (bmp.getWidth() * imageCompression);
        int newHeight = (int) (bmp.getHeight() * imageCompression);

        return Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
    }

    public void capturePixels() {
        Bitmap frame = getFrame();
        Bitmap scaledFrame = scaleBitmap(frame, this.compression);
        frame.recycle();

        int w = scaledFrame.getWidth();
        int h = scaledFrame.getHeight();

        this.pixels = new int[w*h];
        scaledFrame.getPixels(this.pixels, 0, w, 0, 0, w, h);

        scaledFrame.recycle();
    }

    public int countPixels(PixelSelector callback) {
        if(this.pixels == null || this.pixels.length == 0) {
            capturePixels();
        }

        int count = 0;
        for(int c : this.pixels) {
            if(callback.shouldCount(Color.red(c), Color.blue(c), Color.red(c))) {
                count++;
            }
        }
        return count;
    }


}