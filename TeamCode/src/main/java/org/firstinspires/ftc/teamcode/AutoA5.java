package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

/**
 * For copying this OpMode you really just want to change startpose and trajectories 1-3
 * Leave everything else as-is
 */
public class AutoA5 extends LinearOpMode {

    public void runOpMode() {
        Pose2d startPose = new Pose2d(36, 59, Math.toRadians(-90));

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setPoseEstimate(startPose);

        // Create a new TrajectorySequence for every possible parking spot
        TrajectorySequence location1 = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(60, 59, Math.toRadians(-90)))
                .lineToLinearHeading(new Pose2d(60, 36, Math.toRadians(-90)))
                .build();

        TrajectorySequence location2 = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(36, 24, Math.toRadians(-90)))
                .build();

        TrajectorySequence location3 = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(12, 59, Math.toRadians(-90)))
                .lineToLinearHeading(new Pose2d(12, 24, Math.toRadians(-90)))
                .build();

        WebcamHelper webcam = new WebcamHelper("Webcam 1", hardwareMap);
        webcam.setCompression(0.25);
        webcam.capturePixels();

        int numRedPixels = webcam.countPixels((r, g, b) -> r > g && r > b);
        int numGreenPixels = webcam.countPixels((r, g, b) -> g > r && g > b);
        int numBluePixels = webcam.countPixels((r, g, b) -> b > r && b > g);

        webcam.stopCamera();

        // Cone red
        if(numRedPixels > numGreenPixels && numRedPixels > numBluePixels) {
            drive.followTrajectorySequence(location1);

        // Cone blue
        } else if(numBluePixels > numGreenPixels && numBluePixels > numRedPixels) {
            drive.followTrajectorySequence(location3);

        // Cone green or anything else
        } else {
            drive.followTrajectorySequence(location2);
        }

    }
}
