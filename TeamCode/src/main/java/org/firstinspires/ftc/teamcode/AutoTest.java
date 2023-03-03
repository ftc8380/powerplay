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
        float robotLength = 0;
        Pose2d startPose = new Pose2d();
        TrajectorySequence traj = drive.trajectorySequenceBuilder(startPose)
                //close the claw on the preloaded cone
                .addTemporalMarker(() -> {
                    v4b.closeClaw();
                })
                .waitSeconds(1)
                .forward(72 - robotLength/2)
                .turn(Math.toRadians(-90))
                .back(8)
                .addTemporalMarker(() ->{
                    v4b.highJunction();
                })
                .waitSeconds(3)
                .addTemporalMarker(() ->{
                    v4b.openClaw();
                })
                .waitSeconds(1)
                .addTemporalMarker(() -> {
                    v4b.floor();
                })
                .waitSeconds(3)
                .forward(8)
                //I dont even know if its right or left tbh or the length lets change
                .strafeRight(12)
                .back(24)
                .build();
        //like hell it will be 5
        for(int i = 0; i < 5; i++) {
            //dont worry about it
            int finalI = i;
            //putting this in a loop is it even halal? Will it work
            TrajectorySequence trajLoop = drive.trajectorySequenceBuilder(startPose)
                    .forward(50)
                    .turn(Math.toRadians(-90))
                    .forward(24)
                    //I dont know if its neccesary to add 2 temporalmarkers but I saw you do it so I guess
                    .addTemporalMarker(() -> {
                        //these values are bullshit I just want to go to sleep also I think doing it with the slides is more optimal but your call
                        v4b.motorGrouping(1 - (finalI *0.05));
                    })
                    .waitSeconds(2)
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
        }


        waitForStart();
        v4b.openClaw();
        drive.followTrajectorySequence(traj);
        //do I have to do this multiple times if its looped. Also fix this error I want to go to sleep
        drive.followTrajectorySequence(trajLoop);
    }
}
