package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.util.AprilTagDetectionPipeline;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

/* For copying this OpMode you really just want to change startpose and trajectories 1-3
 Leave everything else as-is */
@Disabled
public class AutoF2Park extends LinearOpMode {

    final int LEFT = 1, MIDDLE = 2, RIGHT = 3;
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    SampleMecanumDrive drive;

    public void runOpMode() {

        // setPoseEstimate is very important so robot knows where it's starting
        Pose2d startPose = new Pose2d(-34, -61, Math.toRadians(90));
        drive = new SampleMecanumDrive(hardwareMap);
        drive.setPoseEstimate(startPose);

        // Create new TrajectorySequence for every possible parking spot
        TrajectorySequence location1 = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(-60, -61, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(-60, -24, Math.toRadians(90)))
                .build();
 
        TrajectorySequence location2 = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(-34, -21, Math.toRadians(90)))
                .build();


        TrajectorySequence location3 = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(-12.5, -61, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(-12.5, -19, Math.toRadians(90)))
                .build();

        // Connecting to webcam shouldn't be this verbose but it is
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        // These numbers are camera specifics that don't matter, just make detection pipeline & attach it to camera
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(0.166, 578.272, 578.272, 402.145, 221.506);
        camera.setPipeline(aprilTagDetectionPipeline);

        // Now open camera & if it works, begin streaming 800x448px images
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(800, 448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        // -1 if no tag found, to be handled by else or default block
        int detectedTag = -1;

        // Runs in a loop between INIT and PLAY
        while (!isStarted() && !isStopRequested()) {
            ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getLatestDetections();

            for (AprilTagDetection detection : detections) {
                if (detection.id == LEFT || detection.id == MIDDLE || detection.id == RIGHT) {
                    detectedTag = detection.id;
                    telemetry.addData("AprilTag ID", detectedTag);
                    break;
                }
            }
        }

        // User hit PLAY, let's go
        if (detectedTag == LEFT) {
            drive.followTrajectorySequence(location1);
        } else if (detectedTag == RIGHT) {
            drive.followTrajectorySequence(location3);

        // Default to position 2 if AprilTag not found
        } else {
            drive.followTrajectorySequence(location2);
        }

    }
}