package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
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
@Autonomous
public class AutoConeRight extends LinearOpMode {

    final int LEFT = 1, MIDDLE = 2, RIGHT = 3;
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    SampleMecanumDrive drive;

    public void runOpMode() {

        // setPoseEstimate is very important so robot knows where it's starting
        Pose2d startPose = new Pose2d();
        drive = new SampleMecanumDrive(hardwareMap);
        drive.setPoseEstimate(startPose);

        DcMotor motorLift = hardwareMap.dcMotor.get("motor lift");
        motorLift.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setTargetPosition(0);
        motorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Servo servoIntake = hardwareMap.servo.get("servo intake");

        TrajectorySequence goToHighJunction = drive.trajectorySequenceBuilder(startPose)
                .forward(28)
                .strafeLeft(40)
                .addTemporalMarker(() -> {
                    motorLift.setTargetPosition(rotationsToTicks(7.2));
                })
                .waitSeconds(2)
                .forward(4,
                        SampleMecanumDrive.getVelocityConstraint(7, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(7))
                .waitSeconds(5)
                .addTemporalMarker(() -> {
                    servoIntake.setPosition(0);
                })
                .waitSeconds(2)
                .back(4,
                        SampleMecanumDrive.getVelocityConstraint(7, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(7))
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    motorLift.setTargetPosition(0);
                })
                .build();

        // Create new TrajectorySequence for every possible parking spot
        TrajectorySequence location1 = drive.trajectorySequenceBuilder(goToHighJunction.end())
                .strafeRight(64)
                .build();

        TrajectorySequence location2 = drive.trajectorySequenceBuilder(goToHighJunction.end())
                .strafeRight(40)
                .build();

        TrajectorySequence location3 = drive.trajectorySequenceBuilder(goToHighJunction.end())
                .strafeRight(16)
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
        servoIntake.setPosition(0.5);
        sleep(1000);
        motorLift.setPower(0.8);
        motorLift.setTargetPosition(rotationsToTicks(0.5));
        sleep(1000);

        drive.followTrajectorySequence(goToHighJunction);

        if (detectedTag == LEFT) {
            drive.followTrajectorySequence(location1);
        } else if (detectedTag == RIGHT) {
            drive.followTrajectorySequence(location3);

            // Default to position 2 if AprilTag not found
        } else {
            drive.followTrajectorySequence(location2);
        }

    }

    // Used for lift, motor rotations --> encoder ticks
    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }
}
