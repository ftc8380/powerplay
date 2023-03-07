package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
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

        double[] v4bPositions = {0.15, 0.14, 0.11, 0.1, 0.08, 0.06};
        TrajectorySequence traj = drive.trajectorySequenceBuilder(startPose)
                //close the claw on the preloaded cone
                .addTemporalMarker(v4b::closeClaw)
                .waitSeconds(1)
                .forward(50)
                .turn(Math.toRadians(-135))
                .back(10)
                .addTemporalMarker(v4b::highJunction)
                .waitSeconds(3)
                .addTemporalMarker(v4b::openClaw)
                .waitSeconds(1)
                .addTemporalMarker(v4b::closeClaw)
                .waitSeconds(1)
                .forward(10)
                .addTemporalMarker(() -> {
                    v4b.motorGrouping(0);
                    v4b.v4b(v4bPositions[0]);
                })
                .turn(Math.toRadians(45))
                //I dont even know if its right or left tbh or the length lets change
                .build();
        waitForStart();

        drive.setPoseEstimate(startPose);
        drive.followTrajectorySequence(traj);

        for(int i = 1; i < 6; i++) {
            int finalI = i;
            TrajectorySequence trajLoop = drive.trajectorySequenceBuilder(startPose)
                    //I dont know if its neccesary to add 2 temporalmarkers but I saw you do it so I guess
                    .forward(15)
                    .addTemporalMarker(v4b::closeClaw)
                    .waitSeconds(1)
                    .addTemporalMarker(() -> v4b.v4b(0.3))
                    .back(15)
                    .turn(Math.toRadians(-45))
                    .back(10)
                    .addTemporalMarker(v4b::highJunction)
                    .waitSeconds(3)
                    .addTemporalMarker(v4b::openClaw)
                    .waitSeconds(1)
                    .addTemporalMarker(v4b::closeClaw)
                    .waitSeconds(1)
                    .forward(10)
                    .addTemporalMarker(() -> {
                        v4b.motorGrouping(0);
                        v4b.v4b(v4bPositions[finalI]);
                    })
                    .turn(Math.toRadians(45))
                    .build();

            drive.setPoseEstimate(startPose);
            drive.followTrajectorySequence(trajLoop);
        }

    }
}
