package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous
public class AutoTest extends LinearOpMode {
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Sliders v4b = new Sliders(hardwareMap);

        Pose2d startPose = new Pose2d();
        TrajectorySequence traj = drive.trajectorySequenceBuilder(startPose)
                .forward(50)
                .turn(Math.toRadians(-90))
                .forward(24)
                .addTemporalMarker(() -> {
                    v4b.closeClaw();
                })
                .waitSeconds(1)
                .back(24)
                .turn(Math.toRadians(-45))
                .back(8)
                .addTemporalMarker(() -> {
                    v4b.highJunction();
                })
                .waitSeconds(3)
                .addTemporalMarker(() -> {
                    v4b.openClaw();
                })
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    v4b.floor();
                })
                .waitSeconds(5)
                .build();


        waitForStart();
        v4b.openClaw();
        drive.followTrajectorySequence(traj);
    }
}
