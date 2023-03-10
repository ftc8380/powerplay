package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.Vector;

@Autonomous
public class AutoTest extends LinearOpMode {
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Sliders v4b = new Sliders(hardwareMap);
        Pose2d startPose = new Pose2d();

        double[] v4bPositions = {0.14, 0.11, 0.1, 0.08, 0.06, 0};
        TrajectorySequence traj = drive.trajectorySequenceBuilder(startPose)
                //close the claw on the preloaded cone
                .addTemporalMarker(v4b::closeClaw)
                .addTemporalMarker(() -> {
                    v4b.v4b(0.25);
                })
                .waitSeconds(1.5)
                .lineToLinearHeading(new Pose2d(10, 0, Math.toRadians(-90)))
                .lineTo(new Vector2d(72, 4))
                .addTemporalMarker(v4b::highJunction)
                .waitSeconds(1.5)
                .addTemporalMarker(v4b::openClaw)
                .waitSeconds(0.5)
                .addTemporalMarker(v4b::closeClaw)
                .waitSeconds(0.5)
                .lineTo(new Vector2d(57, 0))
                .addTemporalMarker(() -> {
                    v4b.motorGrouping(0);
                    v4b.v4b(v4bPositions[0]);
                })
                //I dont even know if its right or left tbh or the length lets change
                .build();
        waitForStart();

        drive.setPoseEstimate(startPose);
        drive.followTrajectorySequence(traj);

        for(int i = 1; i < 6; i++) {
            int finalI = i;
            TrajectorySequence trajLoop = drive.trajectorySequenceBuilder(startPose)
                    //I dont know if its neccesary to add 2 temporalmarkers but I saw you do it so I guess
                    .addTemporalMarker(v4b::openClaw)
                    .forward(18 + 0.02*finalI)
                    .addTemporalMarker(v4b::closeClaw)
                    .waitSeconds(0.5)
                    .addTemporalMarker(() -> v4b.v4b(0.3))
                    .back(20)
                    .lineTo(new Vector2d(-3, 15))
                    .addTemporalMarker(v4b::highJunction)
                    .waitSeconds(1.5)
                    .addTemporalMarker(v4b::openClaw)
                    .waitSeconds(0.5)
                    .addTemporalMarker(v4b::closeClaw)
                    .waitSeconds(0.5)
                    .lineTo(new Vector2d(0, 0))
                    .addTemporalMarker(() -> {
                        v4b.motorGrouping(0);
                        v4b.v4b(v4bPositions[finalI]);
                    })
                    .build();

            drive.setPoseEstimate(startPose);
            drive.followTrajectorySequence(trajLoop);
        }

    }
}
